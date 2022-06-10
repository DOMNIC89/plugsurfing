package com.parth.plugsurfing.dto

import java.net.URL

data class MusicArtistResponseDTO (
    val mbid: String,
    val name: String,
    val gender: String,
    val country: String,
    val disambiguation: String,
    val description: String,
    val albums: List<AlbumDTO>,
)

data class AlbumDTO(
    val id: String,
    val title: String,
    val imageUrl: String?,
)