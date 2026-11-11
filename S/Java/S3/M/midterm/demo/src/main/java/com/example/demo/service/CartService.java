package com.example.demo.service;

import com.example.demo.model.CartItem;
import com.example.demo.model.CartLine;
import com.example.demo.repository.CartLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartLineRepository repository;

    public List<CartItem> getCartItems(Long staffId) {
        List<CartItem> items = new ArrayList<>();
        for (CartLine line : repository.findByStaffId(staffId)) {
            items.add(new CartItem(line.getProductId(), line.getProductName(), line.getPrice(), line.getQty()));
        }
        return items;
    }

    public int getCartQty(Long staffId) {
        int total = 0;
        for (CartLine line : repository.findByStaffId(staffId)) {
            total += line.getQty();
        }
        return total;
    }

    public double getCartTotal(Long staffId) {
        double total = 0;
        for (CartLine line : repository.findByStaffId(staffId)) {
            total += line.getPrice() * line.getQty();
        }
        return total;
    }

    public void addItem(Long staffId, Long productId, String productName, double price, int qty) {
        Optional<CartLine> existing = repository.findByStaffIdAndProductId(staffId, productId);
        if (existing.isPresent()) {
            CartLine line = existing.get();
            line.setQty(line.getQty() + qty);
            line.setPrice(price);
            line.setProductName(productName);
            repository.save(line);
        } else {
            repository.save(new CartLine(staffId, productId, productName, price, qty));
        }
    }

    public void updateQty(Long staffId, Long productId, int qty) {
        Optional<CartLine> existing = repository.findByStaffIdAndProductId(staffId, productId);
        if (existing.isEmpty()) return;
        if (qty <= 0) {
            repository.delete(existing.get());
        } else {
            CartLine line = existing.get();
            line.setQty(qty);
            repository.save(line);
        }
    }

    @Transactional
    public void removeItem(Long staffId, Long productId) {
        repository.deleteByStaffIdAndProductId(staffId, productId);
    }

    @Transactional
    public void clearCart(Long staffId) {
        repository.deleteByStaffId(staffId);
    }
}
