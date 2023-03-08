package com.codersee.springcoroutines.handler

import com.codersee.springcoroutines.dto.CompanyRequest
import com.codersee.springcoroutines.dto.CompanyResponse
import com.codersee.springcoroutines.model.Company
import com.codersee.springcoroutines.model.User
import com.codersee.springcoroutines.service.CompanyService
import com.codersee.springcoroutines.service.UserService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ResponseStatusException

@Component
class CompanyHandler(
    private val companyService: CompanyService,
    private val userService: UserService
) {

    suspend fun createCompany(request: ServerRequest): ServerResponse {
        val companyRequest = request.awaitBody(CompanyRequest::class)

        val createdCompanyResponse = companyService.saveCompany(
            company = companyRequest.toModel()
        )
            ?.toResponse()
            ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during company creation."
            )

        return ServerResponse.ok()
            .bodyValueAndAwait(createdCompanyResponse)
    }

    suspend fun findCompany(
        request: ServerRequest
    ): ServerResponse {
        val companies = request.queryParamOrNull("name")
            ?.let { name -> companyService.findAllCompaniesByNameLike(name) }
            ?: companyService.findAllCompanies()

        val companiesResponses = companies
            .map { company ->
                company.toResponse(
                    users = findCompanyUsers(company)
                )
            }

        return ServerResponse.ok()
            .bodyAndAwait(companiesResponses)
    }


    suspend fun findCompanyById(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val companyResponse = companyService.findCompanyById(id)
            ?.let { company ->
                company.toResponse(
                    users = findCompanyUsers(company)
                )
            }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Company with id $id was not found.")

        return ServerResponse.ok()
            .bodyValueAndAwait(companyResponse)
    }


    suspend fun deleteCompanyById(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id").toLong()

        companyService.deleteCompanyById(id)

        return ServerResponse.noContent()
            .buildAndAwait()
    }

    suspend fun updateCompany(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val companyRequest = request.awaitBody(CompanyRequest::class)


        val updatedCompanyResponse = companyService.updateCompany(
            id = id,
            requestedCompany = companyRequest.toModel()
        )
            .let { company ->
                company.toResponse(
                    users = findCompanyUsers(company)
                )
            }

        return ServerResponse.ok()
            .bodyValueAndAwait(updatedCompanyResponse)
    }

    private suspend fun findCompanyUsers(company: Company) =
        userService.findUsersByCompanyId(company.id!!)
            .toList()
}


private fun CompanyRequest.toModel(): Company =
    Company(
        name = this.name,
        address = this.address
    )

private fun Company.toResponse(users: List<User> = emptyList()): CompanyResponse =
    CompanyResponse(
        id = this.id!!,
        name = this.name,
        address = this.address,
        users = users.map(User::toResponse)
    )

