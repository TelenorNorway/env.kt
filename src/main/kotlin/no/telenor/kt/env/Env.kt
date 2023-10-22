package no.telenor.kt.env

/**
 * Can be used on constructor parameters to indicate the parameter should be collected from an environment variable.
 *
 * @property value The name of the environment variable to read, defaults to the property name converted to upper snake case.
 * @property noPrefix Do not use a prefix defined in the [EnvConstructor] annotation.
 * @property noSuffix Do not use a suffix defined in the [EnvConstructor] annotation.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Env(
	val value: String = "",
	val noPrefix: Boolean = false,
	val noSuffix: Boolean = false,
)
