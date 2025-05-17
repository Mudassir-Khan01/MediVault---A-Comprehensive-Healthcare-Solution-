package com.geekymusketeers.mediVault.Enhancements.ChatBot

data class MessageChat(
    var message: String,
    var sentBy: String
) {
    companion object {
        const val SENT_BY_ME = "me"
        const val SENT_BY_BOT = "bot"
    }
}