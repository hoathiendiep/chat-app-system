package com.example.chatapp.app.service

import com.example.chatapp.app.dto.response.ApiResponse
import com.example.chatapp.domain.common.constant.ResponseCode
import com.example.chatapp.domain.common.utils.MessageUtils
import org.springframework.stereotype.Component

@Component
class ApiResponseFactory(
    private val messageUtils: MessageUtils
) {
    fun <T> success(
        data: T? = null,
        code: ResponseCode = ResponseCode.SUCCESS,
        metadata: Any? = null
    ): ApiResponse<T> = ApiResponse(
        status = code.code,
        message = messageUtils.getMessage(code),
        data = data,
        metadata = metadata
    )

    fun <T> error(
        code: ResponseCode = ResponseCode.INTERNAL_SERVER_ERROR,
        message: String? = null,
        vars: Array<String> = emptyArray(),
        data: T? = null
    ): ApiResponse<T> = ApiResponse(
        status = code.code,
        message = message ?: messageUtils.getMessage(code, vars),
        data = data
    )
}