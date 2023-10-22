import no.telenor.kt.EnvironmentSnapshot
import no.telenor.kt.clearEnv
import no.telenor.kt.env.Env
import no.telenor.kt.env.EnvConstructor
import no.telenor.kt.env.ListEnv
import no.telenor.kt.env.construct
import no.telenor.kt.envSnapshot
import no.telenor.kt.restoreEnv
import no.telenor.kt.setenv
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

data class TestClass @EnvConstructor(prefix = "TEST_") constructor(
	@Env val int: Int,
	@Env val numbers: @ListEnv(separator = ";") List<@ListEnv(separator = ".") List<Int>>
)

class Tests {

	// region cleanup
	private var snapshot: EnvironmentSnapshot? = null

	@BeforeTest
	fun saveSnapshot() {
		snapshot = envSnapshot()
		clearEnv()
	}

	@AfterTest
	fun restoreSnapshot() {
		snapshot?.let { restoreEnv(it) }
	}

	// endregion

	@Test
	fun `construct simple data class`() {
		setenv(
			"TEST_INT" to "123",
			"TEST_NUMBERS" to "1.2;3.4;5.6;7.8"
		)
		val derived = construct<TestClass>()
		assertEquals(derived.int, 123)
		assertEquals(derived.numbers, listOf(listOf(1, 2), listOf(3, 4), listOf(5, 6), listOf(7, 8)))
	}

}
