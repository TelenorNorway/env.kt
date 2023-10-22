package no.telenor.kt.env.internal

import no.telenor.kt.env.Parser
import no.telenor.kt.env.ParserFn
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.jvmErasure

private val lineRegex = Regex("([\\r\\n\\s\\t])+")

private fun classToParserFunctions(cls: Class<*>): Map<KClass<*>, ParserFn> {
	val kClass = cls.kotlin
	if (kClass.visibility != KVisibility.PUBLIC || kClass.typeParameters.isNotEmpty()) return emptyMap()
	val functions = kClass.memberFunctions.map {
		if (!(
				it.visibility == KVisibility.PUBLIC &&
					it.name == "parse${it.returnType.jvmErasure.simpleName}" &&
					it.typeParameters.isEmpty() &&
					!it.returnType.isMarkedNullable &&
					it.parameters.size == 4 &&
					!it.parameters[0].isOptional && it.parameters[0].kind == KParameter.Kind.INSTANCE &&
					!it.parameters[1].isOptional && it.parameters[1].type.jvmErasure == KType::class &&
					!it.parameters[2].isOptional && it.parameters[2].type.jvmErasure == String::class &&
					!it.parameters[3].isOptional && it.parameters[3].type.jvmErasure == String::class
				)
		) return@map null
		return@map it
	}.filterNotNull()

	if (functions.isNotEmpty()) {
		val constructorFn =
			kClass.constructors.firstOrNull { it.typeParameters.isEmpty() && it.parameters.isEmpty() && it.visibility == KVisibility.PUBLIC }
				?: return emptyMap()
		val instance = constructorFn.call()
		if (instance !is Parser) return emptyMap()
		val parsers = mutableMapOf<KClass<*>, ParserFn>()
		for (fn in functions) {
			parsers[fn.returnType.jvmErasure] = { type, name, value ->
				fn.call(instance, type, name, value)!!
			}
		}
		return parsers
	}

	return emptyMap()
}

private val classToParserFunctionsTransformer: (clazz: Class<*>) -> Map<KClass<*>, ParserFn> =
	{ classToParserFunctions(it) }


internal object ParserCollection {
	internal val parsers = mutableMapOf<KClass<*>, ParserFn>()
	private val visited = mutableSetOf<Class<*>>()

	init {
		scan(ClassLoader.getSystemClassLoader())
	}

	fun scan(classLoader: ClassLoader) {
		for (url in classLoader.getResources("META-INF/services/no.telenor.kt.env.Parser")) {
			for (
			classParsers in url
				.openStream()
				.bufferedReader()
				.readText()
				.trim()
				.split(lineRegex)
				.map { classLoader.loadClass(it) }
				.filter {
					val isVisited = !visited.contains(it)
					if (!isVisited) visited.add(it)
					isVisited
				}
				.map(classToParserFunctionsTransformer)
			) {
				parsers.putAll(classParsers)
			}
		}
	}
}
