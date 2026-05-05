package com.santiagocz.paymentservice.clients;

import com.santiagocz.paymentservice.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "client-service")
public interface MemberClient {

    @GetMapping("/members/{id}")
    MemberDto getMemberById(@PathVariable Long id);
}
