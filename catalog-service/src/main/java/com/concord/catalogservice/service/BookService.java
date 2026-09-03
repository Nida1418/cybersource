package com.concord.catalogservice.service;

import com.concord.catalogservice.entity.Author;
import com.concord.catalogservice.entity.Book;
import com.concord.catalogservice.entity.Category;
import com.concord.catalogservice.event.BookAddedEvent;
import com.concord.catalogservice.repository.AuthorRepository;
import com.concord.catalogservice.repository.BookRepository;
import com.concord.catalogservice.repository.CategoryRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final KafkaTemplate<String, BookAddedEvent> kafkaTemplate;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, CategoryRepository categoryRepository, KafkaTemplate<String, BookAddedEvent> kafkaTemplate) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
    }

    public Book createBook(int authorId, int categoryId, String title, String isbn, String description) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found: " + authorId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setDescription(description);
        book.setAuthor(author);
        book.setCategory(category);

        Book savedBook = bookRepository.save(book);

        // Publish the BookAddedEvent to Kafka
        BookAddedEvent event = new BookAddedEvent(savedBook.getId(), savedBook.getTitle(), savedBook.getIsbn());
        kafkaTemplate.send("catalog-events",String.valueOf(savedBook.getId()),event);

        return savedBook;
    }
}
