package no.telenor.kt.env

/**
 * Note, default parameters are not allowed in Java, but they are in Kotlin. Something is screwed up somewhere.
 *
 * Define all parameters when using.
 *
 * https://youtrack.jetbrains.com/issue/IDEA-320939/Unexpected-and-unmodifiable-background-color-on-line-where-cursor-is
 */
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class MapEnv(
	val eq: String = "=",
	val eqRegex: Boolean = false,
	val separator: String = ";",
	val separatorRegex: Boolean = false,
)
