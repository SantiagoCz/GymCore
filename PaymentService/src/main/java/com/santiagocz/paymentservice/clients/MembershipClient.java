package com.santiagocz.paymentservice.clients;

import com.santiagocz.paymentservice.dto.MembershipDto;
import com.santiagocz.paymentservice.dto.SubscriptionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "membership-service")
public interface MembershipClient {

    @GetMapping("/memberships/{id}")
    MembershipDto getMembershipById(@PathVariable Long id);

    @PostMapping("/subscriptions/create")
    SubscriptionDto create(@RequestParam Long memberId, @RequestParam Long membershipId);

    @PostMapping("/subscriptions/cancel-active")
    void cancelActiveSubscription(@RequestParam Long memberId);
}