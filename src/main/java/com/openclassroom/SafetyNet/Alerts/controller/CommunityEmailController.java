package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.service.CommunityEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommunityEmailController {

    private final CommunityEmailService communityEmailService;

    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getEmails(@RequestParam("city") String city) {
        log.info("GET /communityEmail?city={} - incoming", city);
        List<String> emails = communityEmailService.getEmailsByCity(city);
        log.info("GET /communityEmail - ok: count={}", emails.size());
        return ResponseEntity.ok(emails);
    }
}
