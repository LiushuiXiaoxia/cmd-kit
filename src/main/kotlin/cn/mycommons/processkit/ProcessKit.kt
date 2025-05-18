package cn.mycommons.processkit

import cn.mycommons.processkit.core.Global
import cn.mycommons.processkit.core.ProcessEngine
import cn.mycommons.processkit.core.ProcessReq
import cn.mycommons.processkit.core.RealProcessResult
import java.io.File
import java.time.LocalDateTime
import java.util.*


interface ProcessLog {
    fun log(s: String)
    fun output(s: String, error: Boolean)
}

class ProcessExecException(msg: String, e: Throwable? = null) : RuntimeException(msg, e)

interface IProcessReq {
    val cmdList: List<String>
    val workspace: File?
    val env: Map<String, String>?
    val timeout: Long
    val logEnable: Boolean
    val processLog: ProcessLog?
}

interface ProcessResult {

    val exitValue: Int
    val all: List<String>
    val text: String
    val error: String

    fun isSuccess(): Boolean {
        return exitValue == 0
    }

    @Throws(ProcessExecException::class)
    fun check(errorMessage: String): ProcessResult
}

fun ProcessResult.display(): String {
    val r = this as RealProcessResult
    return r.display()
}

object ProcessKit {

    fun setup(logEnable: Boolean, processLog: ProcessLog, timeout: Long) {
        Global.setup(logEnable, processLog, timeout)
    }

    fun newProcess(cmd: String, ws: File? = null): IProcessReq {
        return ProcessReq(listOf(cmd), ws)
    }

    fun exec(cmd: String, ws: File? = null, output: Boolean = false): ProcessResult {
        val req = ProcessReq(
            listOf(cmd),
            ws,
            logEnable = output,
        )
        return ProcessEngine(req).exec()
    }

    fun result(p: Process, output: Boolean = false): ProcessResult {
        val req = ProcessReq(
            listOf(),
            null,
            logEnable = output,
        )
        return ProcessEngine(req).result(p)
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