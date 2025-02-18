plugins {
  idea

  val kotlinVersion = "2.1.0"

  kotlin("jvm") version kotlinVersion
  kotlin("plugin.spring") version kotlinVersion
  kotlin("plugin.jpa") version kotlinVersion

  id("org.springframework.boot") version "3.4.2"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "de.xehmer"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
  mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-validation")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation(kotlin("reflect"))

  developmentOnly("org.springframework.boot:spring-boot-devtools")

  runtimeOnly("com.h2database:h2")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(kotlin("test-junit5"))
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

allOpen {
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.MappedSuperclass")
  annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
  useJUnitPlatform()
  jvmArgs("-javaagent:${mockitoAgent.asPath}")
}
