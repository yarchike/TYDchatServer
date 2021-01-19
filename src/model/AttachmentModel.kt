package com.martynov.model

enum class AttachmentType {
    IMAGE
}

data class AttachmentModel(val id: String, val mediaType: AttachmentType = AttachmentType.IMAGE)