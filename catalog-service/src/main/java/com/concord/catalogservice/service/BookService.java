package com.concord.catalogservice.service;

import com.concord.catalogservice.dto.BookRequest;
import com.concord.catalogservice.entity.Author;
import com.concord.catalogservice.entity.Book;
import com.concord.catalogservice.entity.Category;
import com.concord.catalogservice.entity.OutboxEvent;
import com.concord.catalogservice.event.BookAddedEvent;
import com.concord.catalogservice.repository.AuthorRepository;
import com.concord.catalogservice.repository.BookRepository;
import com.concord.catalogservice.repository.CategoryRepository;
import com.concord.catalogservice.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, CategoryRepository categoryRepository, OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper, KafkaTemplate<String, BookAddedEvent> kafkaTemplate) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
    }

    @Transactional
    public Book createBook(BookRequest bookRequest) {
        Author author = authorRepository.findById(bookRequest.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found: " + bookRequest.getAuthorId()));
        Category category = categoryRepository.findById(bookRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + bookRequest.getCategoryId()));

        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setIsbn(bookRequest.getIsbn());
        book.setDescription(bookRequest.getDescription());
        book.setAuthor(author);
        book.setCategory(category);

        Book savedBook = bookRepository.save(book);

       try {
            String payload = objectMapper.writeValueAsString(new BookAddedEvent(savedBook.getId(), savedBook.getTitle(), savedBook.getIsbn()));

           OutboxEvent outboxEvent = new OutboxEvent(
                    "Book",
                    String.valueOf(savedBook.getId()),
                    "BookAdded",
                    payload,
                    "catalog-events"
            );

            outboxEventRepository.save(outboxEvent);
       } catch (Exception e) {
           throw new RuntimeException("Failed to serialize outbox event", e);
        }
        return savedBook;
    }
}
