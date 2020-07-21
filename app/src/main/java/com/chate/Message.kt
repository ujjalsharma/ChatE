package com.chate

class Message {

    var message: String? = null
    var userID: String? = null
    var timestamp: String? = null
    var messageID: String? = null
    var chatID: String? = null

    constructor(
        message: String?,
        userID: String?,
        timestamp: String?,
        messageID: String?,
        chatID: String?
    ) {
        this.message = message
        this.userID = userID
        this.timestamp = timestamp
        this.messageID = messageID
        this.chatID = chatID
    }

    constructor() {}


}