package com.concord.catalogservice.service;

import com.concord.catalogservice.entity.Author;
import com.concord.catalogservice.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author getAuthorById(int id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found: " + id));
    }

    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Author updateAuthor(int id, Author author) {
        Author existingAuthor = getAuthorById(id);
        existingAuthor.setName(author.getName());
        return authorRepository.save(existingAuthor);
    }

}

