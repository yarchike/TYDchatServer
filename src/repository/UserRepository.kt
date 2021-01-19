package com.martynov.repository

import com.martynov.model.UserModel

interface UserRepository {
    suspend fun getById(id: Long): UserModel?
    suspend fun getByUsername(username: String): UserModel?
    suspend fun save(item: UserModel): UserModel
    suspend fun getSizeListUser(): Int
    suspend fun addUser(iteam: UserModel): Boolean

}