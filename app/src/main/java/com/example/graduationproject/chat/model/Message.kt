package com.example.graduationproject.chat.model

data class Message(
    val sender: String? = "",
    val receiver: String? = "",
    val message: String? = "",
    val time: String? = "",

    ) {
    val id: String get() = "$sender-$receiver-$message-$time"
}