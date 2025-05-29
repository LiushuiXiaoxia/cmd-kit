buildscript {
    repositories {
        if (System.getProperty("os.name").lowercase().contains("mac")) {
            maven(url = "https://maven.aliyun.com/repository/public")
        }
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "2.1.21"
    `java-library`
    `maven-publish`
}

group = "com.github.LiushuiXiaoxia"
version = "${project.properties["LIB_VERSION"]}"

repositories {
    if (System.getProperty("os.name").lowercase().contains("mac")) {
        maven(url = "https://maven.aliyun.com/repository/public")
    }
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(11)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useTestNG()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("cmd-kit") // 项目名称
                description.set("kotlin/java call process kit")
                url.set("https://github.com/LiushuiXiaoxia/cmd-kit")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("LiushuiXiaoxia")
                        name.set("LiushuiXiaoxia")
                        url.set("https://github.com/LiushuiXiaoxia")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/LiushuiXiaoxia/cmd-kit.git")
                    developerConnection.set("scm:git:ssh://github.com/LiushuiXiaoxia/cmd-kit.git")
                    url.set("https://github.com/LiushuiXiaoxia/cmd-kit")
                }
            }
        }
    }
}
