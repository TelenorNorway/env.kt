package no.telenor.kt.env

/**
 * Can be used to mark a class as constructable with environment variables.
 *
 * @property prefix All subsequent properties/parameters that uses the [Env] parameter annotation will use this prefix
 *                  when getting a value from an environment variable.
 * @property suffix All subsequent properties/parameters that uses the [Env] parameter annotation will use this suffix
 *                  when getting a value from an environment variable.
 */
@Target(AnnotationTarget.CONSTRUCTOR)
annotation class EnvConstructor(
	val prefix: String = "",
	val suffix: String = "",
)
