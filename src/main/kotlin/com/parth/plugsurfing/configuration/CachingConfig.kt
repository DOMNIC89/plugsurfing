package com.parth.plugsurfing.configuration

import fm.last.musicbrainz.coverart.CoverArtArchiveClient
import fm.last.musicbrainz.coverart.impl.DefaultCoverArtArchiveClient
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CachingConfig {

    @Bean
    fun cacheManager(): CacheManager {
        return ConcurrentMapCacheManager("ArtistAlbums", "CoverImage", "WikiData", "Wikipedia")
    }

    @Bean
    fun httpClientEngine(): HttpClientEngine = CIO.create()

    @Bean
    fun coverArtArchiveClient(): CoverArtArchiveClient {
        return DefaultCoverArtArchiveClient()
    }
}