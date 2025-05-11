package com.example.chatapp.domain.common.utils

import com.example.chatapp.domain.common.constant.ResponseCode
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class MessageUtils(private val messageSource: MessageSource) {
    fun getMessage(code: ResponseCode, args: Array<String> = emptyArray()): String {
        return messageSource.getMessage(code.type, args, Locale.getDefault())
    }
}