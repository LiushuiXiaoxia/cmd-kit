package com.github.liushuixiaoxia.cmdkit

import com.github.liushuixiaoxia.cmdkit.core.CmdEngine
import com.github.liushuixiaoxia.cmdkit.core.RealCmdReq
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
fun Process.asCmdResult(output: Boolean = false): CmdResult {
    return CmdKit.result(this, output)
}

typealias CmdBlock = CmdReq.() -> Unit

fun CmdReq.checkResult(errorMessage: String) {
    cmdCallback = object : CmdCallback {
        override fun onReceive(line: ResultLine) {
        }

        override fun onComplete(result: CmdResult) {
            result.check(errorMessage)
        }
    }
}

fun runCmd(
    cmd: String,
    ws: File? = null,
    block: CmdBlock? = null,
): Int {
    val req = CmdKit.newCmd(listOf(cmd), ws)
    block?.invoke(req)
    return CmdKit.exec(req).exitValue
}

fun callCmd(
    cmd: String,
    ws: File? = null,
    block: CmdBlock? = null,
): CmdResult {
    val req = CmdKit.newCmd(listOf(cmd), ws)
    block?.invoke(req)
    return CmdKit.exec(req)
}

fun process(
    cmd: String,
    ws: File? = null,
    block: CmdBlock? = null,
): Process {
    val req = CmdKit.newCmd(listOf(cmd), ws)
    block?.invoke(req)
    return CmdEngine(req as RealCmdReq).create()
}