# ProcessKit

---

工作中经常需要调用 `shell` 命令，参考 `python` 的 `subprocess`, 使用 `kotlin` 简单包了一层，支持 `java/kotlin`, `mac/linux` 使用。

# 使用方法


```gradle
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
        implementation("com.github.LiushuiXiaoxia:process-kit:$version")
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
    <artifactId>process-kit</artifactId>
    <version>${version}</version>
</dependency>
```

使用举例

主要有 `run` 和 `call` 两个方法。

```kotlin
// 全局配置
fun setup(
    logEnable: Boolean,
    processLogback: ProcessLogback,
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
): ProcessResult {
}
```

```kotlin
val tmp = File("tmp", "t-${System.currentTimeMillis()}").canonicalFile
println("tmp = $tmp")
ProcessKit.call("mkdir -p $tmp").check("make dir fail")
ProcessKit.call("git clone git@github.com:LiushuiXiaoxia/process-kit.git $tmp").check("git check failed")
ProcessKit.run("cd $tmp && git status && git log -5")
ProcessKit.run("cd $tmp && ./gradlew clean assemble")
ProcessKit.run("rm -rf $tmp")

println("tmp = ${tmp.exists()}")
```