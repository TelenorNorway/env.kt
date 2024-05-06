package no.telenor.kt.env.internal

internal fun debug(message: String) {
	if (System.getenv("KT_ENV_DEBUG") == "true") {
		println("KT_ENV_DEBUG = $message")
	}
}
