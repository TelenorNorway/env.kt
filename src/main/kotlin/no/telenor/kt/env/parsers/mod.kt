package no.telenor.kt.env.parsers

import no.telenor.kt.env.internal.ParserCollection

fun scan(classLoader: ClassLoader) {
	ParserCollection.scan(classLoader)
}
