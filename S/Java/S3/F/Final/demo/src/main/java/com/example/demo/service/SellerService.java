package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public Seller findById(Long id) {
        return sellerRepository.findById(id).orElseThrow(() -> new RuntimeException("Seller not found"));
    }

    public Seller findByUserId(Long userId) {
        return sellerRepository.findByUserId(userId).orElse(null);
    }

    public List<Seller> getApprovedSellers() {
        return sellerRepository.findByStatusOrderByRatingDesc(SellerStatus.APPROVED);
    }

    public List<Seller> getPendingApplications() {
        return sellerRepository.findByStatus(SellerStatus.PENDING);
    }

    public List<Seller> findAll() {
        return sellerRepository.findAll();
    }

    public List<Seller> search(String q) {
        return search(q, "newest");
    }

    public List<Seller> search(String q, String sort) {
        List<Seller> list = (q == null || q.isBlank()) ? findAll() : sellerRepository.search(q.trim());
        return list.stream().sorted(sellerComparator(sort)).toList();
    }

    private Comparator<Seller> sellerComparator(String sort) {
        String key = sort == null ? "newest" : sort;
        Comparator<Seller> byIdDesc = Comparator.comparing(Seller::getId, Comparator.nullsLast(Long::compareTo)).reversed();
        return switch (key) {
            case "oldest" -> Comparator.comparing(Seller::getJoinedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Seller::getId, Comparator.nullsLast(Long::compareTo));
            case "name_asc" -> Comparator.comparing(Seller::getStoreName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                    .thenComparing(byIdDesc);
            case "name_desc" -> Comparator.comparing(Seller::getStoreName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)).reversed()
                    .thenComparing(byIdDesc);
            case "owner_asc" -> Comparator.comparing(Seller::getOwnerName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                    .thenComparing(byIdDesc);
            case "rating_desc" -> Comparator.comparing((Seller s) -> s.getRating() != null ? s.getRating() : 0.0).reversed()
                    .thenComparing(byIdDesc);
            case "products_desc" -> Comparator.comparing((Seller s) -> s.getTotalProducts() != null ? s.getTotalProducts() : 0).reversed()
                    .thenComparing(byIdDesc);
            case "status" -> Comparator.comparing((Seller s) -> s.getStatus() != null ? s.getStatus().name() : "", String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(byIdDesc);
            default -> Comparator.comparing(Seller::getJoinedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed()
                    .thenComparing(byIdDesc);
        };
    }

    @Transactional
    public Seller apply(User user, String storeName, String ownerName, String phone,
                        String address, String location, String businessInfo,
                        String description, String idDocument) {
        return apply(user, storeName, ownerName, phone, address, location, null,
                businessInfo, description, idDocument);
    }

    @Transactional
    public Seller apply(User user, String storeName, String ownerName, String phone,
                        String address, String location, String country, String businessInfo,
                        String description, String idDocument) {
        if (user == null) {
            throw new RuntimeException("Please register or log in as a customer before applying to become a seller");
        }
        if (sellerRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("You already have a seller application");
        }
        if (country == null || country.isBlank()) {
            throw new RuntimeException("Country is required");
        }
        Seller seller = Seller.builder()
                .user(user)
                .storeName(storeName)
                .ownerName(ownerName)
                .phone(phone)
                .address(address)
                .location(location)
                .country(country.trim())
                .businessInfo(businessInfo)
                .description(description)
                .idDocument(idDocument)
                .status(SellerStatus.PENDING)
                .level(SellerLevel.NEW)
                .logoUrl("https://ui-avatars.com/api/?name=" + storeName.replace(" ", "+") + "&background=0d7377&color=fff&size=128")
                .bannerUrl("https://images.unsplash.com/photo-1587831990711-23ca6441447b?w=1200&h=300&fit=crop")
                .build();
        return sellerRepository.save(seller);
    }

    @Transactional
    public Seller approve(Long sellerId) {
        Seller seller = findById(sellerId);
        seller.setStatus(SellerStatus.APPROVED);
        seller.setLevel(SellerLevel.VERIFIED);
        User user = seller.getUser();
        user.setRole(Role.SELLER);
        userRepository.save(user);
        sellerRepository.save(seller);

        notificationRepository.save(Notification.builder()
                .user(user)
                .title("Seller Application Approved")
                .message("Congratulations! Your store \"" + seller.getStoreName() + "\" has been approved.")
                .type("SELLER")
                .link("/seller-dashboard")
                .build());
        return seller;
    }

    @Transactional
    public Seller reject(Long sellerId, String reason) {
        Seller seller = findById(sellerId);
        seller.setStatus(SellerStatus.REJECTED);
        seller.setRejectionReason(reason);
        sellerRepository.save(seller);

        notificationRepository.save(Notification.builder()
                .user(seller.getUser())
                .title("Seller Application Rejected")
                .message("Your application was rejected. Reason: " + reason)
                .type("SELLER")
                .link("/become-seller")
                .build());
        return seller;
    }

    @Transactional
    public Seller updateLevel(Long sellerId, SellerLevel level) {
        Seller seller = findById(sellerId);
        seller.setLevel(level);
        return sellerRepository.save(seller);
    }

    @Transactional
    public Seller ban(Long sellerId) {
        Seller seller = findById(sellerId);
        seller.setStatus(SellerStatus.SUSPENDED);
        User user = seller.getUser();
        user.setEnabled(false);
        userRepository.save(user);
        sellerRepository.save(seller);

        notificationRepository.save(Notification.builder()
                .user(user)
                .title("Store Suspended")
                .message("Your seller account \"" + seller.getStoreName() + "\" has been banned by the marketplace admin.")
                .type("SELLER")
                .link("/account")
                .build());
        return seller;
    }

    @Transactional
    public Seller unban(Long sellerId) {
        Seller seller = findById(sellerId);
        seller.setStatus(SellerStatus.APPROVED);
        User user = seller.getUser();
        user.setEnabled(true);
        if (user.getRole() != Role.SELLER && user.getRole() != Role.ADMIN) {
            user.setRole(Role.SELLER);
        }
        userRepository.save(user);
        sellerRepository.save(seller);

        notificationRepository.save(Notification.builder()
                .user(user)
                .title("Store Restored")
                .message("Your seller account \"" + seller.getStoreName() + "\" has been unbanned. You can sell again.")
                .type("SELLER")
                .link("/seller-dashboard")
                .build());
        return seller;
    }

    /** Admin creates an approved seller + login account. */
    @Transactional
    public Seller createByAdmin(String storeName, String ownerName, String email, String phone,
                                String password, String location, String address, String level) {
        return createByAdmin(storeName, ownerName, email, phone, password, location, address, null, level);
    }

    @Transactional
    public Seller createByAdmin(String storeName, String ownerName, String email, String phone,
                                String password, String location, String address, String country,
                                String level) {
        if (storeName == null || storeName.isBlank()) throw new RuntimeException("Store name is required");
        if (email == null || email.isBlank()) throw new RuntimeException("Email is required");
        if (password == null || password.isBlank()) throw new RuntimeException("Password is required");
        if (userRepository.existsByEmail(email.trim().toLowerCase())) {
            throw new RuntimeException("Email already registered");
        }
        User user = userRepository.save(User.builder()
                .fullName(ownerName != null && !ownerName.isBlank() ? ownerName.trim() : storeName.trim())
                .email(email.trim().toLowerCase())
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .role(Role.SELLER)
                .enabled(true)
                .build());
        SellerLevel sellerLevel = SellerLevel.VERIFIED;
        if (level != null && !level.isBlank()) {
            sellerLevel = SellerLevel.valueOf(level);
        }
        String resolvedCountry = (country != null && !country.isBlank())
                ? country.trim()
                : CountryResolver.resolve(address, location, null);
        if ("Unknown".equals(resolvedCountry) || "Other".equals(resolvedCountry)) {
            resolvedCountry = null;
        }
        return sellerRepository.save(Seller.builder()
                .user(user)
                .storeName(storeName.trim())
                .ownerName(ownerName != null && !ownerName.isBlank() ? ownerName.trim() : storeName.trim())
                .phone(phone)
                .location(location)
                .address(address)
                .country(resolvedCountry)
                .status(SellerStatus.APPROVED)
                .level(sellerLevel)
                .logoUrl("https://ui-avatars.com/api/?name=" + storeName.trim().replace(" ", "+")
                        + "&background=0d7377&color=fff&size=128")
                .bannerUrl("https://images.unsplash.com/photo-1587831990711-23ca6441447b?w=1200&h=300&fit=crop")
                .totalProducts(0)
                .build());
    }

    @Transactional
    public Seller updateByAdmin(Long id, String storeName, String ownerName, String phone,
                                String location, String address, String status, String level) {
        return updateByAdmin(id, storeName, ownerName, phone, location, address, null, status, level);
    }

    @Transactional
    public Seller updateByAdmin(Long id, String storeName, String ownerName, String phone,
                                String location, String address, String country,
                                String status, String level) {
        Seller seller = findById(id);
        if (storeName != null && !storeName.isBlank()) seller.setStoreName(storeName.trim());
        if (ownerName != null && !ownerName.isBlank()) seller.setOwnerName(ownerName.trim());
        if (phone != null) seller.setPhone(phone.trim());
        if (location != null) seller.setLocation(location.trim());
        if (address != null) seller.setAddress(address.trim());
        if (country != null && !country.isBlank()) seller.setCountry(country.trim());
        if (status != null && !status.isBlank()) {
            seller.setStatus(SellerStatus.valueOf(status));
            User user = seller.getUser();
            if (seller.getStatus() == SellerStatus.APPROVED) {
                user.setEnabled(true);
                if (user.getRole() != Role.ADMIN) user.setRole(Role.SELLER);
            } else if (seller.getStatus() == SellerStatus.SUSPENDED) {
                user.setEnabled(false);
            }
            userRepository.save(user);
        }
        if (level != null && !level.isBlank()) {
            seller.setLevel(SellerLevel.valueOf(level));
        }
        return sellerRepository.save(seller);
    }

    @Transactional
    public void deleteByAdmin(Long id) {
        Seller seller = findById(id);
        User user = seller.getUser();
        try {
            sellerRepository.delete(seller);
            if (user.getRole() == Role.SELLER) {
                user.setRole(Role.CUSTOMER);
                userRepository.save(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete seller — products or orders still linked. Ban/suspend instead.");
        }
    }

    @Transactional
    public Seller updateStoreProfile(Long sellerId, String storeName, String ownerName, String phone,
                                     String location, String address, String description) {
        return updateStoreProfile(sellerId, storeName, ownerName, phone, location, address, null,
                description, null, null);
    }

    @Transactional
    public Seller updateStoreProfile(Long sellerId, String storeName, String ownerName, String phone,
                                     String location, String address, String description,
                                     String logoUrl, String bannerUrl) {
        return updateStoreProfile(sellerId, storeName, ownerName, phone, location, address, null,
                description, logoUrl, bannerUrl);
    }

    @Transactional
    public Seller updateStoreProfile(Long sellerId, String storeName, String ownerName, String phone,
                                     String location, String address, String country, String description,
                                     String logoUrl, String bannerUrl) {
        Seller seller = findById(sellerId);
        if (storeName != null && !storeName.isBlank()) seller.setStoreName(storeName.trim());
        if (ownerName != null && !ownerName.isBlank()) seller.setOwnerName(ownerName.trim());
        if (phone != null) seller.setPhone(phone.trim());
        if (location != null) seller.setLocation(location.trim());
        if (address != null) seller.setAddress(address.trim());
        if (country != null && !country.isBlank()) seller.setCountry(country.trim());
        if (description != null) seller.setDescription(description.trim());
        if (logoUrl != null && !logoUrl.isBlank()) seller.setLogoUrl(logoUrl.trim());
        if (bannerUrl != null && !bannerUrl.isBlank()) seller.setBannerUrl(bannerUrl.trim());
        return sellerRepository.save(seller);
    }
}
