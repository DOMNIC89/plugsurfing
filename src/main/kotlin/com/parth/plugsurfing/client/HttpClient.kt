package com.parth.plugsurfing.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.http.*

class HttpClient(
    private val serviceUrl: String,
    private val urlProtocol: String = "https",
    private val httpClientEngine: HttpClientEngine,
    private val timeout: Long = 60,
    private val servicePort: Int?,
) {
    val client: HttpClient
    init {
        client = newDefaultHttpClient()
    }

    private fun newDefaultHttpClient(): HttpClient {
        return HttpClient(httpClientEngine) {
            install(JsonFeature) {
                serializer = JacksonSerializer {
                    jacksonObjectMapper()
                }
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }

            defaultRequest {
                timeout {
                    timeout * 1000
                }
                url {
                    protocol = URLProtocol.byName.getOrDefault(urlProtocol.lowercase(), URLProtocol.HTTPS)
                    host = serviceUrl
                }
            }
        }
    }
}