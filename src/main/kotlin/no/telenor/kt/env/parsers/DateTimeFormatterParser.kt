@file:Suppress("UNUSED_PARAMETER")

package no.telenor.kt.env.parsers

import no.telenor.kt.env.Parser
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.reflect.KType

class DateTimeFormatterParser : Parser {
	fun parseDateTimeFormatter(type: KType, name: String, value: String): DateTimeFormatter =
		DateTimeFormatter.ofPattern(value).withZone(ZoneId.systemDefault())
}
