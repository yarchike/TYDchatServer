package com.martynov

import com.martynov.repository.UserRepository
import com.martynov.repository.UserRepositoryInMemoryWithMutexImpl
import com.martynov.route.RoutingV1
import com.martynov.service.FileService
import com.martynov.service.JWTTokenService
import com.martynov.service.UserService
import com.martynov.socet.Server
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.with
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration
import javax.naming.ConfigurationException
import kotlin.coroutines.EmptyCoroutineContext

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)


@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        install(io.ktor.websocket.WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }
    install(KodeinFeature) {
        constant(tag = "foto-dir") with (
                environment.config.propertyOrNull("ph.upload.dir")?.getString()
                    ?: throw ConfigurationException("Upload dir is not specified")
                )
        bind<FileService>() with eagerSingleton {
            FileService(
                instance(tag = "foto-dir")
            )
        }

        bind<RoutingV1>() with eagerSingleton {
            RoutingV1(
                instance(tag = "foto-dir"),
                instance(),
                instance()
            )
        }
        bind<UserService>() with eagerSingleton { UserService(instance(), instance(), instance()) }
        bind<UserRepository>() with eagerSingleton { UserRepositoryInMemoryWithMutexImpl() }
        bind<JWTTokenService>() with eagerSingleton { JWTTokenService() }
        bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
    }
    install(Authentication) {
        jwt {
            val jwtServisce by kodein().instance<JWTTokenService>()
            verifier(jwtServisce.verifier)
            val userService by kodein().instance<UserService>()

            validate {
                val id = it.payload.getClaim("id").asLong()
                userService.getModelByid(id)
            }
        }
    }
    install(Routing) {
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }
    launch(Dispatchers.Default) {
        val server = Server ()
    }
        //


}

