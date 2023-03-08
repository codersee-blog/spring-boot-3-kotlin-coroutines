package com.codersee.springcoroutines.repository

import com.codersee.springcoroutines.model.Company
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CompanyRepository : CoroutineCrudRepository<Company, Long> {
    fun findByNameContaining(name: String): Flow<Company>
}