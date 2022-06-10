package com.parth.plugsurfing.service

import com.parth.plugsurfing.dto.AlbumDTO
import com.parth.plugsurfing.dto.MusicArtistResponseDTO
import com.parth.plugsurfing.exception.SomethingWentWrongException
import com.parth.plugsurfing.findLastPathSegment
import com.parth.plugsurfing.model.ArtistAlbum
import com.parth.plugsurfing.repository.CoverArtRepository
import com.parth.plugsurfing.repository.MusicBrainzRepository
import com.parth.plugsurfing.repository.WikiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class MusicArtistService(
    private val musicBrainzRepository: MusicBrainzRepository,
    private val wikiRepository: WikiRepository,
    private val covertArtRepository: CoverArtRepository,
) {

    suspend fun findArtistByMBID(mbid: String): MusicArtistResponseDTO {
        val musicBrainzResponseDTO = musicBrainzRepository.findMusicBrainzArtistByMBID(mbid)
        // Find the wiki
        val wikiUrl =
            musicBrainzResponseDTO.relations.find { it.type.lowercase() == "wikidata" && it.ended.not() }?.urlResourceDTO?.resource
                ?: throw SomethingWentWrongException("Unable to find the wikidata information")
        val artistAlbums = musicBrainzResponseDTO.releaseGroups.filter { it.type == "Album" }
            .map { ArtistAlbum(it.id, it.title) }
        val wikiId = wikiUrl.findLastPathSegment()
        val wikiResponseDTO = wikiRepository.findWikiDataByWikiID(wikiId)
        val wikipediaId = wikiResponseDTO.entities[wikiId]?.sitelinks?.enwiki?.url?.findLastPathSegment() ?: ""
        val description = wikiRepository.findDescriptionByWikipediaLink(wikipediaId).extractHtml
        // list all albums and covers
        val albums = mutableListOf<ArtistAlbum>()
        repeat(artistAlbums.size) { index ->
            val artistAlbum = artistAlbums[index]
            withContext(Dispatchers.IO) {
                val imageUrl = covertArtRepository.findCoverArtImageUrl(artistAlbum)
                val updatedArtistAlbum = artistAlbum.copy(albumCoverImage = imageUrl)
                albums.add(updatedArtistAlbum)
            }
         }
        return MusicArtistResponseDTO(
            mbid = musicBrainzResponseDTO.id,
            name = musicBrainzResponseDTO.name,
            gender = musicBrainzResponseDTO.gender,
            country = musicBrainzResponseDTO.country,
            disambiguation = musicBrainzResponseDTO.disambiguation,
            description = description,
            albums = albums.map { AlbumDTO(it.id, it.title, it.albumCoverImage) }.toList()
        )
    }
}