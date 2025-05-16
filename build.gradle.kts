plugins {
    kotlin("jvm") version "2.1.20"
}

group = "cn.mycommons"
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