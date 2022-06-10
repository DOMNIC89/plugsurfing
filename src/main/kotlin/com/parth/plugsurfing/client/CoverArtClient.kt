package com.parth.plugsurfing.client

import com.parth.plugsurfing.dto.CoverArtImagesDTO
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CoverArtClient(
    httpClientEngine: HttpClientEngine,
    @Value("\${coverArt.baseUrl}") private val coverArtBaseUrl: String,
) {
    private val client: HttpClient
    init {
        client = HttpClient(
            serviceUrl = coverArtBaseUrl,
            httpClientEngine = httpClientEngine,
            servicePort = null,
        )
    }

    suspend fun getCoverArtImageForAlbum(albumId: String):CoverArtImagesDTO {
        return client.client.get {
            url {
                encodedPath = "/release-group/$albumId"
            }
        }
    }
}