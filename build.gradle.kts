plugins {
	java
}

group = "com.gridnine"
version = "0.0.1-SNAPSHOT"
description = "Flight filters kata (plain Java)"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

