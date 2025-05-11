export interface Notification {
    chat_id?: string;
    content?: string;
    sender_id?: string;
    receiver_id?: string;
    user_id?: string;
    message_type?: number;
    type?: number;
    chat_name?: string;
    media?: Array<string>;
    user_status?: boolean;

}
