package com.example.wordsmemory.domain

interface User : IItem {
    var userId: String
    var accessToken: String
}