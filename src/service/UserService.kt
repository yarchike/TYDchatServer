package com.martynov.service

import com.martynov.dto.AuthenticationRequestDto
import com.martynov.dto.AuthenticationResponseDto
import com.martynov.dto.RegistrationRequestDto
import com.martynov.exception.PasswordChangeException
import com.martynov.exception.UserAddException
import com.martynov.model.UserModel
import com.martynov.repository.UserRepository
import io.ktor.features.*
import org.springframework.security.crypto.password.PasswordEncoder

class UserService(
    private val repo: UserRepository,
    private val tokenService: JWTTokenService,
    private val passwordEncoder: PasswordEncoder
) {
    suspend fun getModelByid(id: Long): UserModel? {
        return repo.getById(id)
    }

    suspend fun registration(iteam: RegistrationRequestDto): AuthenticationResponseDto {
        val model = UserModel(
            id = repo.getSizeListUser().toLong(),
            username = iteam.username,
            password = passwordEncoder.encode(iteam.password),
            attachment = iteam.attachmentModel,
            token = tokenService.generate(repo.getSizeListUser().toLong())
        )
        val chekingIsUser =repo.addUser(model)
        if (chekingIsUser){
            return AuthenticationResponseDto(model.token)
        }else{
            return throw UserAddException("\"error\": Пользователь с таким логином уже зарегистрирован")
        }

    }
    suspend fun authenticate(input: AuthenticationRequestDto): AuthenticationResponseDto {
        val model = repo.getByUsername(input.username) ?: throw NotFoundException()
        if (!passwordEncoder.matches(input.password, model.password)) {
            throw PasswordChangeException("Неверный пароль")
        }
        val token = tokenService.generate(model.id)
        return AuthenticationResponseDto(token)
    }
}