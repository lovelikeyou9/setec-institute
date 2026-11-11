package com.example.demo.config;

import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.Staff;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StaffRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final StaffRepository staffRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataSeeder(StaffRepository staffRepository,
                      CategoryRepository categoryRepository,
                      ProductRepository productRepository) {
        this.staffRepository = staffRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (staffRepository.count() == 0) {
            staffRepository.save(new Staff("admin", "admin", "Admin"));
            staffRepository.save(new Staff("stock", "stock", "Stock"));
            staffRepository.save(new Staff("user", "user", "User"));
        } else {
            for (Staff staff : staffRepository.findAll()) {
                if (staff.getRole() == null || staff.getRole().isBlank()) {
                    String name = staff.getUserName() == null ? "" : staff.getUserName().toLowerCase();
                    if (name.contains("admin")) staff.setRole("Admin");
                    else if (name.contains("stock")) staff.setRole("Stock");
                    else staff.setRole("User");
                    staffRepository.save(staff);
                }
            }
        }

        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category("Tablet"));
            categoryRepository.save(new Category("Smartphone"));
            categoryRepository.save(new Category("Game Console"));
            categoryRepository.save(new Category("Camera"));
            categoryRepository.save(new Category("Smartwatch"));
            categoryRepository.save(new Category("Audio"));
            categoryRepository.save(new Category("Computer"));
            categoryRepository.save(new Category("Drone"));
        }

        if (productRepository.count() == 0) {
            productRepository.save(new Product("MacBook Air M1 2020", 20, 999.00, LocalDate.of(2027, 1, 15), 7L));
            productRepository.save(new Product("iPhone 15 Pro", 30, 1099.00, LocalDate.of(2026, 12, 1), 2L));
            productRepository.save(new Product("Galaxy Watch 6", 40, 299.00, LocalDate.of(2026, 6, 20), 5L));
            productRepository.save(new Product("iPad Pro 12.9", 15, 1099.00, LocalDate.of(2027, 3, 10), 1L));
            productRepository.save(new Product("Sony WH-1000XM5", 25, 349.00, LocalDate.of(2025, 1, 1), 6L));
            productRepository.save(new Product("PS5 DualSense Controller", 50, 69.00, LocalDate.of(2028, 5, 5), 3L));
            productRepository.save(new Product("Canon EOS R50", 10, 679.00, LocalDate.of(2026, 8, 8), 4L));
            productRepository.save(new Product("HomePod Mini", 35, 99.00, LocalDate.of(2024, 12, 31), 6L));
            productRepository.save(new Product("MacBook Pro 16", 12, 2499.00, LocalDate.of(2027, 9, 9), 7L));
            productRepository.save(new Product("Bamboo Speaker", 18, 79.00, LocalDate.of(2025, 2, 2), 6L));
        }
    }
}
