
val vrsn = project.file("VERSION").readText().trim();
project.version = vrsn.toString();

tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

plugins {
    id("java-library")
    id("edu.wpi.first.GradleRIO") version "2025.2.1"
    id("maven-publish")
}

tasks.javadoc {
    destinationDir = file("./docs/html/javadoc")
}

repositories {
    mavenCentral()
}

dependencies {
    api("edu.wpi.first.wpilibj:wpilibj-java:2025.2.1")
    api("edu.wpi.first.wpiutil:wpiutil-java:2025.2.1")
    api("org.tomlj:tomlj:1.1.1")

    // Test imports
    testImplementation("edu.wpi.first.wpilibj:wpilibj-java:2025.2.1")
    testImplementation("edu.wpi.first.wpiutil:wpiutil-java:2025.2.1")
    testImplementation("org.tomlj:tomlj:1.1.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter (JUnit 5) test framework
            useJUnitJupiter()
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/frc4206/battleaid")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr"){
            groupId = "org.team4206"
            from(components["java"])
            artifact(tasks["sourceJar"])
        }
    }
}
