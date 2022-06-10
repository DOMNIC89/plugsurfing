package com.parth.plugsurfing.repository

import com.parth.plugsurfing.model.ArtistAlbum
import fm.last.musicbrainz.coverart.CoverArt
import fm.last.musicbrainz.coverart.CoverArtArchiveClient
import fm.last.musicbrainz.coverart.CoverArtImage
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import java.util.*

internal class CoverArtRepositoryTest {

    private val coverArtArchiveClient = mockk<CoverArtArchiveClient>()
    private val cacheManager = mockk<CacheManager>()
    private lateinit var coverArtRepository: CoverArtRepository

    @BeforeEach
    fun setup() {
        coverArtRepository = CoverArtRepository(
            coverArtArchiveClient = coverArtArchiveClient,
            cacheManager = cacheManager,
        )
    }

    @Test
    fun `given an artistAlbum to findCoverArtImageUrl when the cache is not present then should call coverArtArchiveClient getReleaseGroupByMbid`() {
        val id = UUID.randomUUID().toString()
        val mockCache = mockk<Cache>()
        every {
            cacheManager.getCache("CoverImage")
        } answers {
            mockCache
        }
        every {
            mockCache.get(id, String::class.java)
        } answers {
            null
        }
        val coverArt = mockk<CoverArt>()
        every {
            coverArtArchiveClient.getReleaseGroupByMbid(UUID.fromString(id))
        } answers {
            coverArt
        }
        val coverArtImage = mockk<CoverArtImage>()
        every {
            coverArt.frontImage
        } answers {
            coverArtImage
        }
        every {
            coverArtImage.imageUrl
        } answers {
            "http://parth.com/logo.png"
        }
        justRun {
            mockCache.put(id, "http://parth.com/logo.png")
        }
        val artistAlbum = ArtistAlbum(id = id, title = "Be there for you!")
        coverArtRepository.findCoverArtImageUrl(artistAlbum)
        verify(exactly = 1) {
            coverArtArchiveClient.getReleaseGroupByMbid(UUID.fromString(id))
        }
    }

    @Test
    fun `given an artistAlbum to findCoverArtImageUrl when the cache is present then should not call coverArtArchiveClient getReleaseGroupByMbid`() {
        val id = UUID.randomUUID().toString()
        val mockCache = mockk<Cache>()
        every {
            cacheManager.getCache("CoverImage")
        } answers {
            mockCache
        }
        val coverArt = mockk<CoverArt>()
        every {
            mockCache.get(id, String::class.java)
        } answers {
            "http://parth.com/logo.png"
        }

        val coverArtImage = mockk<CoverArtImage>()
        every {
            coverArt.frontImage
        } answers {
            coverArtImage
        }
        every {
            coverArtImage.imageUrl
        } answers {
            "http://parth.com/logo.png"
        }
        justRun {
            mockCache.put(id, "http://parth.com/logo.png")
        }
        val artistAlbum = ArtistAlbum(id = id, title = "Be there for you!")
        coverArtRepository.findCoverArtImageUrl(artistAlbum)
        verify(exactly = 0) {
            coverArtArchiveClient.getReleaseGroupByMbid(UUID.fromString(id))
        }
    }
}
