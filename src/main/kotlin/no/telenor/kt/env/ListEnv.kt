package no.telenor.kt.env

/**
 * Note, default parameters are not allowed in Java, but they are in Kotlin. Something is screwed up somewhere.
 *
 * Define all parameters when using.
 *
 * https://youtrack.jetbrains.com/issue/IDEA-320939/Unexpected-and-unmodifiable-background-color-on-line-where-cursor-is
 */
@Target(AnnotationTarget.TYPE)
annotation class ListEnv(
	val separator: String = ",",
	val regex: Boolean = false
)
