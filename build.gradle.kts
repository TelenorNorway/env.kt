plugins {
	kotlin("jvm") version "1.9.20"
	id("sh.tnn") version "0.3.0"
	`maven-publish`
}

group = "no.telenor.kt"

repositories {
	mavenCentral()
	telenor.public()
}

dependencies {
	implementation(kotlin("reflect"))
	testImplementation(kotlin("test"))
	testImplementation("no.telenor.kt:setenv:2.0.2")
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
