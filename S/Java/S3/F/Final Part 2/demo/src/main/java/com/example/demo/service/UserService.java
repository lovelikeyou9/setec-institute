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
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;

    public User findByEmail(String email) {
        String lookup = email == null ? "" : email.trim();
        return userRepository.findByEmailIgnoreCase(lookup)
                .or(() -> userRepository.findByEmail(lookup))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User register(String fullName, String email, String phone, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> search(String q) {
        return search(q, null, "newest");
    }

    public List<User> search(String q, Role role, String sort) {
        List<User> list = (q == null || q.isBlank()) ? findAll() : userRepository.search(q.trim());
        if (role != null) {
            list = list.stream().filter(u -> u.getRole() == role).toList();
        }
        return list.stream().sorted(userComparator(sort)).toList();
    }

    private Comparator<User> userComparator(String sort) {
        String key = sort == null ? "newest" : sort;
        Comparator<User> byIdDesc = Comparator.comparing(User::getId, Comparator.nullsLast(Long::compareTo)).reversed();
        return switch (key) {
            case "oldest" -> Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(User::getId, Comparator.nullsLast(Long::compareTo));
            case "name_asc" -> Comparator.comparing(User::getFullName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                    .thenComparing(byIdDesc);
            case "name_desc" -> Comparator.comparing(User::getFullName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)).reversed()
                    .thenComparing(byIdDesc);
            case "email_asc" -> Comparator.comparing(User::getEmail, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                    .thenComparing(byIdDesc);
            case "email_desc" -> Comparator.comparing(User::getEmail, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)).reversed()
                    .thenComparing(byIdDesc);
            case "role" -> Comparator.comparing((User u) -> u.getRole() != null ? u.getRole().name() : "", String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(byIdDesc);
            default -> Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed()
                    .thenComparing(byIdDesc);
        };
    }

    public List<Address> getAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByDefaultAddressDescCreatedAtDesc(userId);
    }

    @Transactional
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    @Transactional
    public User setEnabled(Long userId, boolean enabled) {
        User user = findById(userId);
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot ban or unban an admin account");
        }
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    @Transactional
    public User create(String fullName, String email, String phone, String password, Role role, boolean enabled) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        if (fullName == null || fullName.isBlank()) throw new RuntimeException("Name is required");
        if (email == null || email.isBlank()) throw new RuntimeException("Email is required");
        if (password == null || password.isBlank()) throw new RuntimeException("Password is required");
        return userRepository.save(User.builder()
                .fullName(fullName.trim())
                .email(email.trim().toLowerCase())
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .role(role != null ? role : Role.CUSTOMER)
                .enabled(enabled)
                .build());
    }

    @Transactional
    public User update(Long id, String fullName, String email, String phone, String password, Role role, Boolean enabled) {
        User user = findById(id);
        if (user.getRole() == Role.ADMIN && role != null && role != Role.ADMIN) {
            throw new RuntimeException("Cannot change admin role");
        }
        if (fullName != null && !fullName.isBlank()) user.setFullName(fullName.trim());
        if (email != null && !email.isBlank()) {
            String newEmail = email.trim().toLowerCase();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email already registered");
            }
            user.setEmail(newEmail);
        }
        if (phone != null) user.setPhone(phone.trim());
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        if (role != null && user.getRole() != Role.ADMIN) {
            user.setRole(role);
        }
        if (enabled != null && user.getRole() != Role.ADMIN) {
            user.setEnabled(enabled);
        }
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot delete an admin account");
        }
        try {
            userRepository.delete(user);
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete user — linked orders, seller, or cart data exist. Ban instead.");
        }
    }

    @Transactional
    public User updateProfile(Long id, String fullName, String phone) {
        return updateProfile(id, fullName, phone, null, null);
    }

    @Transactional
    public User updateProfile(Long id, String fullName, String phone, String avatarUrl) {
        return updateProfile(id, fullName, phone, avatarUrl, null);
    }

    @Transactional
    public User updateProfile(Long id, String fullName, String phone, String avatarUrl, String coverUrl) {
        User user = findById(id);
        if (fullName != null && !fullName.isBlank()) user.setFullName(fullName.trim());
        if (phone != null) user.setPhone(phone.trim());
        if (avatarUrl != null && !avatarUrl.isBlank()) {
            user.setAvatarUrl(avatarUrl.trim());
        }
        if (coverUrl != null && !coverUrl.isBlank()) {
            user.setCoverUrl(coverUrl.trim());
        }
        return userRepository.save(user);
    }

    @Transactional
    public Address updateAddress(Long userId, Long addressId, String fullName, String phone,
                                 String province, String district, String commune, String street) {
        return updateAddress(userId, addressId, fullName, phone, province, district, commune, street, null);
    }

    @Transactional
    public Address updateAddress(Long userId, Long addressId, String fullName, String phone,
                                 String province, String district, String commune, String street,
                                 String country) {
        Address addr = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!addr.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not your address");
        }
        if (country == null || country.isBlank()) {
            throw new RuntimeException("Country is required");
        }
        addr.setFullName(fullName.trim());
        addr.setPhone(phone.trim());
        addr.setProvince(province.trim());
        addr.setDistrict(district.trim());
        addr.setCommune(commune);
        addr.setStreet(street.trim());
        addr.setCountry(country.trim());
        return addressRepository.save(addr);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address addr = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!addr.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not your address");
        }
        addressRepository.delete(addr);
    }
}
