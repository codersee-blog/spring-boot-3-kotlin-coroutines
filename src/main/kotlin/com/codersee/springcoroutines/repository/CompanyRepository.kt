package com.codersee.springcoroutines.repository

import com.codersee.springcoroutines.model.Company
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : CoroutineCrudRepository<Company, Long> {
    fun findByNameContaining(name: String): Flow<Company>
}