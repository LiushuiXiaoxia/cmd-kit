# CmdKit

---

工作中经常需要调用 `shell` 命令，参考 `python` 的 `subprocess`, 使用 `kotlin` 简单包了一层，支持 `java/kotlin`, `mac/linux` 使用。

[![Java CI with Gradle](https://github.com/LiushuiXiaoxia/cmd-kit/actions/workflows/gradle.yml/badge.svg)](https://github.com/LiushuiXiaoxia/cmd-kit/actions/workflows/gradle.yml)

# 使用方法

[![](https://jitpack.io/v/LiushuiXiaoxia/cmd-kit.svg)](https://jitpack.io/#LiushuiXiaoxia/cmd-kit)

```gradle
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
        implementation("com.github.LiushuiXiaoxia:cmd-kit:$version")
}
```

```angular2html
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.LiushuiXiaoxia</groupId>
    <artifactId>cmd-kit</artifactId>
    <version>${version}</version>
</dependency>
```

使用举例

主要有 `run` 和 `call` 两个方法。

```kotlin
// 全局配置
fun setup(
    logEnable: Boolean,
    logback: CmdLogback,
    timeout: Long = Global.DEFAULT_TIMEOUT,
) {
}

// 仅执行，默认输出，忽略错误，返回结果码
fun run(
    cmd: String,
    ws: File? = null,
): Int {
}

// 执行，不输出，返回结果, 默认不会检测结果
fun call(
    cmd: String,
    ws: File? = null,
    timeout: Long,
    env: Map<String, String>?,
    check: Boolean = false,
): CmdResult {
}
```

```kotlin
val tmp = File("tmp", "t-${System.currentTimeMillis()}").canonicalFile
println("tmp = $tmp")
CmdKit.call("mkdir -p $tmp").check("make dir fail")
CmdKit.call("git clone git@github.com:LiushuiXiaoxia/cmd-kit.git $tmp").check("git check failed")
CmdKit.run("cd $tmp && git status && git log -5")
CmdKit.run("cd $tmp && ./gradlew clean assemble")
CmdKit.run("rm -rf $tmp")
println("tmp = ${tmp.exists()}")

// dsl
val tmp = File("build/tmp", "t-${System.currentTimeMillis()}").canonicalFile
runCmd("cd $tmp && ./gradlew clean assemble") {
    timeout = 120
    env = linkedMapOf(
        "JAVA_HOME" to "XXX"
    )
}
println("tmp = ${tmp.exists()}")

// dsl checkResult
runCmd("exit 1") {
    checkResult("cmd run fail")
}
```
