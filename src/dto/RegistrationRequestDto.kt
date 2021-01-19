package com.martynov.dto

import com.martynov.model.AttachmentModel

data class RegistrationRequestDto(val username: String, val password: String, val attachmentModel: AttachmentModel?)