package com.aicodereviewer.review.service;

import com.aicodereviewer.review.dto.ReviewAnalyticsResponse;
import com.aicodereviewer.review.dto.ReviewDetailResponse;
import com.aicodereviewer.review.dto.ReviewSummaryResponse;
import com.aicodereviewer.review.entity.ReviewRecord;
import com.aicodereviewer.review.repository.ReviewRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;

    public Page<ReviewSummaryResponse> list(int page, int size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewRecord> records = (search == null || search.isBlank())
            ? reviewRepository.findAll(pageRequest)
            : reviewRepository.findAllByRepositoryContainingIgnoreCase(search, pageRequest);

        return records.map(record -> new ReviewSummaryResponse(
            record.getId(),
            record.getRequestId(),
            record.getRepository(),
            record.getPrNumber(),
            record.getQualityScore(),
            record.getModelUsed(),
            record.getCreatedAt()
        ));
    }

    public ReviewDetailResponse get(Long id) {
        ReviewRecord record = reviewRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Review not found"));

        return new ReviewDetailResponse(
            record.getId(),
            record.getRequestId(),
            record.getRepository(),
            record.getPrNumber(),
            record.getQualityScore(),
            record.getModelUsed(),
            record.getSummary(),
            record.getNormalizedDiff(),
            record.getResultJson(),
            record.getCreatedAt()
        );
    }

    public ReviewAnalyticsResponse analytics() {
        long total = reviewRepository.count();
        if (total == 0) {
            return new ReviewAnalyticsResponse(0, 0, 0, 0);
        }

        var all = reviewRepository.findAll();
        double average = all.stream().mapToInt(ReviewRecord::getQualityScore).average().orElse(0.0);
        long highRisk = all.stream().filter(review -> review.getQualityScore() < 60).count();
        long lowRisk = all.stream().filter(review -> review.getQualityScore() >= 80).count();
        return new ReviewAnalyticsResponse(total, average, highRisk, lowRisk);
    }
}
