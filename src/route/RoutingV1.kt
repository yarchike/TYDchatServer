package com.martynov.route

import com.martynov.dto.AuthenticationRequestDto
import com.martynov.dto.RegistrationRequestDto
import com.martynov.service.FileService
import com.martynov.service.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*

class RoutingV1(
    private val staticPath: String,
    private val fileService: FileService,
    private val userService: UserService
) {

    fun setup(configuration: Routing){
        with(configuration){
            get("/") {
                call.respondText("Server working", ContentType.Text.Plain)
            }
            static("/static") {
                files(staticPath)
            }
            route("/api/v1/"){
                post("/foto") {
                    val multipart = call.receiveMultipart()
                    val response = fileService.saveFotoUser(multipart)
                    call.respond(response)

                }
            }
            post("/registration") {
                val input = call.receive<RegistrationRequestDto>()
                val response = userService.registration(input)
                call.respond(response)
            }
            post("/authentication") {
                val input = call.receive<AuthenticationRequestDto>()
                val response = userService.authenticate(input)
                call.respond(response)
            }

        }
    }
}