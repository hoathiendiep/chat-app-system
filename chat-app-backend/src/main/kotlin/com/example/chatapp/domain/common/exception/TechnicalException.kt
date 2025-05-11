package com.example.chatapp.domain.common.exception

import com.example.chatapp.domain.common.constant.ResponseCode

class TechnicalException(
    override val responseCode: ResponseCode,
    override val vars: Array<String> = emptyArray(),
    override val message: String? = null
) : BaseException(responseCode, message, vars)
