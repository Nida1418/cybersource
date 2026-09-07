package com.concord.catalogservice.controller;

import com.concord.catalogservice.dto.BookRequest;
import com.concord.catalogservice.entity.Book;
import com.concord.catalogservice.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable int id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public Book createBook(@RequestBody BookRequest request) {
        return bookService.createBook(request);
    }
}
