# Env.kt

_Derive data classes from environment variables seamlessly._

## Dependency

In your gradle file

_Follow [this guide](https://github.com/testersen/no.ghpkg) on how to set up your environment for GitHub packages._

```kt
plugins {
	id("no.ghpkg") version "0.3.3"
}

repositories {
	git.hub("telenornorway", "env.kt")
	// or <.. the below> if you're spicy 🌶️
	// git.hub("telenornorway")
}

dependencies {
	implementation("no.telenor.kt:env:<VERSION HERE>")
}
```

## Usage

```kt
data class MyConfiguration @EnvConstructor(prefix = "MY_CONFIG_") constructor(
	// @Env will try to infer the name of the parameter, transforming it from camel/pascal case to UPPER_SNAKE_CASE
	// this will equal: MY_CONFIG_NUMBERS because of the prefix.
	@Env val numbers: List<Int>,
	// Prefixes can be disabled with noPrefix = true
	// the name will result to: MY_BOOLEAN
	@Env(noPrefix = true) val myBoolean: Boolean,
	// Names can also be overridden
	@Env("MY_OVERRIDE") val somethingCrazyLong: String,
	// Lists can use custom separators, also nested. List separator defaults to `,`. Note that this is not supported with
	// arrays because java/jvm is s@$!
	// feeding this "1.2;3.4;5.6" will result in [[1,2], [3,4], [5,6]]
	@Env val crazyList: @ListEnv(separator = ";") List<@ListEnv(separator = ".") List<Int>>,
)
```

## Advanced

### Adding your own parsers

```kt
package my.very.cool.website

class Feature(val value: String)

// The class must implement Parser, lame right..
class MyParser : Parser {
	// Note that the name of the function MUST begin with parse and end
	// with the class name of what it returns.
	// For example, LocalDateTime.
	// fun parseLocalDateTime(type: KType, name: String, value: String): LocalDateTime
	fun parseFeature(type: KType, name: String, value: String): Feature {
		return Feature(value)
	}
}
```

Now create a new file in the resources folder: `META-INF/services/no.telenor.kt.env.Parser` with a line separated list
of your parser implementations.
For example:

```
my.very.cool.website.MyParser
```

If this is used in the same classloader as the main application, this will be included automatically. But, if the
contents is dynamically linked through a separate jar file, the `ClassLoader` for that jar file must be scanned.

```kt
fun main() {
	val dynamicClassLoader = getClassLoaderSomehowFor("my-mod.jar")
	no.telenor.kt.env.parsers.scan(dynamicClassLoader)
}
```
