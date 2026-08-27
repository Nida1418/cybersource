package com.concord.catalogservice.service;

import com.concord.catalogservice.entity.Copy;
import com.concord.catalogservice.entity.CopyStatus;
import com.concord.catalogservice.repository.BookRepository;
import com.concord.catalogservice.repository.CopyRepository;
import org.springframework.stereotype.Service;

@Service
public class CopyService {

    private final CopyRepository copyRepository;
    private final BookRepository bookRepository;

    public CopyService(CopyRepository copyRepository, BookRepository bookRepository) {
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
    }

    public Copy createCopy(int bookId, int copyNumber, String location) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));

        Copy copy = new Copy();
        copy.setCopyNumber(copyNumber);
        copy.setLocation(location);
        copy.setBook(book);
        copy.setStatus(CopyStatus.AVAILABLE);
        return copyRepository.save(copy);
    }

    public Copy updateCopyStatus(int copyId, CopyStatus status) {
        var copy = copyRepository.findById(copyId)
                .orElseThrow(() -> new RuntimeException("Copy not found: " + copyId));
        copy.setStatus(status);
        return copyRepository.save(copy);
    }
}