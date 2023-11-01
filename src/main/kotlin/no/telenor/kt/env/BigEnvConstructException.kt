package no.telenor.kt.env

import kotlin.reflect.jvm.jvmName

private val newLineRegex = Regex("\r?\n")

class BigEnvConstructException(
	val parent: EnvConstructException
) :
	Exception(
		"Could not construct class ${parent.kClass.jvmName}:${
			parent.problems.joinToString("") {
				"\r\n  - parameter ${it.first.name ?: "#${it.first.index}"}${it.second?.let { name -> "($name)" }} ${
					it.third.stackTraceToString().split(newLineRegex).joinToString("\r\n") { str -> "      $str" }.trim()
				}"
			}
		}\r\nStacktrace:"
	)
