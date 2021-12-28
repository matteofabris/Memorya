package com.memorya.domain

interface User : IItem {
    var userId: String
    var accessToken: String
    var refreshToken: String
}