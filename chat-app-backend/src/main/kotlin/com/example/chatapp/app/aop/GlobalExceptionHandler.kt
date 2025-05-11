package com.example.chatapp.app.aop

import com.example.chatapp.app.dto.response.ApiResponse
import com.example.chatapp.app.service.ApiResponseFactory
import com.example.chatapp.domain.common.constant.ResponseCode
import com.example.chatapp.domain.common.exception.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler(private val apiResponseFactory: ApiResponseFactory) {

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException): ResponseEntity<ApiResponse<Unit>> {
        return createErrorResponse(ex.responseCode, ex.message)
    }

    @ExceptionHandler(TechnicalException::class)
    fun handleTechnicalException(ex: TechnicalException): ResponseEntity<ApiResponse<Unit>> {
        return createErrorResponse(ex.responseCode, ex.message, ex.vars)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
        ex.printStackTrace()
        return createErrorResponse(
            ResponseCode.INTERNAL_SERVER_ERROR,
            ex.message ?: "Internal server error"
        )
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ApiResponse<Unit>> {
        return createErrorResponse(ex.responseCode, vars = ex.vars)
    }

    private fun createErrorResponse(
        code: ResponseCode,
        message: String? = null,
        vars: Array<String> = emptyArray()
    ): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity
            .status(code.code)
            .body(apiResponseFactory.error(code, message, vars))
    }
}