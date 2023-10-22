package no.telenor.kt.env

import no.telenor.kt.env.internal.ParserCollection
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

typealias ParserFn = (type: KType, name: String, value: String) -> Any?

fun parseValue(type: KType, name: String, value: String): Any? {
	val parser = ParserCollection.parsers[type.jvmErasure]
		?: throw Throwable("No parser for '${type.jvmErasure}' was found!")
	return parser(type, name, value)
}
