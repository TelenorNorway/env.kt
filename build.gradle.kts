plugins {
	kotlin("jvm") version "1.9.0"
	id("no.ghpkg") version "0.3.3"
	`maven-publish`
}

group = "no.telenor.kt"
version = versioning.environment()

repositories {
	mavenCentral()
	git.hub("telenornorgeinternal", "abc123")
}

dependencies {
	implementation(kotlin("reflect"))
	testImplementation(kotlin("test"))
	testImplementation("no.telenor.kt:setenv:2.0.0")
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(17)
}

publishing {
	repositories.github.actions()
	publications.register<MavenPublication>("gpr") {
		from(components["kotlin"])
	}
}
