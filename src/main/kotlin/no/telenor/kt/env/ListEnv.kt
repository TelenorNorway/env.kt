package no.telenor.kt.env

@Target(AnnotationTarget.TYPE)
annotation class ListEnv(
	val separator: String = ",",
	/** THIS DOES NOT WORK CURRENTLY */
	val regex: Boolean = false
)
