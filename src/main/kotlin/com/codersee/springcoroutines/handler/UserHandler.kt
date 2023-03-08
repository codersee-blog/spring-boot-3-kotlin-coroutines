package com.codersee.springcoroutines.handler

import com.codersee.springcoroutines.dto.UserRequest
import com.codersee.springcoroutines.dto.UserResponse
import com.codersee.springcoroutines.model.User
import com.codersee.springcoroutines.service.UserService
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ResponseStatusException

@Component
class UserHandler(
    private val userService: UserService
) {

    suspend fun createUser(request: ServerRequest): ServerResponse {
        val userRequest = request.awaitBody(UserRequest::class)

        val savedUserResponse = userService.saveUser(
            user = userRequest.toModel()
        )
            ?.toResponse()
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during user creation.")

        return ServerResponse.ok()
            .bodyValueAndAwait(savedUserResponse)
    }

    suspend fun findUsers(
        request: ServerRequest
    ): ServerResponse {
        val users = request.queryParamOrNull("name")
            ?.let { name -> userService.findAllUsersByNameLike(name) }
            ?: userService.findAllUsers()

        val usersResponse = users.map(User::toResponse)

        return ServerResponse.ok()
            .bodyAndAwait(usersResponse)
    }

    suspend fun findUserById(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val userResponse = userService.findUserById(id)
            ?.let(User::toResponse)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with id $id was not found.")

        return ServerResponse.ok()
            .bodyValueAndAwait(userResponse)
    }

    suspend fun deleteUserById(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id").toLong()

        userService.deleteUserById(id)

        return ServerResponse.noContent()
            .buildAndAwait()
    }

    suspend fun updateUser(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val userRequest = request.awaitBody(UserRequest::class)

        val userResponse = userService.updateUser(
            id = id,
            requestedUser = userRequest.toModel()
        ).toResponse()

        return ServerResponse.ok()
            .bodyValueAndAwait(userResponse)
    }

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

