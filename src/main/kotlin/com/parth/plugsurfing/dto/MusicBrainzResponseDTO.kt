package com.parth.plugsurfing.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusicBrainzResponseDTO(
    val gender: String,
    val id: String,
    val name: String,
    val relations: List<MusicBrainzRelationsDTO>,
    val country: String,
    val disambiguation: String,
    @JsonProperty("release-groups") val releaseGroups: List<ReleaseGroupsDTO>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusicBrainzRelationsDTO(
    val type: String,
    val ended: Boolean,
    @JsonProperty("url") val urlResourceDTO: URLResourceDTO,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class URLResourceDTO(
    val resource: URL,
    val id: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReleaseGroupsDTO(
    val id: String,
    val title: String,
    @JsonProperty("primary-type") val type: String,
)