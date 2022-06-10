package com.parth.plugsurfing.service

import com.parth.plugsurfing.dto.*
import com.parth.plugsurfing.model.ArtistAlbum
import com.parth.plugsurfing.repository.CoverArtRepository
import com.parth.plugsurfing.repository.MusicBrainzRepository
import com.parth.plugsurfing.repository.WikiRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URL
import java.util.*

internal class MusicArtistServiceTest {

    private val musicBrainzRepository = mockk<MusicBrainzRepository>()
    private val wikiRepository = mockk<WikiRepository>()
    private val coverArtRepository = mockk<CoverArtRepository>()
    private lateinit var musicArtistService: MusicArtistService

    @BeforeEach
    fun setup() {
        musicArtistService = MusicArtistService(
            musicBrainzRepository = musicBrainzRepository,
            wikiRepository = wikiRepository,
            covertArtRepository =  coverArtRepository,
        )
    }

    @Test
    fun `given a mbid to findArtistByMBID when the MusicBrainz artist is present then should return the `() {
        val mbid = UUID.randomUUID().toString()
        val mockMusicBrainzResponseDTO = mockk<MusicBrainzResponseDTO>()
        coEvery {
            musicBrainzRepository.findMusicBrainzArtistByMBID(mbid)
        } answers {
            mockMusicBrainzResponseDTO
        }
        coEvery {
            mockMusicBrainzResponseDTO.id
        } answers {
            mbid
        }
        coEvery { 
            mockMusicBrainzResponseDTO.name
        } answers {
            "Michael Jackson"
        }
        coEvery {
            mockMusicBrainzResponseDTO.country
        } answers {
            "US"
        }
        coEvery {
            mockMusicBrainzResponseDTO.gender
        } answers {
            "Male"
        }
        coEvery {
            mockMusicBrainzResponseDTO.disambiguation
        } answers {
            "King of Pop"
        }

        val musicBrainzRelationsDTO = MusicBrainzRelationsDTO(
            type = "WikiData",
            ended = false,
            urlResourceDTO = URLResourceDTO(URL("https://www.wikidata.org/wiki/Q2831"), id = "27011c1f-bd4f-474c-ae04-23e00f79ccd9")
        )
        coEvery {
            mockMusicBrainzResponseDTO.relations
        } answers {
            listOf(musicBrainzRelationsDTO)
        }
        val albumIdString = UUID.randomUUID().toString()
        val releaseGroup = ReleaseGroupsDTO(
            id = albumIdString,
            title = "Michael Jackson Song",
            type = "Album"
        )
        coEvery {
            mockMusicBrainzResponseDTO.releaseGroups
        } answers {
            listOf(releaseGroup)
        }
        val musicArtistWikiResponseDTO = MusicArtistWikiResponseDTO(
            entities = mapOf("Q2831" to WikiData(sitelinks = SiteLinks(enwiki = WikiLinks(title = "Michael Jackson", URL("https://en.wikipedia.org/wiki/Michael_Jackson")))))
        )
        coEvery {
            wikiRepository.findWikiDataByWikiID("Q2831")
        } answers {
            musicArtistWikiResponseDTO
        }
        coEvery {
            wikiRepository.findDescriptionByWikipediaLink("Michael_Jackson")
        } answers {
            WikipediaResponseDTO(
                title = "Michael Jackson",
                extractHtml = "Sample Extract Text html"
            )
        }
        val artistAlbum = ArtistAlbum(
            id = albumIdString,
            title = "Michael Jackson Song",
        )
        coEvery {
            coverArtRepository.findCoverArtImageUrl(artistAlbum)
        } answers {
            "http://coverartarchive.org/release/51258fa4-29c5-4b86-bfda-b630573ec222/26167697750.jpg"
        }
        val musicArtistResponseDTO = runBlocking {
            musicArtistService.findArtistByMBID(mbid)
        }
        Assertions.assertNotNull(musicArtistResponseDTO)
        Assertions.assertEquals("http://coverartarchive.org/release/51258fa4-29c5-4b86-bfda-b630573ec222/26167697750.jpg", musicArtistResponseDTO.albums[0].imageUrl)
    }



}