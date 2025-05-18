package cn.mycommons.processkit

import java.io.File
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


object ProcessKit {

    private var timeOut = 10L // 10min

    private var globalProcessLog: ProcessLog = ProcessLog.defaultLog

    fun setup(processLog: ProcessLog, timeout: Long) {
        globalProcessLog = processLog
        timeOut = timeout
    }

    fun newProcess(cmd: String, ws: File? = null): ProcessReq {
        return ProcessReq(cmd, ws, timeout = timeOut, processLog = globalProcessLog)
    }

    fun exec(cmd: String, ws: File? = null, output: Boolean = false, logPrefix: String = " - "): ProcessResult {
        return ProcessReq(
            cmd, ws,
            log = output,
            logPrefix = logPrefix,
            timeout = timeOut,
            processLog = globalProcessLog
        ).run()
    }

    fun result(p: Process, output: Boolean = false, logPrefix: String = " - "): ProcessResult {
        return ProcessReq(
            "", ws = null,
            log = output,
            logPrefix = logPrefix,
            processLog = globalProcessLog
        ).result(p)
    }
}

class ProcessReq(
    val cmd: String,
    var ws: File? = null,
    var env: Map<String, String>? = null,
    var log: Boolean = false,
    var timeout: Long = 0,
    var logPrefix: String = " - ",
    var processLog: ProcessLog? = null,
) {

    fun exec(): Process {
        val pb = ProcessBuilder(cmd.asBashCmd())
            .apply {
                directory(ws)
                environment().putAll(env ?: emptyMap())
            }
        return pb.start()
    }

    fun run(): ProcessResult {
        processLog?.log("run process: cmd =  [$cmd], ws = $ws")
        val p = exec()
        return result(p)
    }

    fun result(p: Process): ProcessResult {
        val name = cmd.trim().split(" ").firstOrNull() ?: "process"
        val result = ProcessResult()
        val count = CountDownLatch(2)

        thread(name = "$name-${System.currentTimeMillis()}") {
            p.inputStream.bufferedReader().readLines().forEach {
                result.addLine(it, false)
                if (log) processLog?.output("$logPrefix$it", false)
            }
            count.countDown()
        }
        thread(name = "$name-${System.currentTimeMillis()}") {
            Thread.sleep(10)
            p.errorStream.bufferedReader().readLines().forEach {
                result.addLine(it, true)
                if (log) processLog?.output("$logPrefix$it", true)
            }
            count.countDown()
        }

        if (timeout > 0) {
            p.waitFor(timeout, TimeUnit.MINUTES)
        } else {
            p.waitFor()
        }
        //如果命令未执行完成,则将ex设置错误属性
        if (p.isAlive) {
            result.addLine("命令未执行结束已中断", true)
            result.exitValue = 1
        } else {
            result.exitValue = p.exitValue()
        }
        kotlin.runCatching { count.await() }
        // processLog?.log("result = ${result.display()}")

        return result
    }
}

interface ProcessLog {

    companion object {
        val defaultLog = object : ProcessLog {
            override fun log(s: String) {
                // println(s)
            }

            override fun output(s: String, error: Boolean) {
                if (error) {
                    System.err.println(s)
                } else {
                    println(s)
                }
            }
        }
    }

    fun log(s: String)
    fun output(s: String, error: Boolean)
}

class ProcessResult {

    var exitValue = -1

    private val lines: MutableList<ResultLine> = mutableListOf()

    val all: List<String> by lazy { lines.map { it.line }.toList() }
    val text by lazy { lines.filter { !it.error }.joinToString("\n") { it.line } }
    val error by lazy { lines.filter { it.error }.joinToString("\n") { it.line } }

    fun allResultLines(): List<ResultLine> = ArrayList(lines)

    fun addLine(text: String, error: Boolean = false) {
        synchronized(lines) {
            lines.add(ResultLine(text, error))
        }
    }

    fun check(msg: String): ProcessResult {
        if (isSuccess()) {
            return this
        }
        throw RuntimeException("${msg}:${error}|${text}")
    }

    fun isSuccess(): Boolean = exitValue == 0

    fun display(): String {
        if (all.size > 200) {
            val list = mutableListOf<String>().apply {
                addAll(all.take(20))
                add("~~~~~~~~~~~")
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
        |'${all.joinToString("\n")}'
        """.trimMargin()
    }
}

data class ResultLine(val line: String, val error: Boolean = false) {

    val time = LocalDateTime.now()
}

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

fun String.asBashCmd(): List<String> {
    return bashCmd(this)
}

fun bashCmd(vararg cmd: String): List<String> {
    val list = mutableListOf("/bin/bash", "-c")
    list.addAll(cmd)
    return list
}