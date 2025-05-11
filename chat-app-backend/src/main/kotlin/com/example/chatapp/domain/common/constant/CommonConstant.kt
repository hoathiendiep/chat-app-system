package com.example.chatapp.domain.common.constant

object CommonConstant {
    val ERROR = "ERROR"
    val SUCCESS = "SUCCESS"
    val COLON = ":"
    val DEFAULT_SORT_FIELD = "created_date"
    val SORT_DIR_ASC = "ASC"
    val SORT_DIR_DESC = "DESC"
    val CONTAINER_NAME = "attachments"
    object SortConstants {
        val ALLOWED_SORT_FIELDS = setOf("modified_date", "created_at", "id")
        val SORT_DIR_TYPE = setOf("desc","asc")
    }
    object WebSocketConstants{
        val SIMP_SESSION_ATTRIBUTES = "simpSessionAttributes"
        val USER_ID = "userId"

    }
}