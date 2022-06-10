package com.parth.plugsurfing.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class WikipediaResponseDTO(
    val title: String,
    @JsonProperty("extract_html") val extractHtml: String,
)
