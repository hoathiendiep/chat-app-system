package com.example.chatapp.api.rest.v1

import com.example.chatapp.app.dto.response.ApiResponse
import com.example.chatapp.app.dto.response.PageMetadata
import com.example.chatapp.app.service.ApiResponseFactory
import com.example.chatapp.domain.common.constant.CommonConstant
import com.example.chatapp.domain.common.constant.ResponseCode
import com.example.chatapp.domain.message.dto.request.MessageRequest
import com.example.chatapp.domain.message.dto.response.MessageResponse
import com.example.chatapp.domain.message.service.MessageService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/messages")
@Tag(name = "Message")
@CrossOrigin(origins = ["localhost:4200"])
class MessageController(
    private val messageService: MessageService,
    private val responseFactory: ApiResponseFactory
) {
    @PostMapping
    fun saveMessage(@RequestBody message: MessageRequest) : ApiResponse<Unit> {
        messageService.saveMessage(message)
        return responseFactory.success(code = ResponseCode.CREATED)
    }

    @PostMapping(value = ["/upload-media"], consumes = ["multipart/form-data"])
    fun uploadMedia(
        @RequestParam("chat_id") chatId: UUID,
        @RequestParam("file_path") filePath : String,
        @Parameter @RequestPart("file") file: MultipartFile,
        authentication: Authentication
    ) : ApiResponse<UUID> {
        return responseFactory.success(code = ResponseCode.CREATED, data = messageService.uploadMediaMessage(chatId,filePath, file.bytes, authentication))
    }

    @PatchMapping
    fun setMessageToSeen(@RequestParam("chat_id") chatId: UUID, authentication: Authentication): ApiResponse<Unit> {
        messageService.setMessagesToSeen(chatId, authentication)
        return responseFactory.success(code = ResponseCode.ACCEPTED)
    }

    @GetMapping("/{chat_id}")
    fun getAllMessages(
        @RequestParam pageNumber: Int = 0,
        @RequestParam pageSize: Int = Int.MAX_VALUE,
        @PathVariable("chat_id") chatId: UUID
    ): ApiResponse<List<MessageResponse>> {
        val result = messageService.findChatMessages(chatId,pageNumber,pageSize)
        val metadata = PageMetadata(
            pageNumber = pageNumber,
            pageSize = pageSize,
            totalElements = result.size.toLong(),
            sort = "${CommonConstant.DEFAULT_SORT_FIELD}${CommonConstant.COLON}${CommonConstant.SORT_DIR_DESC}"
        )
        return responseFactory.success(
            data = result,
            metadata = metadata
        )
    }

    @GetMapping("/download-attachment/{message_id}")
    fun downloadFile(
        @PathVariable("message_id") messageId: UUID
    ): ResponseEntity<ByteArray> {
        val attach = this.messageService.getAttachmentByMessageId(messageId)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${attach.fileName}\"")
            .body(attach.data)
    }

}