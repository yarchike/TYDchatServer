package com.martynov.service

import com.martynov.dto.MediaResponseDto
import com.martynov.model.MediaType
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class FileService(private val fotoPath: String) {
    private val images = listOf(ContentType.Image.JPEG, ContentType.Image.PNG)
    init {
        println(Paths.get(fotoPath).toAbsolutePath().toString())
        if (Files.notExists(Paths.get(fotoPath))) {
            Files.createDirectory(Paths.get(fotoPath))
        }
    }
    suspend fun saveFotoUser(multipart: MultiPartData): MediaResponseDto {
        var response: MediaResponseDto? = null
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    if (part.name == "file") {
                        if (!images.contains(part.contentType)) {
                            throw UnsupportedMediaTypeException(part.contentType ?: ContentType.Any)
                        }
                        val ext = when (part.contentType) {
                            ContentType.Image.JPEG -> "jpg"
                            ContentType.Image.PNG -> "png"
                            else -> throw UnsupportedMediaTypeException(part.contentType!!)
                        }
                        val name = "${UUID.randomUUID()}.$ext"
                        val path = Paths.get(fotoPath, name)
                        part.streamProvider().use {
                            withContext(Dispatchers.IO) {
                                Files.copy(it, path)
                            }
                        }
                        part.dispose()
                        response = MediaResponseDto(name, MediaType.IMAGE)
                        return@forEachPart
                    }
                }
            }

            part.dispose()
        }
        return response ?: throw BadRequestException("No file field in request")
    }

}