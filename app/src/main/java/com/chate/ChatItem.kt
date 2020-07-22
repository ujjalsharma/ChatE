package com.chate

class ChatItem {

    var chatID: String? = null
    var latestTimestamp: String? = null
    var userID: String? = null

    constructor(
        chatID: String?,
        latestTimestamp: String?,
        userID: String?
    ) {
        this.chatID = chatID
        this.latestTimestamp = latestTimestamp
        this.userID = userID
    }

    // make sure to have an empty constructor inside ur model class
    constructor() {}

}