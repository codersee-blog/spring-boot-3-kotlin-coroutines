package com.codersee.springcoroutines.config

import com.codersee.springcoroutines.handler.CompanyHandler
import com.codersee.springcoroutines.handler.SearchHandler
import com.codersee.springcoroutines.handler.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class Config {

    @Bean
    fun router(
        userHandler: UserHandler,
        companyHandler: CompanyHandler,
        searchHandler: SearchHandler
    ) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {

            "/api".nest {

                "/users".nest {
                    POST("", userHandler::createUser)
                    GET("", userHandler::findUsers)
                    GET("/{id}", userHandler::findUserById)
                    DELETE("/{id}", userHandler::deleteUserById)
                    PUT("/{id}", userHandler::updateUser)
                }

                "/companies".nest {
                    POST("", companyHandler::createCompany)
                    GET("", companyHandler::findCompany)
                    GET("/{id}", companyHandler::findCompanyById)
                    DELETE("/{id}", companyHandler::deleteCompanyById)
                    PUT("/{id}", companyHandler::updateCompany)
                }

                "/search".nest {
                    GET("", searchHandler::searchByNames)
                }
            }

        }
    }

}