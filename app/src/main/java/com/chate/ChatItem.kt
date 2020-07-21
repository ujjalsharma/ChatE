package com.chate

class ChatItem {

    var chatID: String? = null
    var latestTimestamp: Long? = null

    constructor(
        chatID: String?,
        latestTimestamp: Long?
    ) {
        this.chatID = chatID
        this.latestTimestamp = latestTimestamp
    }

    // make sure to have an empty constructor inside ur model class
    constructor() {}

}