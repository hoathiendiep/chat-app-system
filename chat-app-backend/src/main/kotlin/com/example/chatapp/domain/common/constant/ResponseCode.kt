package com.example.chatapp.domain.common.constant

enum class ResponseCode(val type: String, val code: Int) {
    SUCCESS("SUCCESS", 200),
    CREATED("CREATED", 201),
    ACCEPTED("ACCEPTED", 202),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", 500),
    BAD_REQUEST("BAD_REQUEST", 400),
    INVALID_PARAMS("INVALID_PARAMS", 400),
    USER_NOT_FOUND("USER_NOT_FOUND", 404),
    CHAT_NOT_FOUND("CHAT_NOT_FOUND", 404),
    MESSAGE_NOT_FOUND("MESSAGE_NOT_FOUND", 404);

}