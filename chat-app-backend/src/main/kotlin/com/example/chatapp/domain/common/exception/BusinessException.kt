package com.example.chatapp.domain.common.exception

import com.example.chatapp.domain.common.constant.ResponseCode

class BusinessException(
    override val responseCode: ResponseCode,
    override val message: String? = null
) : BaseException(responseCode, message)