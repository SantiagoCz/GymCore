package com.santiagocz.clientservice.controllers;

import com.santiagocz.clientservice.dto.MemberRequestDto;
import com.santiagocz.clientservice.dto.MemberResponseDto;
import com.santiagocz.clientservice.domain.enums.MemberStatus;
import com.santiagocz.clientservice.services.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> findAll() {
        return ResponseEntity.ok(memberService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.findById(id));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<MemberResponseDto> findByDni(@PathVariable String dni) {
        return ResponseEntity.ok(memberService.findByDni(dni));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MemberResponseDto>> findByStatus(@PathVariable MemberStatus status) {
        return ResponseEntity.ok(memberService.findByStatus(status));
    }

    @PostMapping
    public ResponseEntity<MemberResponseDto> create(@Valid @RequestBody MemberRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequestDto dto) {
        return ResponseEntity.ok(memberService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}