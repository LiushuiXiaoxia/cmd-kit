package cn.mycommons.processkit

import cn.mycommons.processkit.core.RealProcessResult
import java.io.File

interface ProcessLog {
    fun log(s: String)
    fun output(s: String, error: Boolean)
}

class ProcessExecException(msg: String, e: Throwable? = null) : RuntimeException(msg, e)

interface ProcessReq {
    val cmdList: List<String>
    val workspace: File?
    var env: Map<String, String>?
    var timeout: Long
    var logEnable: Boolean
    var processLog: ProcessLog?
}

fun ProcessReq.exec(): ProcessResult {
    return ProcessKit.exec(this)
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