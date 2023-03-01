package com.codersee.springcoroutines.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("application.app_user")
data class User(
    @Id val id: Long? = null,
    val email: String,
    val name: String,
    val age: Int,
    val companyId: Long
)