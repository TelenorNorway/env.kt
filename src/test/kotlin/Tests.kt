import no.telenor.kt.env.BigEnvConstructException
import no.telenor.kt.env.Env
import no.telenor.kt.env.EnvConstructException
import no.telenor.kt.env.EnvConstructor
import no.telenor.kt.env.Environment
import no.telenor.kt.env.EnvironmentSnapshot
import no.telenor.kt.env.ListEnv
import no.telenor.kt.env.MapEnv
import no.telenor.kt.env.construct
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

enum class Lol {
	Foo,
	Bar,
	Baz
}

data class TestClass @EnvConstructor(prefix = "TEST_") constructor(
	@Env val int: Int,
	@Env val numbers: @ListEnv(separator = ";") List<@ListEnv(separator = ".") List<Int>>,
	@Env val lol: Lol,
	@Env val map1: Map<String, String>,
	@Env val map2: @MapEnv(eq = ":", eqRegex = false, separator = ",", separatorRegex = false) Map<String, String>,
	@Env val map3: @MapEnv(
		eq = "=",
		eqRegex = false,
		separator = "[\\s\\r\\n]+",
		separatorRegex = true
	) Map<String, String>,
)

class Tests {

	// region cleanup
	private var snapshot: EnvironmentSnapshot? = null

	@BeforeTest
	fun saveSnapshot() {
		snapshot = Environment.clear()
	}

	@AfterTest
	fun restoreSnapshot() {
		snapshot?.let { Environment.restore(it) }
	}

	// endregion

	@Test
	fun `construct simple data class`() {
		Environment.set(
			"TEST_INT" to "123",
			"TEST_NUMBERS" to "1.2;3.4;5.6;7.8",
			"TEST_LOL" to "Foo",
			"TEST_MAP1" to "john=doe;foo=bar=baz",
			"TEST_MAP2" to "john:doe,foo:bar:baz",
			"TEST_MAP3" to "john=doe foo=bar=baz\r\nthis=is=cool",
		)
		val derived = try {
			construct<TestClass>()
		} catch (ex: EnvConstructException) {
			throw BigEnvConstructException(ex)
		}
		assertEquals(derived.int, 123)
		assertEquals(derived.numbers, listOf(listOf(1, 2), listOf(3, 4), listOf(5, 6), listOf(7, 8)))
		assertEquals(derived.lol, Lol.Foo)
		assertEquals(derived.map1, mapOf("john" to "doe", "foo" to "bar=baz"))
		assertEquals(derived.map2, mapOf("john" to "doe", "foo" to "bar:baz"))
		assertEquals(derived.map3, mapOf("john" to "doe", "foo" to "bar=baz", "this" to "is=cool"))
	}

}
