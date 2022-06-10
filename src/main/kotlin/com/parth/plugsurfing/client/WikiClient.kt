package com.parth.plugsurfing.client

import com.parth.plugsurfing.dto.MusicArtistWikiResponseDTO
import com.parth.plugsurfing.dto.WikipediaResponseDTO
import io.ktor.client.engine.*
import io.ktor.client.request.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class WikiClient(
    httpClientEngine: HttpClientEngine,
    @Value("\${wiki.data.baseUrl}") private val wikiDataBaseUrl: String,
    @Value("\${wiki.pedia.baseUrl}") private val wikipediaBaseUrl: String
) {
    private val wikiDataClient: HttpClient
    private val wikipediaClient: HttpClient

    init {
        wikiDataClient = HttpClient(
            serviceUrl = wikiDataBaseUrl,
            httpClientEngine = httpClientEngine,
            servicePort = null
        )
        wikipediaClient = HttpClient(
            serviceUrl = wikipediaBaseUrl,
            httpClientEngine = httpClientEngine,
            servicePort = null
        )
    }

    suspend fun findWikiDataById(wikiId: String): MusicArtistWikiResponseDTO {
        return wikiDataClient.client.get {
            url {
                encodedPath = "/wiki/Special:EntityData/${wikiId}.json"
            }
        }
    }

    suspend fun findWikipediaDescription(wikipediaId: String): WikipediaResponseDTO {
        return wikipediaClient.client.get {
            url {
                encodedPath = "/api/rest_v1/page/summary/$wikipediaId"
            }
        }
    }
}