package com.parth.plugsurfing.client

import com.parth.plugsurfing.dto.MusicBrainzResponseDTO
import com.parth.plugsurfing.exception.InvalidMBIDRequestException
import com.parth.plugsurfing.exception.MBIDNotFoundException
import com.parth.plugsurfing.exception.ServerBehavedIrresponsiblyException
import com.parth.plugsurfing.exception.SomethingWentWrongException
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MusicBrainzClient(
    httpClientEngine: HttpClientEngine,
    @Value("\${musicBrainz.baseUrl}") private val musicBrainzBaseUrl: String,
) {
    private val httpClient: HttpClient

    init {
        httpClient = HttpClient(
            serviceUrl = musicBrainzBaseUrl,
            httpClientEngine = httpClientEngine,
            servicePort = null
        )
    }

    suspend fun findMusicBrainzArtistData(
        mbid: String
    ): MusicBrainzResponseDTO {
        try {
            return httpClient.client.get {
                url {
                    encodedPath = "/ws/2/artist/$mbid"
                }
                parameter("fmt", "json")
                parameter("inc", "url-rels+release-groups")
            }
        } catch(ex: ClientRequestException) {
            when (ex.response.status) {
                HttpStatusCode.BadRequest -> {
                    throw InvalidMBIDRequestException("MBID: $mbid is invalid!")
                }
                HttpStatusCode.NotFound -> {
                    throw MBIDNotFoundException("MBID: $mbid not found!")
                }
                else -> {
                    throw SomethingWentWrongException("Request failed for mbid: $mbid")
                }
            }
        } catch (ex: ServerResponseException) {
            throw ServerBehavedIrresponsiblyException("Failed to get artist for MusicBrainz for id: $mbid")
        }
    }
}