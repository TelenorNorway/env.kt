package no.telenor.kt.env

import no.telenor.kt.env.internal.ParserCollection
import kotlin.reflect.KType
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.jvmErasure

typealias ParserFn = (type: KType, name: String, value: String) -> Any?

fun parseValue(type: KType, name: String, value: String): Any? {
	if (type.jvmErasure.java.isEnum) {
		return parseEnum(type, name, value)
	}
	val parser = ParserCollection.parsers[type.jvmErasure]
		?: throw Throwable("No parser for '${type.jvmErasure}' was found!")
	return parser(type, name, value)
}

fun parseEnum(
	type: KType,
	@Suppress("UNUSED_PARAMETER") name: String,
	value: String
): Any? {
	return type.jvmErasure.functions.first { it.name == "valueOf" }.call(value)
}
