package com.example.demo.repository;

import com.example.demo.model.CartLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartLineRepository extends JpaRepository<CartLine, Long> {
    List<CartLine> findByStaffId(Long staffId);
    Optional<CartLine> findByStaffIdAndProductId(Long staffId, Long productId);
    void deleteByStaffId(Long staffId);
    void deleteByStaffIdAndProductId(Long staffId, Long productId);
}
