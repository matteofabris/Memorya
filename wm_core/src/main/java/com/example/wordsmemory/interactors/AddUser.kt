package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.repository.UserRepository
import com.example.wordsmemory.domain.User

class AddUser(private val _userRepository: UserRepository) {
    suspend operator fun invoke(user: User) = _userRepository.add(user)
}