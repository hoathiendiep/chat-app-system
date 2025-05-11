package com.example.chatapp.domain.common.exception

import com.example.chatapp.domain.common.constant.ResponseCode

class NotFoundException(
    override val responseCode: ResponseCode,
    override val vars: Array<String> = emptyArray()
) : BaseException(responseCode, vars = vars)
