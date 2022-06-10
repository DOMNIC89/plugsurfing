package com.parth.plugsurfing.repository

import com.parth.plugsurfing.exception.AlbumCoverArtNotFound
import com.parth.plugsurfing.exception.InvalidMBIDRequestException
import com.parth.plugsurfing.exception.ServerBehavedIrresponsiblyException
import com.parth.plugsurfing.exception.SomethingWentWrongException
import com.parth.plugsurfing.model.ArtistAlbum
import fm.last.musicbrainz.coverart.CoverArtArchiveClient
import io.ktor.client.features.*
import io.ktor.http.*
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class CoverArtRepository(
    private val coverArtArchiveClient: CoverArtArchiveClient,
    private val cacheManager: CacheManager,
) {

    @CachePut(value=["CoverImage"], key="#artistAlbum.id")
    fun findCoverArtImageUrl(artistAlbum: ArtistAlbum): String {
        return if (cacheManager.getCache("CoverImage")?.get(artistAlbum.id, String::class.java) != null) {
            cacheManager.getCache("CoverImage")?.get(artistAlbum.id, String::class.java)!!
        } else {
            try {
                val imageUrl = coverArtArchiveClient.getReleaseGroupByMbid(UUID.fromString(artistAlbum.id))?.frontImage?.imageUrl ?: ""
                cacheManager.getCache("CoverImage")?.put(artistAlbum.id, imageUrl)
                imageUrl
            } catch (ex: ClientRequestException) {
                when(ex.response.status) {
                    HttpStatusCode.BadRequest -> throw InvalidMBIDRequestException("Invalid MBID for album with id: ${artistAlbum.id}")
                    HttpStatusCode.NotFound -> throw AlbumCoverArtNotFound("Not able to find Cover art for album: ${artistAlbum.title}")
                    else -> throw SomethingWentWrongException("Unable to execute the request, the exact cause: ${ex.message}")
                }
            } catch (ex: ServerResponseException) {
                throw ServerBehavedIrresponsiblyException("CoverArt Server threw error with message: ${ex.message}")
            }
        }
    }
}