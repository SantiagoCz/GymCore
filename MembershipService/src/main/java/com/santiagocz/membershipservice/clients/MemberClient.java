package com.santiagocz.membershipservice.clients;

import com.santiagocz.membershipservice.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "client-service")
public interface MemberClient {

    @GetMapping("/members/{id}")
    MemberDto getMemberById(@PathVariable Long id);

    @GetMapping("/members/dni/{dni}")
    MemberDto getMemberByDni(@PathVariable String dni);

}