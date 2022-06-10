package com.parth.plugsurfing.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.net.URL

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusicArtistWikiResponseDTO(
    val entities: Map<String, WikiData>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WikiData(
    val sitelinks: SiteLinks
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SiteLinks(
    val enwiki: WikiLinks
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WikiLinks(
    val title: String?,
    val url: URL?
)