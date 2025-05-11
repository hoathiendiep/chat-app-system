package com.example.chatapp.domain.common.exception

import com.example.chatapp.domain.common.constant.ResponseCode

sealed class BaseException(
    open val responseCode: ResponseCode,
    override val message: String? = null,
    open val vars: Array<String> = emptyArray()
) : RuntimeException(message)