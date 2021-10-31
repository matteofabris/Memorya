package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.repository.UserRepository

class RemoveAllUsers(private val _userRepository: UserRepository) {
    suspend operator fun invoke() = _userRepository.removeAll()
}