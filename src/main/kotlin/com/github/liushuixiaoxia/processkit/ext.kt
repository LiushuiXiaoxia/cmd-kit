package com.github.liushuixiaoxia.processkit

import java.io.File


/**
 * 获取pid
 */
fun Process.reflectPid(): Long {
    return runCatching {
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

fun runCmd(
    cmd: String,
    ws: File? = null,
    block: ProcessReq.() -> Unit = {},
): Int {
    val req = ProcessKit.newProcess(listOf(cmd), ws)
    req.block()
    return ProcessKit.exec(req).exitValue
}

fun callCmd(
    cmd: String,
    ws: File? = null,
    block: ProcessReq.() -> Unit = {},
): ProcessResult {
    val req = ProcessKit.newProcess(listOf(cmd), ws)
    req.block()
    return ProcessKit.exec(req)
}