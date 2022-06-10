package com.parth.plugsurfing.repository

import com.parth.plugsurfing.client.WikiClient
import com.parth.plugsurfing.dto.MusicArtistWikiResponseDTO
import com.parth.plugsurfing.dto.WikipediaResponseDTO
import com.parth.plugsurfing.exception.ServerBehavedIrresponsiblyException
import com.parth.plugsurfing.exception.SomethingWentWrongException
import io.ktor.client.features.*
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class WikiRepository(
    private val wikiClient: WikiClient,
    private val cacheManager: CacheManager
) {

    @CachePut(value = ["WikiData"], key="#wikiId")
    suspend fun findWikiDataByWikiID(wikiId: String): MusicArtistWikiResponseDTO {
        val musicArtistWikiResponseDTO = cacheManager.getCache("WikiData")?.get(wikiId, MusicArtistWikiResponseDTO::class.java)
        if (Objects.nonNull(musicArtistWikiResponseDTO)) {
            return musicArtistWikiResponseDTO!!
        }
        try {
            val newResponse = wikiClient.findWikiDataById(wikiId)
            cacheManager.getCache("WikiData")?.put(wikiId, newResponse)
            return newResponse
        } catch (ex: ClientRequestException) {
            throw SomethingWentWrongException(ex.message)
        } catch (ex: ServerResponseException) {
            throw ServerBehavedIrresponsiblyException("WikiData request failed for wikiId: $wikiId")
        }
    }

    @CachePut(value = ["Wikipedia"], key="#wikiId")
    suspend fun findDescriptionByWikipediaLink(wikiId: String): WikipediaResponseDTO {
         val wikipediaResponseDTO = cacheManager.getCache("Wikipedia")?.get(wikiId, WikipediaResponseDTO::class.java)
        if (Objects.nonNull(wikipediaResponseDTO)) {
            return wikipediaResponseDTO!!
        }
        try {
            val newWikipediaResponseDTO = wikiClient.findWikipediaDescription(wikiId)
            cacheManager.getCache("Wikipedia")?.put(wikiId, newWikipediaResponseDTO)
            return newWikipediaResponseDTO
        } catch(ex: ClientRequestException) {
            throw SomethingWentWrongException(ex.message)
        } catch (ex: ServerBehavedIrresponsiblyException) {
            throw ServerBehavedIrresponsiblyException("Wikipedia request failed for wikiId: $wikiId")
        }
    }
}