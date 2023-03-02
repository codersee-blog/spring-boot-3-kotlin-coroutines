package com.codersee.springcoroutines.controller

import com.codersee.springcoroutines.dto.UserRequest
import com.codersee.springcoroutines.dto.UserResponse
import com.codersee.springcoroutines.model.User
import com.codersee.springcoroutines.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    suspend fun createUser(@RequestBody userRequest: UserRequest): UserResponse =
        userService.saveUser(
            user = userRequest.toModel()
        )
            ?.toResponse()
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during user creation.")

    @GetMapping
    suspend fun findUsers(
        @RequestParam("name", required = false) name: String?
    ): Flow<UserResponse> {
        val users = name?.let { userService.findAllUsersByNameLike(name) }
            ?: userService.findAllUsers()

        return users.map(User::toResponse)
    }

    @GetMapping("/{id}")
    suspend fun findUserById(
        @PathVariable id: Long
    ): UserResponse =
        userService.findUserById(id)
            ?.let(User::toResponse)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with id $id was not found.")

    @DeleteMapping("/{id}")
    suspend fun deleteUserById(
        @PathVariable id: Long
    ) {
        userService.deleteUserById(id)
    }

    @PutMapping("/{id}")
    suspend fun updateUser(
        @PathVariable id: Long,
        @RequestBody userRequest: UserRequest
    ): UserResponse =
        userService.updateUser(
            id = id,
            requestedUser = userRequest.toModel()
        )
            .toResponse()
}

private fun UserRequest.toModel(): User =
    User(
        email = this.email,
        name = this.name,
        age = this.age,
        companyId = this.companyId
    )

fun User.toResponse(): UserResponse =
    UserResponse(
        id = this.id!!,
        email = this.email,
        name = this.name,
        age = this.age
    )

