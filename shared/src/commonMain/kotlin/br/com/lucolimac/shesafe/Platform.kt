package br.com.lucolimac.shesafe

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
expect fun onApplicationStartPlatformSpecific()