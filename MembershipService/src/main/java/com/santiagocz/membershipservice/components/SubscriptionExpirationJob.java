package com.santiagocz.membershipservice.components;

import com.santiagocz.membershipservice.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionExpirationJob {

    private final SubscriptionService subscriptionService;

    // Every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void expireSubscriptions() {
        subscriptionService.expireSubscriptions();
    }
}