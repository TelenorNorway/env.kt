package no.telenor.kt.env

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.jvmName

class EnvConstructException(
	val kClass: KClass<*>,
	val constructor: KFunction<*>,
	val problems: List<Triple<KParameter, String?, Throwable>>
) :
	Exception(
		"Could not construct class ${kClass.jvmName}:${
			problems.joinToString("") {
				"\r\n  - parameter ${it.first.name ?: "#${it.first.index}"}${it.second?.let { name -> "($name)" }}${if (it.third::class != Throwable::class) " [${it.third::class.jvmName}]" else ""}: ${it.third.message}"
			}
		}\r\nStacktrace:"
	)
