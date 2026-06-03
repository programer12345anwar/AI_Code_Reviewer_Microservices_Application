package com.aicodereviewer.review.repository;

import com.aicodereviewer.review.entity.ReviewRecord;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewRecord, Long> {
    Optional<ReviewRecord> findByRequestId(String requestId);
    Page<ReviewRecord> findAllByRepositoryContainingIgnoreCase(String repository, Pageable pageable);
}
