package com.codersee.springcoroutines.dto

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val age: Int
)
