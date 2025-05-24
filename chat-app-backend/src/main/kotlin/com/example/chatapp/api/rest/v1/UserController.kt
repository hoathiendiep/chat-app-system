package com.example.chatapp.api.rest.v1

import com.example.chatapp.app.dto.response.ApiResponse
import com.example.chatapp.app.service.ApiResponseFactory
import com.example.chatapp.domain.user.dto.response.UserResponse
import com.example.chatapp.domain.user.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Tag(name = "User")
class UserController (private val userService: UserService,
    private val responseFactory: ApiResponseFactory){

    @GetMapping
    fun getAllUsers(authentication: Authentication): ApiResponse<List<UserResponse>> {
        val result = userService.findAllUsersExceptSelf(authentication)
        return responseFactory.success(data= result)
    }


}