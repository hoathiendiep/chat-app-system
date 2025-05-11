package com.example.chatapp.api.rest.v1

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/webhook")
class TestController {
    @GetMapping
    fun validateWebhook(): ResponseEntity<String> {
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body("SUCCESS")
    }
    @PostMapping
    fun subsribeWebhook( @RequestParam(required = false) validationToken: String?,
                         @RequestBody(required = false) value : String?): ResponseEntity<String> {
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(validationToken)
    }
}