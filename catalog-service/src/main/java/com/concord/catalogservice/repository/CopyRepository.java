package com.concord.catalogservice.repository;

import com.concord.catalogservice.entity.Copy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CopyRepository extends JpaRepository<Copy, Integer> {
}
