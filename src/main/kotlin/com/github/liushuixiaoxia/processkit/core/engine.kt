package com.github.liushuixiaoxia.processkit.core

import com.github.liushuixiaoxia.processkit.ProcessCallback
import com.github.liushuixiaoxia.processkit.ProcessExecException
import com.github.liushuixiaoxia.processkit.ProcessLogback
import com.github.liushuixiaoxia.processkit.ProcessReq
import com.github.liushuixiaoxia.processkit.ProcessResult
import com.github.liushuixiaoxia.processkit.ResultLine
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class RealProcessReq(
    override val cmdList: List<String>,
    override val workspace: File? = null,
    override var env: Map<String, String>? = null,
    override var timeout: Long = Global.timeout,
    override var logEnable: Boolean = true,
    override var processLogback: ProcessLogback? = null,
    override var processCallback: ProcessCallback? = null,
) : ProcessReq {

    val currentLog: ProcessLogback by lazy {
        if (logEnable) {
            processLogback ?: Global.logback ?: EmptyLogback()
        } else {
            EmptyLogback()
        }
    }
}

internal class RealProcessResult : ProcessResult {

    override var exitValue = -1

    var e: Throwable? = null

    private val lines: MutableList<ResultLine> = mutableListOf()

    override val all: List<String> by lazy { lines.map { it.msg }.toList() }
    override val text: String by lazy { lines.filter { !it.error }.joinToString("\n") { it.msg } }
    override val error: String by lazy { lines.filter { it.error }.joinToString("\n") { it.msg } }

    fun allResultLines(): List<ResultLine> = ArrayList(lines)

    fun addLine(text: String, error: Boolean = false): ResultLine {
        val line = ResultLine(text, error)
        synchronized(lines) {
            lines.add(line)
        }
        return line
    }

    override fun check(errorMessage: String): ProcessResult {
        if (isSuccess()) {
            return this
        }
        throw ProcessExecException(errorMessage, e)
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
        return """ProcessResult(exitValue=$exitValue, lines=${lines.size})
        |'${list.joinToString("\n")}'
        """.trimMargin()
    }
}

class ProcessEngine(val req: RealProcessReq) {

    private fun create(): Process {
        return ProcessBuilder(req.cmdList.asBashCmd())
            .apply {
                directory(req.workspace)
                environment().putAll(req.env ?: emptyMap())
            }.start()
    }

    fun exec(): ProcessResult {
        req.currentLog.log("run process: cmdList =  ${req.cmdList}, workspace = ${req.workspace}")
        val p = create()
        return result(p)
    }

    fun result(p: Process): ProcessResult {
        val name = req.cmdList.firstOrNull() ?: "process"
        val result = RealProcessResult()

        thread(name = "$name-${System.currentTimeMillis()}") {
            p.inputStream.bufferedReader().lines().forEach {
                val line = result.addLine(it, false)
                req.processCallback?.onReceive(line)
                req.currentLog.output(it, false)
            }
        }
        thread(name = "$name-${System.currentTimeMillis()}") {
            Thread.sleep(10)
            p.errorStream.bufferedReader().lines().forEach {
                val line = result.addLine(it, true)
                req.processCallback?.onReceive(line)
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
            req.currentLog.log("run process: cmdList =  ${req.cmdList}, exitValue = ${result.exitValue}")
        }.onFailure {
            result.exitValue = -1
            result.e = it

            req.currentLog.log("run process: cmdList =  ${req.cmdList}, exitValue = ${result.exitValue}")
        }
        req.processCallback?.onComplete(result)

        return result
    }
}

internal fun String.asBashCmd(): List<String> {
    return listOf(this).asBashCmd()
}

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