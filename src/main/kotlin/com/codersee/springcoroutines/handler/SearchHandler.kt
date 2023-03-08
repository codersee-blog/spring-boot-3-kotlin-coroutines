package com.codersee.springcoroutines.handler

import com.codersee.springcoroutines.dto.IdNameTypeResponse
import com.codersee.springcoroutines.dto.ResultType
import com.codersee.springcoroutines.model.Company
import com.codersee.springcoroutines.model.User
import com.codersee.springcoroutines.service.CompanyService
import com.codersee.springcoroutines.service.UserService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import org.springframework.web.server.ResponseStatusException

@Component
class SearchHandler(
    private val userService: UserService,
    private val companyService: CompanyService
) {

    suspend fun searchByNames(
        request: ServerRequest
    ): ServerResponse {
        val query = request.queryParamOrNull("name")
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val usersFlow = userService.findAllUsersByNameLike(name = query)
            .map(User::toIdNameTypeResponse)
        val companiesFlow = companyService.findAllCompaniesByNameLike(name = query)
            .map(Company::toIdNameTypeResponse)

        val mergedFlows = merge(usersFlow, companiesFlow)

        return ServerResponse.ok()
            .bodyAndAwait(mergedFlows)
    }
}

private fun User.toIdNameTypeResponse(): IdNameTypeResponse =
    IdNameTypeResponse(
        id = this.id!!,
        name = this.name,
        type = ResultType.USER
    )

private fun Company.toIdNameTypeResponse(): IdNameTypeResponse =
    IdNameTypeResponse(
        id = this.id!!,
        name = this.name,
        type = ResultType.COMPANY
    )