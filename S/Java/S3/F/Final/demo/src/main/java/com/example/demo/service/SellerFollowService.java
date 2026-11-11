package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.SellerFollowRepository;
import com.example.demo.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerFollowService {

    private final SellerFollowRepository sellerFollowRepository;
    private final SellerRepository sellerRepository;

    public long countFollowers(Long sellerId) {
        return sellerFollowRepository.countBySellerId(sellerId);
    }

    public boolean isFollowing(Long userId, Long sellerId) {
        if (userId == null || sellerId == null) return false;
        return sellerFollowRepository.existsByUserIdAndSellerId(userId, sellerId);
    }

    /** Toggle follow. Returns true if now following. */
    @Transactional
    public boolean toggle(User user, Long sellerId) {
        if (user == null) throw new RuntimeException("Please log in to follow a store");
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        if (seller.getStatus() != SellerStatus.APPROVED) {
            throw new RuntimeException("This store cannot be followed");
        }
        if (seller.getUser() != null && seller.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot follow your own store");
        }

        var existing = sellerFollowRepository.findByUserIdAndSellerId(user.getId(), sellerId);
        if (existing.isPresent()) {
            sellerFollowRepository.delete(existing.get());
            syncFollowerCount(seller);
            return false;
        }
        sellerFollowRepository.save(SellerFollow.builder()
                .user(user)
                .seller(seller)
                .build());
        syncFollowerCount(seller);
        return true;
    }

    @Transactional
    public void syncFollowerCount(Seller seller) {
        long n = sellerFollowRepository.countBySellerId(seller.getId());
        seller.setFollowers((int) Math.min(n, Integer.MAX_VALUE));
        sellerRepository.save(seller);
    }

    @Transactional
    public void syncAllFollowerCounts() {
        for (Seller s : sellerRepository.findAll()) {
            syncFollowerCount(s);
        }
    }
}
