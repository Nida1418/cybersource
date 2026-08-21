package com.concord.catalogservice.repository;

import com.concord.catalogservice.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
}
