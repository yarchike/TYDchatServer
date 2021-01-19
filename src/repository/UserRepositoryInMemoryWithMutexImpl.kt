package com.martynov.repository

import com.google.gson.Gson
import com.martynov.FILE_USER
import com.martynov.model.UserModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class UserRepositoryInMemoryWithMutexImpl : UserRepository {
    private var nextId = 1L
    private val items = mutableListOf<UserModel>()
    private val mutex = Mutex()
    override suspend fun getById(id: Long): UserModel? {
        mutex.withLock {
            return items.find { it.id == id }
        }

    }

    override suspend fun getByUsername(username: String): UserModel? {
        mutex.withLock {
            return items.find { it.username == username }
        }

    }

    override suspend fun save(item: UserModel): UserModel {
        mutex.withLock {
            return when (val index = items.indexOfFirst { item.id == it.id }) {
                -1 -> {
                    val copy = item.copy(id = items.size.toLong())
                    items.add(copy)
                    File(FILE_USER).writeText(Gson().toJson(items))
                    copy
                }
                else -> {
                    val copy = items[index].copy(username = item.username, password = item.password)
                    items.set(index, copy)
                    copy
                }
            }
        }
    }

    override suspend fun getSizeListUser(): Int {
        return items.size
    }

    override suspend fun addUser(item: UserModel): Boolean {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == item.id }){
                -1 ->{
                    items.add(item)
                    File(FILE_USER).writeText(Gson().toJson(items))
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}