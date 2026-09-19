package br.com.lucolimac.shesafe.platform

actual fun platformLocationService(): LocationService = IosLocationService()

actual fun platformSmsService(): SmsService = IosSmsService()
