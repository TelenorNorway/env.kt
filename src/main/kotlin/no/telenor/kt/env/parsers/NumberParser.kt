@file:Suppress("UNUSED_PARAMETER")

package no.telenor.kt.env.parsers

import no.telenor.kt.env.Parser
import kotlin.math.min
import kotlin.reflect.KType

class NumberParser : Parser {
	private fun radix(
		value: String
	): Triple<String, Int, String> {
		val signed = value.substring(0, min(1, value.length)).let { s -> s == "-" || s == "+" }
		val sign = if (signed) value.substring(0, min(1, value.length)) else ""
		var number = if (signed) value.substring(min(1, value.length)) else value
		val radix = when (number.substring(0, min(2, number.length))) {
			"0b" -> 2
			"0o" -> 8
			"0x" -> 16
			else -> 10
		}
		number = sign + (if (radix == 10) number else number.substring(min(2, value.length)))
		return Triple(number, radix, sign)
	}

	fun parseByte(type: KType, name: String, value: String) = radix(value).let { it.first.toByte(it.second) }
	fun parseShort(type: KType, name: String, value: String) = radix(value).let { it.first.toShort(it.second) }
	fun parseInt(type: KType, name: String, value: String) = radix(value).let { it.first.toInt(it.second) }
	fun parseLong(type: KType, name: String, value: String) = radix(value).let { it.first.toLong(it.second) }

	fun parseUByte(type: KType, name: String, value: String) = radix(value).let { it.first.toUByte(it.second) }
	fun parseUShort(type: KType, name: String, value: String) = radix(value).let { it.first.toUShort(it.second) }
	fun parseUInt(type: KType, name: String, value: String) = radix(value).let { it.first.toUInt(it.second) }
	fun parseULong(type: KType, name: String, value: String) = radix(value).let { it.first.toULong(it.second) }

	fun parseFloat(type: KType, name: String, value: String) = value.toFloat()
	fun parseDouble(type: KType, name: String, value: String) = value.toDouble()
	fun parseBigDecimal(type: KType, name: String, value: String) = value.toBigDecimal()
}
