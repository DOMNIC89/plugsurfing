package com.parth.plugsurfing.repository

import com.parth.plugsurfing.client.MusicBrainzClient
import com.parth.plugsurfing.dto.MusicBrainzResponseDTO
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import java.util.*

internal class MusicBrainzRepositoryTest {

    private val musicBrainzClientMock = mockk<MusicBrainzClient>()
    private val cacheManager = mockk<CacheManager>()
    private lateinit var musicBrainzRepository: MusicBrainzRepository

    @BeforeEach
    fun setup() {
        musicBrainzRepository = MusicBrainzRepository(musicBrainzClientMock, cacheManager)
    }

    @Test
    fun `given an mbid to findMusicBrainzArtistByMBID when cache does not have the value then should call MusicBrainzClient findMusicBrainzArtistData` () {
        val mbid = UUID.randomUUID().toString()
        val cache = mockk<Cache>()
        every {
            cacheManager.getCache("ArtistAlbums")
        } answers {
            cache
        }
        every {
            cache.get(mbid, MusicBrainzResponseDTO::class.java)
        } answers {
            null
        }
        val musicBrainzResponseDTO = mockk<MusicBrainzResponseDTO>()
        coEvery {
            musicBrainzClientMock.findMusicBrainzArtistData(mbid)
        } answers {
            musicBrainzResponseDTO
        }
        justRun { cache.put(mbid, musicBrainzResponseDTO) }
        runBlocking {
            musicBrainzRepository.findMusicBrainzArtistByMBID(mbid)
        }
        coVerify(exactly = 1) {
            musicBrainzClientMock.findMusicBrainzArtistData(mbid)
        }
        verify {
            cache.put(mbid, musicBrainzResponseDTO)
        }
    }

    @Test
    fun `given a mbid to findMusicBrainzArtistByMBID when cache has value then should not call MusicBrainzClient findMusicBrainzArtistData`() {
        val mbid = UUID.randomUUID().toString()
        val cache = mockk<Cache>()
        every {
            cacheManager.getCache("ArtistAlbums")
        } answers {
            cache
        }
        val musicBrainzResponseDTO = mockk<MusicBrainzResponseDTO>()
        every {
            cache.get(mbid, MusicBrainzResponseDTO::class.java)
        } answers {
            musicBrainzResponseDTO
        }
        runBlocking {
            musicBrainzRepository.findMusicBrainzArtistByMBID(mbid)
        }
        coVerify(exactly = 0) { musicBrainzClientMock.findMusicBrainzArtistData(mbid) }
        verify(exactly = 0) { cache.put(mbid, musicBrainzResponseDTO) }
    }
}