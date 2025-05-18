package cn.mycommons.processkit


fun Process.reflectPid(): Long {
    return kotlin.runCatching {
        pid()
    }.getOrElse {
        val pid = javaClass.getDeclaredField("pid")
        pid.isAccessible = true
        pid.getLong(this)
    }
}

fun Process.asProcessResult(output: Boolean = false): ProcessResult {
    return ProcessKit.result(this, output)
}