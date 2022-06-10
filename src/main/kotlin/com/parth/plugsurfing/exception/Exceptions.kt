package com.parth.plugsurfing.exception

data class InvalidMBIDRequestException(override val message: String) : Exception(message)

data class MBIDNotFoundException(override val message: String) : Exception(message)

data class SomethingWentWrongException(override val message: String) : Exception(message)

data class ServerBehavedIrresponsiblyException(override val message: String) : Exception(message)

data class AlbumCoverArtNotFound(override val message: String) : Exception(message)

data class ApiError(
    val status: Int,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)