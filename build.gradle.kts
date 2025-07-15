plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("application")
}

application {
	mainClass = "me.rahul.thoughts.ThoughtsApplication"
}

group = "me.rahul"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.sendgrid:sendgrid-java")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	//developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("me.paulschwarz:spring-dotenv:4.0.0")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testRuntimeOnly("com.h2database:h2")

	val mockito = "org.mockito:mockito-core"
	testImplementation(mockito)
	mockitoAgent(mockito) { isTransitive = false }
}

tasks.withType<Test> {
	jvmArgs("-javaagent:${mockitoAgent.asPath}")
	useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
//	options.compilerArgs.add("-parameters")
//	println(options.compilerArgs)
}
