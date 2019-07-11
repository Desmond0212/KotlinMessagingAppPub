package com.example.kotlin_messaging_app.VO

class MessageVO(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long)
{
    constructor() : this("", "", "", "", -1)
}