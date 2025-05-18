package cn.mycommons.processkit


/**
 * 获取pid
 */
fun Process.reflectPid(): Long {
    return kotlin.runCatching {
        pid()
    }.getOrElse {
        val pid = javaClass.getDeclaredField("pid")
        pid.isAccessible = true
        pid.getLong(this)
    }
}

/**
 * 获取已有进程执行的结果
 */
fun Process.asProcessResult(output: Boolean = false): ProcessResult {
    return ProcessKit.result(this, output)
}