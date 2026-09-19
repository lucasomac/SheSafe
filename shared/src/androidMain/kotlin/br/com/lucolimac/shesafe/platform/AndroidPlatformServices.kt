package br.com.lucolimac.shesafe.platform

actual fun platformLocationService(): LocationService =
    error("Android location service requires an Android Context")

actual fun platformSmsService(): SmsService =
    error("Android SMS service requires an Android Context")
