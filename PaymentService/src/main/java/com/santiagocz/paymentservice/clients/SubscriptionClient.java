package com.santiagocz.paymentservice.clients;

import com.santiagocz.paymentservice.dto.SubscriptionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "membership-service")
public interface SubscriptionClient {

    @GetMapping("/subscriptions/{id}")
    SubscriptionDto getById(@PathVariable Long id);

    @PatchMapping("/subscriptions/{id}/renew")
    void renew(@PathVariable Long id);
}
