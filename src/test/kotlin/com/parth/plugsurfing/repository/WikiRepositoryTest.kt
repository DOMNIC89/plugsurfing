package com.parth.plugsurfing.repository

import com.parth.plugsurfing.client.WikiClient
import com.parth.plugsurfing.dto.MusicArtistWikiResponseDTO
import com.parth.plugsurfing.dto.WikipediaResponseDTO
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager

internal class WikiRepositoryTest {

    private val wikiClient = mockk<WikiClient>()
    private val cacheManager = mockk<CacheManager>()
    private lateinit var wikiRepository: WikiRepository

    @BeforeEach
    fun setup() {
        wikiRepository = WikiRepository(wikiClient, cacheManager)
    }

    @Test
    fun `given a wikiId to findWikiDataByWikiID when cache is not present`() {
        every {
           cacheManager.getCache("WikiData")
        } answers {
            null
        }
        val musicArtistWikiResponseDTO = mockk<MusicArtistWikiResponseDTO>()
        coEvery {
            wikiClient.findWikiDataById("wikidataId")
        } answers {
            musicArtistWikiResponseDTO
        }
        runBlocking {
            wikiRepository.findWikiDataByWikiID("wikidataId")
        }
        coVerify {
           wikiClient.findWikiDataById("wikidataId")
        }
    }

    @Test
    fun `given a wikiId to findWikiDataByWikiId when cache is present then should not call wikiClients findWikiDataById`() {
        val mockCache = mockk<Cache>()
        every {
            cacheManager.getCache("WikiData")
        } answers {
            mockCache
        }
        val musicArtistWikiResponseDTO = mockk<MusicArtistWikiResponseDTO>()
        every {
            mockCache.get("wikidataId", MusicArtistWikiResponseDTO::class.java)
        } answers {
            musicArtistWikiResponseDTO
        }
        runBlocking {
            wikiRepository.findWikiDataByWikiID("wikidataId")
        }
        coVerify(exactly = 0) { wikiClient.findWikiDataById("wikidataId") }
    }

    @Test
    fun `given a wikidataId to findDescriptionByWikipediaLink when cache is not present then should not call wikiClients findWikipediaDescription`() {
        val mockCache = mockk<Cache>()
        every {
            cacheManager.getCache("Wikipedia")
        } answers {
            mockCache
        }
        every {
            mockCache.get("Michael_Jackson", WikipediaResponseDTO::class.java)
        } answers {
            null
        }
        val wikipediaResponseDTO = mockk<WikipediaResponseDTO>()
        coEvery {
            wikiClient.findWikipediaDescription("Michael_Jackson")
        } answers {
            wikipediaResponseDTO
        }
        justRun {
            mockCache.put("Michael_Jackson", wikipediaResponseDTO)
        }
        runBlocking {
            wikiRepository.findDescriptionByWikipediaLink("Michael_Jackson")
        }
        coVerify(exactly = 1) {
            wikiClient.findWikipediaDescription("Michael_Jackson")
        }

        verify(exactly = 1) {
            mockCache.put("Michael_Jackson", wikipediaResponseDTO)
        }
    }

    @Test
    fun `given a wikidataId to findDescriptionByWikipedia Link when cache is present then should call wikiClients findWikipediaDescription`() {
        val mockCache = mockk<Cache>()
        every {
            cacheManager.getCache("Wikipedia")
        } answers {
            mockCache
        }
        val wikipediaResponseDTO = mockk<WikipediaResponseDTO>()
        every {
            mockCache.get("Michael_Jackson", WikipediaResponseDTO::class.java)
        } answers {
            wikipediaResponseDTO
        }
        runBlocking {
            wikiRepository.findDescriptionByWikipediaLink("Michael_Jackson")
        }
        coVerify(exactly = 0) {
            wikiClient.findWikipediaDescription("Michael_Jackson")
        }
        verify (exactly = 0) {
            mockCache.put("Michael_Jackson", wikipediaResponseDTO)
        }
    }

}