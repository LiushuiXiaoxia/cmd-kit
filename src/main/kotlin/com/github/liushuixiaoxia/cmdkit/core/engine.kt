package com.github.liushuixiaoxia.cmdkit.core

import com.github.liushuixiaoxia.cmdkit.*
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.Collections
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


internal class RealCmdReq(
    override val cmdList: List<String>,
    override val workspace: File? = null,
    override var env: Map<String, String>? = null,
    override var timeout: Long = Global.timeout,
    override var logEnable: Boolean = true,
    override var cmdLogback: CmdLogback? = null,
    override var cmdCallback: CmdCallback? = null,
    override var redirectError: Boolean = false,
) : CmdReq {

    val currentLog: CmdLogback by lazy {
        if (logEnable) {
            cmdLogback ?: Global.logback ?: EmptyLogback()
        } else {
            EmptyLogback()
        }
    }
}

internal class RealCmdResult : CmdResult {

    override var exitValue = -1

    var e: Throwable? = null

    internal val lines: MutableList<ResultLine> = Collections.synchronizedList(mutableListOf())

    override val all: List<String> by lazy {
        synchronized(lines) {
            lines.map { it.msg }.toList()
        }
    }
    override val text: String by lazy {
        synchronized(lines) {
            lines.filter { !it.error }.joinToString("\n") { it.msg }
        }
    }
    override val error: String by lazy {
        synchronized(lines) {
            lines.filter { it.error }.joinToString("\n") { it.msg }
        }
    }

    var internalDuration: Duration = Duration.ZERO
    override val duration: Duration
        get() = internalDuration

    fun allResultLines(): List<ResultLine> = ArrayList(lines)

    fun addLine(text: String, error: Boolean = false): ResultLine {
        val line = ResultLine(text, error)
        lines.add(line)
        return line
    }

    override fun check(errorMessage: String): CmdResult {
        if (isSuccess()) {
            return this
        }
        throw CmdExecException(errorMessage, e)
    }

    private val begin = LocalDateTime.now()

    fun setComplete() {
        val end = LocalDateTime.now()
        internalDuration = Duration.between(begin, end)
    }

    fun display(): String {
        if (all.size > 200) {
            val list = mutableListOf<String>().apply {
                addAll(all.take(20))
                add("........")
                addAll(all.takeLast(20))
            }
            return myString(list)
        }
        return toString()
    }

    override fun toString(): String {
        return myString(all)
    }

    private fun myString(list: List<String>): String {
        return """CmdResult(exitValue=$exitValue, lines=${lines.size}, duration = $duration)
        |'${list.joinToString("\n")}'
        """.trimMargin()
    }
}

internal class CmdEngine(val req: RealCmdReq) {

    internal fun create(): Process {
        return ProcessBuilder(req.cmdList.asBashCmd())
            .apply {
                directory(req.workspace)
                environment().putAll(req.env ?: emptyMap())
                redirectErrorStream(req.redirectError)
            }.start()
    }

    internal fun exec(): CmdResult {
        req.currentLog.log("run cmd: cmdList =  ${req.cmdList}, workspace = ${req.workspace}")
        val p = create()
        return result(p)
    }

    internal fun result(p: Process): CmdResult {
        val name = req.cmdList.firstOrNull() ?: "cmd-kit"
        val result = RealCmdResult()

        thread(name = "$name-${System.currentTimeMillis()}") {
            p.inputStream.bufferedReader().lines().forEach {
                val line = result.addLine(it, false)
                req.cmdCallback?.onReceive(line)
                req.currentLog.output(it, false)
            }
        }
        thread(name = "$name-${System.currentTimeMillis()}") {
            Thread.sleep(10)
            p.errorStream.bufferedReader().lines().forEach {
                val line = result.addLine(it, true)
                req.cmdCallback?.onReceive(line)
                req.currentLog.output(it, true)
            }
        }

        runCatching {
            if (req.timeout > 0) {
                p.waitFor(req.timeout, TimeUnit.SECONDS)
            } else {
                p.waitFor()
            }

            if (p.isAlive) {
                // result.addLine("命令未执行结束已中断", true)
                p.destroy()
                result.exitValue = p.exitValue()
                val ret = p.waitFor(2, TimeUnit.SECONDS)
                if (!ret) {
                    p.destroyForcibly()
                }
            } else {
                result.exitValue = p.exitValue()
            }
        }.onSuccess {
            req.currentLog.log("run cmd: cmdList =  ${req.cmdList}, exitValue = ${result.exitValue}")
        }.onFailure {
            result.exitValue = -1
            result.e = it

            req.currentLog.log("run cmd: cmdList =  ${req.cmdList}, exitValue = ${result.exitValue}")
        }
        result.setComplete()
        req.cmdCallback?.onComplete(result)

        return result
    }
}

//internal fun String.asBashCmd(): List<String> {
//    return listOf(this).asBashCmd()
//}

internal fun List<String>.asBashCmd(): List<String> {
    val os = System.getProperty("os.name").lowercase()
    val list = if (os.contains("win")) {
        // Windows 系统
        mutableListOf("cmd.exe", "/c")
    } else {
        // macOS 或 Linux 系统
        mutableListOf("/bin/bash", "-c")
    }
    list.addAll(this)
    return list
}