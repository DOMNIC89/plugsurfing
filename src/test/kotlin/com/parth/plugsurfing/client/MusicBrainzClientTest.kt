package com.parth.plugsurfing.client

import com.parth.plugsurfing.exception.InvalidMBIDRequestException
import com.parth.plugsurfing.exception.MBIDNotFoundException
import com.parth.plugsurfing.exception.SomethingWentWrongException
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.util.ResourceUtils
import java.nio.file.Files

internal class MusicBrainzClientTest {

    @Test
    fun `given a mbid to findArtistByMBID when response is 200 should return an instance of MusicBrainzResponseDTO`(){
        val mbid = "f27ec8db-af05-4f36-916e-3d57f91ecf5e"
        val responseString = String(Files.readAllBytes(ResourceUtils.getFile("classpath:sample-mbid-request-id.json").toPath()))
        val mockEngine = MockEngine { request ->
            respond(
                content = responseString,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val musicBrainzClient = MusicBrainzClient(mockEngine, "localhost")
        val responseDTO = runBlocking {
            musicBrainzClient.findMusicBrainzArtistData(mbid)
        }
        Assertions.assertNotNull(responseDTO)
        Assertions.assertEquals("Male", responseDTO.gender)
        Assertions.assertEquals("US", responseDTO.country)
        Assertions.assertEquals("“King of Pop”", responseDTO.disambiguation)
    }

    @Test
    fun `given a mbid to findArtistByMBID when response is 400 should throw an InvalidMBIDRequestException`() {
        val mbid = "f27ec8db-af05-4f36-916e-3d57f91ecf5e"
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val musicBrainzClient = MusicBrainzClient(mockEngine, "localhost")
        Assertions.assertThrows(InvalidMBIDRequestException::class.java) {
            runBlocking {
                musicBrainzClient.findMusicBrainzArtistData(
                    mbid
                )
            }
        }
    }

    @Test
    fun `given a mbid to findArtistByMBID when response is 404 should throw an MBIDNotFoundException`() {
        val mbid = "f27ec8db-af05-4f36-916e-3d57f91ecf5e"
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val musicBrainzClient = MusicBrainzClient(mockEngine, "localhost")
        Assertions.assertThrows(MBIDNotFoundException::class.java) {
            runBlocking {
                musicBrainzClient.findMusicBrainzArtistData(
                    mbid
                )
            }
        }
    }

    @Test
    fun `given a mbid to findArtistByMBID when response is 422 should throw SomethingWentWrongException`() {
        val mbid = "f27ec8db-af05-4f36-916e-3d57f91ecf5e"
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.UnprocessableEntity,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val musicBrainzClient = MusicBrainzClient(mockEngine, "localhost")
        Assertions.assertThrows(SomethingWentWrongException::class.java) {
            runBlocking {
                musicBrainzClient.findMusicBrainzArtistData(
                    mbid
                )
            }
        }
    }
}