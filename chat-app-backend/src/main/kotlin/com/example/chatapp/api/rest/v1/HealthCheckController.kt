package com.example.chatapp.api.rest.v1

import com.example.chatapp.app.dto.response.ApiResponse
import com.example.chatapp.app.service.ApiResponseFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController(private val responseFactory: ApiResponseFactory) {

    @GetMapping("/actuator2/health")
    fun getHealthInfo():ApiResponse<String>{
        return responseFactory.success(data = "Healthy")
    }

}