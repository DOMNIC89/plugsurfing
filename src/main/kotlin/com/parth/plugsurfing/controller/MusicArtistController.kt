package com.parth.plugsurfing.controller

import com.parth.plugsurfing.dto.MusicArtistResponseDTO
import com.parth.plugsurfing.service.MusicArtistService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/musify/music-artist")
class MusicArtistController(
    private val musicArtistService: MusicArtistService,
) {

    @GetMapping("/details/{mbid}")
    suspend fun getMusicArtistDetails(@PathVariable mbid: String): ResponseEntity<MusicArtistResponseDTO> {
        return ResponseEntity.ok(musicArtistService.findArtistByMBID(mbid))
    }
}