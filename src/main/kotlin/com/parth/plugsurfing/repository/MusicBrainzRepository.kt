package com.parth.plugsurfing.repository

import com.parth.plugsurfing.client.MusicBrainzClient
import com.parth.plugsurfing.dto.MusicBrainzResponseDTO
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Repository

@Repository
class MusicBrainzRepository(
    private val musicBrainzClient: MusicBrainzClient,
    private val cacheManager: CacheManager
){
    @CachePut(value= ["ArtistAlbums"], key = "#mbid")
    suspend fun findMusicBrainzArtistByMBID(mbid: String): MusicBrainzResponseDTO {
        val musicBrainzResponseDTO = if (cacheManager.getCache("ArtistAlbums")?.get(mbid, MusicBrainzResponseDTO::class.java) != null) {
            cacheManager.getCache("ArtistAlbums")?.get(mbid, MusicBrainzResponseDTO::class.java)!!
        } else {
            val response = musicBrainzClient.findMusicBrainzArtistData(mbid)
            cacheManager.getCache("ArtistAlbums")?.put(mbid, response)
            response
        }
        return musicBrainzResponseDTO
    }
}