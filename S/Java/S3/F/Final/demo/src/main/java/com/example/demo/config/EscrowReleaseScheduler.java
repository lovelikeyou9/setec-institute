package com.example.demo.config;

import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * After delivery + buyer-protection window with no dispute,
 * automatically release held funds to the seller (minus commission).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EscrowReleaseScheduler {

    private final OrderService orderService;

    /** Every 15 minutes (demo-friendly). */
    @Scheduled(fixedDelayString = "${app.escrow.release-interval-ms:900000}")
    public void releaseDuePayouts() {
        int n = orderService.autoReleaseEligibleOrders();
        if (n > 0) {
            log.info("Escrow auto-released {} order payout(s)", n);
        }
    }
}