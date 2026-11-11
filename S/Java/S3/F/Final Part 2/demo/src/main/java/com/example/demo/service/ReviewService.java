package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdAndHiddenFalseOrderByCreatedAtDesc(productId);
    }

    @Transactional
    public Review addReview(User user, Product product, int rating, String comment, boolean verified) {
        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(rating)
                .comment(comment)
                .verifiedPurchase(verified)
                .build();
        review = reviewRepository.save(review);
        updateProductRating(product);
        return review;
    }

    @Transactional
    public void sellerRespond(Long reviewId, String response) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        review.setSellerResponse(response);
        review.setSellerRespondedAt(java.time.LocalDateTime.now());
        reviewRepository.save(review);
    }

    @Transactional
    public void hide(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        review.setHidden(true);
        reviewRepository.save(review);
        updateProductRating(review.getProduct());
    }

    private void updateProductRating(Product product) {
        List<Review> reviews = reviewRepository.findByProductIdAndHiddenFalseOrderByCreatedAtDesc(product.getId());
        if (reviews.isEmpty()) {
            product.setRating(0.0);
            product.setReviewCount(0);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
            product.setRating(Math.round(avg * 10.0) / 10.0);
            product.setReviewCount(reviews.size());
        }
        productRepository.save(product);
    }
}
