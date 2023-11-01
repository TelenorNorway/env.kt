package no.telenor.kt.env

@Target(AnnotationTarget.TYPE)
annotation class MapEnv(
	/** Separates key and value */
	val eq: String = "=",
	/** Separates key1=value1 key2=value2 */
	val separator: String = ";",
)
