buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "2.1.20"
    `java-library`
    `maven-publish`
}

group = "dev.LiushuiXiaoxia"
version = project.properties["LIB_VERSION"].toString()

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useTestNG()
}

kotlin {
    jvmToolchain(11)
}

java {
    withSourcesJar()
    withJavadocJar()
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("process-kit") // 项目名称
                description.set("kotlin/java call process kit")
                url.set("https://github.com/yourusername/yourproject")

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
                    connection.set("scm:git:git://github.com/LiushuiXiaoxia/process-kit.git")
                    developerConnection.set("scm:git:ssh://github.com/LiushuiXiaoxia/process-kit.git")
                    url.set("https://github.com/LiushuiXiaoxia/process-kit")
                }
            }
        }
    }
}
