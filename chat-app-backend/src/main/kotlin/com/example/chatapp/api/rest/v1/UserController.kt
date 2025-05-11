package com.example.chatapp.api.rest.v1

import com.example.chatapp.app.dto.response.ApiResponse
import com.example.chatapp.app.dto.response.PageMetadata
import com.example.chatapp.app.service.ApiResponseFactory
import com.example.chatapp.domain.common.constant.CommonConstant
import com.example.chatapp.domain.user.dto.response.UserResponse
import com.example.chatapp.domain.user.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Tag(name = "User")
@CrossOrigin(origins = ["localhost:4200"])
class UserController (private val userService: UserService,
    private val responseFactory: ApiResponseFactory){

    @GetMapping
    fun getAllUsers(authentication: Authentication,
                    @RequestParam pageNumber: Int = 0, @RequestParam pageSize: Int = Int.MAX_VALUE): ApiResponse<List<UserResponse>> {
        val result = userService.findAllUsersExceptSelf(authentication, pageNumber, pageSize)
        val metadata = PageMetadata(
            pageNumber = pageNumber,
            pageSize = pageSize,
            totalElements = result.size.toLong(),
            sort = "${CommonConstant.DEFAULT_SORT_FIELD}${CommonConstant.COLON}${CommonConstant.SORT_DIR_DESC}"
        )
        return responseFactory.success(data= result, metadata=metadata)
    }
//
//    @PatchMapping("/status/{user_id}")
//    fun setUserStatus(@PathVariable("user_id") userId: UUID,@RequestParam("is_online") isOnline: Boolean): ApiResponse<Unit> {
//        userService.setUserStatus(userId, isOnline)
//        return responseFactory.success(code = ResponseCode.ACCEPTED)
//    }


}