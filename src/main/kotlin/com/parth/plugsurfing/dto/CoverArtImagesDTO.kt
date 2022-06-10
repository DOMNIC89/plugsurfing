package com.parth.plugsurfing.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.net.URL

@JsonIgnoreProperties(ignoreUnknown = true)
data class CoverArtImagesDTO(val images: List<CoverArtImage>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CoverArtImage(
    val front: Boolean,
    val back: Boolean,
    val image: URL,
    val approved: Boolean,
    val id: String,
)
