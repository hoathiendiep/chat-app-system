import { CommonModule } from '@angular/common';
import { Component, NgModule } from '@angular/core';
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { FormsModule } from '@angular/forms';
import { ChatService, MessageService, UserService } from '../../services/services';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { Notification } from './models/notification';
import { ChatPreviewResponse, MessageRequest, MessageResponse, UserResponse } from '../../services/models';
import { InfiniteScrollDirective } from 'ngx-infinite-scroll';
import { DownloadFile$Params } from '../../services/fn/message/download-file';
import { HttpResponse } from '@angular/common/http';


@Component({
  selector: 'app-main',
  imports: [CommonModule, PickerComponent, FormsModule, InfiniteScrollDirective],
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent {
  messageContent: string = '';
  showEmojis = false;
  socketClient: any = null;
  selectedChat: ChatPreviewResponse = {};
  chatMessages: Array<MessageResponse> = [];
  chats: Array<ChatPreviewResponse> = [];
  users: Array<UserResponse> = [];
  currUserName: string = "";
  currUserId: string = "";
  isLoadingUser = false;
  currentPageUser = 0;
  itemsPerPageUser = 4;
  private notificationSubscription: any;

  constructor(
    private chatService: ChatService,
    private messageService: MessageService,
    private keycloakService: KeycloakService,
    private userService: UserService) {

  }
  toggleLoadingUser = () => this.isLoadingUser = !this.isLoadingUser;

  ngOnInit(): void {
    this.initWebSocket();
    this.getAllChats();
    this.getAllUsers();
    this.currUserName = this.keycloakService.fullName
    this.currUserId = this.keycloakService.userId
  }

  private getAllUsers() {
    this.userService.getAllUsers({
      pageNumber: this.currentPageUser,
      // pageSize: this.itemsPerPageUser
    }).subscribe({
      next: (res) => {
        this.users = res.data || [];
      }
    })
  }

  chatSelected(chatResponse: ChatPreviewResponse) {
    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id as string);
    this.selectedChat.unread_count = 0;
  }

  private getAllChatMessages(chatId: string) {
    this.messageService.getAllMessages({
      'chat_id': chatId
    }).subscribe({
      next: (res) => {
        this.chatMessages = res.data || [];
        this.setMessagesToSeen();

      }
    });
  }

  onScrollUser = () => {
    this.currentPageUser++;
    this.appendDataUser();
  }

  appendDataUser = () => {
    this.toggleLoadingUser();
    this.userService.getAllUsers({
      pageNumber: this.currentPageUser,
      pageSize: this.itemsPerPageUser
    }).subscribe({
      next: response => this.users = [...this.users, ...response.data || []],
      error: err => console.log(err),
      complete: () => this.toggleLoadingUser()
    })
  }

  private getAllChats() {
    this.chatService.getChatsByReceiver({
      pageNumber: 0,
      // pageSize: 10
    })
      .subscribe({
        next: (res) => {
          this.chats = res.data || [];
        }
      });
  }

  isSelfMessage(message: MessageResponse): boolean {
    return message.sender_id === this.keycloakService.userId;
  }

  logout() {
    this.keycloakService.logout();
  }

  userProfile() {
    this.keycloakService.accountManagement();
  }

  private getSenderId(): string {
    if (this.selectedChat.sender_id === this.keycloakService.userId) {
      return this.selectedChat.sender_id as string;
    }
    return this.selectedChat.recipient_id as string;
  }

  private getReceiverId(): string {
    if (this.selectedChat.sender_id === this.keycloakService.userId) {
      return this.selectedChat.recipient_id as string;
    }
    return this.selectedChat.sender_id as string;
  }

  private initWebSocket() {
    if (this.keycloakService.keycloak.tokenParsed?.sub) {
      let ws = new SockJS(`http://localhost:8081/chat-app-api/rs/v1/ws?token=${this.keycloakService.keycloak.token}`);
      this.socketClient = Stomp.over(ws);
      const subUrl = `/user/${this.keycloakService.keycloak.tokenParsed?.sub}/chat`;
      this.socketClient.connect({ 'Authorization': 'Bearer ' + this.keycloakService.keycloak.token },
        () => {
          this.notificationSubscription = this.socketClient.subscribe(subUrl,
            (message: any) => {
              const notification: Notification = JSON.parse(message.body);
              this.handleNotification(notification);

            },
            () => console.error('Error while connecting to webSocket')
          );
        }
      );
    }
  }

  private handleNotification(notification: Notification) {
    if (!notification) return;
    if (notification.type === 3) {
      const user = this.users.find(u => u.id === notification.user_id)
      if (user !== undefined) {
        user.online = notification.user_status;
      }
      const chat = this.chats.find(c => (c.recipient_id === notification.user_id) || (c.sender_id === notification.user_id));
      if (chat !== undefined) {
        chat.is_recipient_online = notification.user_status;
      }
      if (this.selectedChat && (this.selectedChat.recipient_id === notification.user_id || this.selectedChat.sender_id === notification.user_id)) {
        this.selectedChat.is_recipient_online = notification.user_status;
      }
    }
    else if (this.selectedChat && this.selectedChat.id === notification.chat_id) {
      switch (notification.type) {
        case 1:
        case 2:
          const message: MessageResponse = {
            sender_id: notification.sender_id,
            receiver_id: notification.receiver_id,
            content: notification.content,
            type: notification.message_type,
            attachment_file: notification.media,
            created_date: new Date().toString()
          };
          if (notification.type === 2) {
            this.selectedChat.last_message = 'Attachment';
          } else {
            this.selectedChat.last_message = notification.content;
          }
          this.chatMessages.unshift(message);
          break;

        case 0:
          this.chatMessages.forEach(m => m.state = 1);
          break;
      }
    }
    else {
      const destChat = this.chats.find(c => c.id === notification.chat_id);
      if (destChat && notification.type !== 0) {
        if (notification.type === 1) {
          destChat.last_message = notification.content;
        } else if (notification.type === 2) {
          destChat.last_message = 'Attachment';
        }
        destChat.last_message_time = new Date().toString();
        destChat.unread_count! += 1;
      }
    }
  }


  onSelectEmojis(emojiSelected: any) {
    const emoji: EmojiData = emojiSelected.emoji;
    this.messageContent += emoji.native;
  }

  sendMessage() {
    if (this.messageContent) {
      const messageRequest: MessageRequest = {
        chat_id: this.selectedChat.id,
        chat_name: this.selectedChat.chat_name,
        sender_id: this.getSenderId(),
        receiver_id: this.getReceiverId(),
        content: this.messageContent,
        type: 0,
      };
      this.messageService.saveMessage({
        body: messageRequest
      }).subscribe({
        next: () => {
          const message: MessageResponse = {
            sender_id: this.getSenderId(),
            receiver_id: this.getReceiverId(),
            content: this.messageContent,
            type: 0,
            state: 0,
            created_date: new Date().toString()
          };
          this.selectedChat.last_message = this.messageContent;
          this.chatMessages.unshift(message);
          this.messageContent = '';
          this.showEmojis = false;
        }
      });
    }
  }

  uploadMedia(target: EventTarget | null) {
    const file = this.extractFileFromTarget(target);
    const currentDate = new Date();

    const year = currentDate.getFullYear();
    const month = String(currentDate.getMonth() + 1).padStart(2, '0');
    const day = String(currentDate.getDate()).padStart(2, '0');
    if (file !== null) {
      const reader = new FileReader();
      reader.onload = () => {
        if (reader.result) {

          const mediaLines = reader.result.toString().split(',')[1];

          this.messageService.uploadMedia({
            "chat_id": this.selectedChat.id || '',
            file_path: `dev/${this.keycloakService.fullName}/${year}/${month}/${day}/${this.keycloakService.token.substring(0, 25)}/${file.name}`,
            body: {
              'file': file
            }
          })
            .subscribe({
              next: (data) => {
                const message: MessageResponse = {
                  id: data.data,
                  file_name: file.name,
                  sender_id: this.getSenderId(),
                  receiver_id: this.getReceiverId(),
                  content: 'Attachment',
                  type: 1,
                  state: 0,
                  attachment_file: [mediaLines],
                  created_date: new Date().toString()
                };
                this.selectedChat.last_message_type = 1;
                this.chatMessages.unshift(message);
                this.messageContent = '';
                this.showEmojis = false;
              },
              error: (err) =>{
                alert("Error upload file due to "+err.message)
              }
            });
        }
      }
      reader.readAsDataURL(file);
    }
  }

  private extractFileFromTarget(target: EventTarget | null): File | null {
    const htmlInputTarget = target as HTMLInputElement;
    if (target === null || htmlInputTarget.files === null) {
      return null;
    }
    return htmlInputTarget.files[0];
  }

  ngOnDestroy(): void {
    if (this.socketClient !== null) {
      this.socketClient.disconnect();
      this.notificationSubscription.unsubscribe();
      this.socketClient = null;
    }
  }


  setMessagesToSeen() {
    if (this.selectedChat.id !== null && this.chats.length > 0 && this.chatMessages[0].receiver_id === this.keycloakService.userId) {
      this.messageService.setMessageToSeen({
        chat_id: this.selectedChat.id as string
      }).subscribe({
        next: () => {
        }
      });
    }


  }

  selectContact(receiver: UserResponse) {
    this.chatService.createChat({
      'sender_id': this.keycloakService.userId as string,
      'receiver_id': receiver.id as string
    }).subscribe({
      next: (res) => {
        const chat: ChatPreviewResponse = {
          id: res.data || undefined,
          chat_name: receiver.firstName + ' ' + receiver.lastName,
          is_recipient_online: receiver.online,
          last_message_time: receiver.lastSeen,
          sender_id: this.keycloakService.userId,
          recipient_id: receiver.id
        };
        if (chat.id && !this.chats.some(existingChat => existingChat.id === chat.id)) {
          this.chats.unshift(chat);
        };
        this.selectedChat = chat
        this.getAllChatMessages(res.data!)
      }
    });

  }
  formatLastMessageTime(dateString?: string): string {
    if (!dateString) {
      return '';
    }
    const date = new Date(dateString);
    const now = new Date();

    if (date.getFullYear() !== now.getFullYear()) {
      return date.toLocaleDateString('en-GB', { month: '2-digit', year: 'numeric' });
    } else if (
      date.getDate() !== now.getDate() ||
      date.getMonth() !== now.getMonth()
    ) {
      return date.toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit' });
    } else {
      return date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
    }
  }

  downloadFile(msgId?: string) {
    if (!msgId) {
      return
    }
    const params: DownloadFile$Params = { message_id: msgId };
    this.messageService.downloadFile(params).subscribe({
      next: (response: HttpResponse<Blob>) => {
        const blob = response.body;
        if (!blob) {
          throw new Error('No file content returned');
        }
        const contentDisposition = response.headers.get('Content-Disposition') || '';
        const matches = /filename="([^"]+)"/.exec(contentDisposition);
        const fileName = matches && matches[1] ? matches[1] : `attachment-${msgId}.jpg`;

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;  
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        alert('Failed to download file: ' + err.message);  
      }
    });
  }
  downloadFileData(data: any, fileName?: string) {
    if (!data || data.length === 0 || !fileName) {
      console.error("No data provided for download.");
      return;
    }

    const fileData = Array.isArray(data) ? data.join('') : data;

    let binaryData;
    if (this.isBase64(fileData)) {
      binaryData = this.base64ToArrayBuffer(fileData);
    } else {
      binaryData = new TextEncoder().encode(fileData);
    }

 
    const blob = new Blob([binaryData], { type: this.getMimeType(fileName.split('.').pop() || '') });

 
    const url = window.URL.createObjectURL(blob);
 
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName || 'attachment';
    document.body.appendChild(a);
    a.click();
 
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }

 
  private isBase64(str: string): boolean {
    try {
      return btoa(atob(str)) === str;
    } catch (err) {
      return false;
    }
  }
 
  private base64ToArrayBuffer(base64: string): ArrayBuffer {
    const binaryString = atob(base64);
    const bytes = new Uint8Array(binaryString.length);
    for (let i = 0; i < binaryString.length; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
  }

 
  private getMimeType(extension: string): string {
    const mimeTypes: Record<string, string> = {
      'jpg': 'image/jpeg',
      'jpeg': 'image/jpeg',
      'png': 'image/png',   
      'gif': 'image/gif',
      'bmp': 'image/bmp',
      'webp': 'image/webp',
      'svg': 'image/svg+xml',
      'pdf': 'application/pdf',
      'txt': 'text/plain',
      'doc': 'application/msword',
      'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'ppt': 'application/vnd.ms-powerpoint',
      'pptx': 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
      'xls': 'application/vnd.ms-excel',
      'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'zip': 'application/zip',
      'mp4': 'video/mp4',
      'mp3': 'audio/mpeg',
      'csv': 'text/csv'
    };

    return mimeTypes[extension.toLowerCase()] || 'application/octet-stream';
  }
  isImageFile(fileName?: string | ''): boolean {
    const imageExtensions = ["jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"];
    const fileExtension = fileName?.split(".").pop()?.toLowerCase();
    return imageExtensions.includes(fileExtension || "");
  }
}


