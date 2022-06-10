package com.parth.plugsurfing

import java.net.URL

fun URL.findLastPathSegment(): String {
    return this.toString().split("/").last()
}