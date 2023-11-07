package no.telenor.kt.env

import no.telenor.kt.env.internal.ParserCollection
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

typealias ParserFn = (type: KType, name: String, value: String) -> Any?

class Construct {
	companion object {
		fun scan(classLoader: ClassLoader) {
			ParserCollection.scan(classLoader)
		}

		private fun <T : Any> getAnnotatedConstructor(klass: KClass<T>): Pair<KFunction<T>, EnvConstructor> {
			var constructor: KFunction<T>? = null
			var envConstructor: EnvConstructor? = null

			if (klass.typeParameters.isNotEmpty()) throw Exception("${klass.jvmName} cannot be constructed with environment variables, class has type parameters")

			for (constr in klass.constructors) {
				val envConstrAnnot = constr.annotations.find {
					it is EnvConstructor
				} ?: continue

				if (constructor != null) {
					throw Exception("${klass.jvmName} cannot be constructed with environment variables, class has more than 1 environmental constructor")
				}

				constructor = constr
				envConstructor = envConstrAnnot as EnvConstructor
			}

			if (constructor == null) {
				throw Exception("${klass.jvmName} cannot be constructed with environment variables, no @EnvConstructor annotation")
			}

			return (constructor to envConstructor!!)
		}

		private val lowerToUpper = Regex("(([a-z][A-Z])|([A-Z]{2}[a-z]))")
		private val lowerToUpperTransform: (MatchResult) -> CharSequence = {
			(it.value.substring(0, 1) + "_" + it.value.substring(1)).lowercase()
		}

		private fun transformNameToUpperSnakeCase(name: String): String {
			return name.replace(lowerToUpper, lowerToUpperTransform).uppercase()
		}

		fun <T : Any> from(klass: KClass<T>): T {
			val (constructor, envConstructorAnnot) = getAnnotatedConstructor(klass)
			val values = mutableMapOf<KParameter, Any?>()
			val problems = mutableListOf<Triple<KParameter, String?, Throwable>>()

			for (parameter in constructor.parameters) {
				val envAnnot = parameter.findAnnotation<Env>()
				val optional = parameter.isOptional
				val nullable = parameter.type.isMarkedNullable

				if (envAnnot == null) {
					if (optional) {
						continue
					}
					if (nullable) {
						values[parameter] = null
						continue
					}
					problems.add(
						Triple(
							parameter,
							null,
							Throwable("not annotated with @Env, parameter is not optional or nullable")
						)
					)
					continue
				}

				val parameterEnvNamePart =
					envAnnot.value.ifBlank { null } ?: parameter.name?.let { transformNameToUpperSnakeCase(it) }

				if (parameterEnvNamePart == null) {
					problems.add(Triple(parameter, null, Throwable("name not inferrable")))
					continue
				}

				val environmentVariableName = "${
					if (envAnnot.noPrefix) "" else envConstructorAnnot.prefix
				}$parameterEnvNamePart${
					if (envAnnot.noSuffix) "" else envConstructorAnnot.suffix
				}"

				val environmentVariableValue = System.getenv(environmentVariableName)

				if (environmentVariableValue == null || environmentVariableValue.isBlank()) {
					if (optional) {
						continue
					} else if (nullable) {
						values[parameter] = null
					} else {
						problems.add(
							Triple(
								parameter,
								environmentVariableName,
								Throwable("parameter cannot be null, no value from environment variable")
							)
						)
					}
					continue
				}

				val value = try {
					parseValue(parameter.type, environmentVariableName, environmentVariableValue)
				} catch (ex: Throwable) {
					problems.add(Triple(parameter, environmentVariableName, ex))
					continue
				}

				if (value == null) {
					if (optional) {
						continue
					} else if (nullable) {
						values[parameter] = null
					} else {
						problems.add(
							Triple(
								parameter,
								environmentVariableName,
								Throwable("parameter cannot be null, parser returned null")
							)
						)
					}
					continue
				}

				values[parameter] = value
			}

			if (problems.isNotEmpty()) throw EnvConstructException(klass, constructor, problems)

			return constructor.callBy(values)
		}

		inline fun <reified T : Any> from() = from(T::class)

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
	}
}
