package cn.mycommons.processkit.core

import cn.mycommons.processkit.*
import java.io.File
import java.util.ArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class ProcessReq(
    override val cmdList: List<String>,
    override val workspace: File? = null,
    override val env: Map<String, String>? = null,
    override val timeout: Long = Global.timeout,
    override val logEnable: Boolean = true,
    override val processLog: ProcessLog? = DefaultLog(logEnable),
) : IProcessReq {

    //    fun exec(): Process {
//        val pb = ProcessBuilder(cmd.asBashCmd())
//            .apply {
//                directory(ws)
//                environment().putAll(env ?: emptyMap())
//            }
//        return pb.start()
//    }
//
//    fun run(): ProcessResult {
//        processLog?.log("run process: cmd =  [$cmd], ws = $ws")
//        val p = exec()
//        return result(p)
//    }
//
//    fun result(p: Process): ProcessResult {
//        val name = cmd.trim().split(" ").firstOrNull() ?: "process"
//        val result = ProcessResult()
//        val count = CountDownLatch(2)
//
//        thread(name = "$name-${System.currentTimeMillis()}") {
//            p.inputStream.bufferedReader().readLines().forEach {
//                result.addLine(it, false)
//                if (log) processLog?.output("$logPrefix$it", false)
//            }
//            count.countDown()
//        }
//        thread(name = "$name-${System.currentTimeMillis()}") {
//            Thread.sleep(10)
//            p.errorStream.bufferedReader().readLines().forEach {
//                result.addLine(it, true)
//                if (log) processLog?.output("$logPrefix$it", true)
//            }
//            count.countDown()
//        }
//
//        if (timeout > 0) {
//            p.waitFor(timeout, TimeUnit.MINUTES)
//        } else {
//            p.waitFor()
//        }
//        //如果命令未执行完成,则将ex设置错误属性
//        if (p.isAlive) {
//            result.addLine("命令未执行结束已中断", true)
//            result.exitValue = 1
//        } else {
//            result.exitValue = p.exitValue()
//        }
//        kotlin.runCatching { count.await() }
//        // processLog?.log("result = ${result.display()}")
//
//        return result
//    }
}


internal class RealProcessResult : ProcessResult {

    override var exitValue = -1

    var e: Throwable? = null

    private val lines: MutableList<ResultLine> = mutableListOf()

    override val all: List<String> by lazy { lines.map { it.line }.toList() }
    override val text: String by lazy { lines.filter { !it.error }.joinToString("\n") { it.line } }
    override val error: String by lazy { lines.filter { it.error }.joinToString("\n") { it.line } }

    fun allResultLines(): List<ResultLine> = ArrayList(lines)

    fun addLine(text: String, error: Boolean = false) {
        synchronized(lines) {
            lines.add(ResultLine(text, error))
        }
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

class ProcessEngine(val req: ProcessReq) {

    private fun create(): Process {
        return ProcessBuilder(req.cmdList.asBashCmd())
            .apply {
                directory(req.workspace)
                environment().putAll(req.env ?: emptyMap())
            }.start()
    }

    fun exec(): ProcessResult {
        req.processLog?.log("run process: cmdList =  [${req.cmdList}], workspace = ${req.workspace}")
        val p = create()
        return result(p)
    }

    fun result(p: Process): ProcessResult {
        val name = req.cmdList.firstOrNull() ?: "process"
        val result = RealProcessResult()
        val count = CountDownLatch(2)

        thread(name = "$name-${System.currentTimeMillis()}") {
            p.inputStream.bufferedReader().readLines().forEach {
                result.addLine(it, false)
                req.processLog?.output(it, false)
            }
            count.countDown()
        }
        thread(name = "$name-${System.currentTimeMillis()}") {
            Thread.sleep(10)
            p.errorStream.bufferedReader().readLines().forEach {
                result.addLine(it, true)
                req.processLog?.output(it, true)
            }
            count.countDown()
        }

        kotlin.runCatching {
            if (req.timeout > 0) {
                p.waitFor(req.timeout, TimeUnit.SECONDS)
            } else {
                p.waitFor()
            }

            //如果命令未执行完成,则将ex设置错误属性
            if (p.isAlive) {
                // result.addLine("命令未执行结束已中断", true)
                result.exitValue = -1
            } else {
                result.exitValue = p.exitValue()
            }
            if (req.timeout > 0) {
                count.await(req.timeout, TimeUnit.SECONDS)
            } else {
                count.await()
            }
        }.onFailure {
            result.exitValue = -1
            result.e = it
        }

        return result
    }
}

internal fun String.asBashCmd(): List<String> {
    return listOf(this).asBashCmd()
}

internal fun List<String>.asBashCmd(): List<String> {
    val list = mutableListOf("/bin/bash", "-c")
    list.addAll(this)
    return list
}