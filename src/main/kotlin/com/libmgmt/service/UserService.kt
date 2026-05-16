package com.libmgmt.service

import com.libmgmt.exception.ResourceNotFoundException
import com.libmgmt.exception.UnauthorizedException
import com.libmgmt.model.User
import com.libmgmt.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun getUserById(userId: String): User {
        return userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }
    }

    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow { UnauthorizedException("User not found") }
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun deactivateUser(userId: String) {
        val user = getUserById(userId)
        val deactivatedUser = user.copy(active = false)
        userRepository.save(deactivatedUser)
    }
}
