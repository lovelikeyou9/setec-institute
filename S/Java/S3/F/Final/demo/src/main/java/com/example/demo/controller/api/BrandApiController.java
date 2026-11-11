package com.example.demo.controller.api;

import com.example.demo.DTO.BrandRequest;
import com.example.demo.DTO.BrandResponse;
import com.example.demo.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandApiController {

    private final BrandService brandService;

    @GetMapping
    public List<BrandResponse> list() {
        return brandService.findAll().stream().map(BrandResponse::from).toList();
    }

    @GetMapping("/{id}")
    public BrandResponse get(@PathVariable Long id) {
        return BrandResponse.from(brandService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> create(@RequestBody BrandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BrandResponse.from(brandService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BrandResponse update(@PathVariable Long id, @RequestBody BrandRequest request) {
        return BrandResponse.from(brandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
