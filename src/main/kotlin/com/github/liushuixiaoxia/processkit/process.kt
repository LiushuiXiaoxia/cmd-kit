package com.github.liushuixiaoxia.processkit

import com.github.liushuixiaoxia.processkit.core.RealProcessResult
import java.io.File
import java.time.LocalDateTime

interface ProcessLogback {
    /**
     * 带调试信息的日志, 非执行日志
     */
    fun log(s: String)

    /**
     * 执行日志
     */
    fun output(s: String, error: Boolean)
}

interface ProcessCallback {

    fun onReceive(line: ResultLine)

    fun onComplete(result: ProcessResult)
}

/**
 * 执行异常
 */
class ProcessExecException(msg: String, e: Throwable? = null) : RuntimeException(msg, e)

interface ProcessReq {
    /**
     * 执行命令
     */
    val cmdList: List<String>

    /**
     * 执行目录
     */
    val workspace: File?

    /**
     * 自定义环境变量
     */
    var env: Map<String, String>?

    /**
     * 执行超时，默认单位为s
     */
    var timeout: Long

    /**
     * 是否输入
     */
    var logEnable: Boolean

    /**
     * 日志输出回调
     */
    var processLogback: ProcessLogback?

    /**
     * 执行回调
     */
    var processCallback: ProcessCallback?
}

/**
 * 获取结果
 */
fun ProcessReq.exec(): ProcessResult {
    return ProcessKit.exec(this)
}

interface ProcessResult {

    /**
     * 结果返回码
     */
    val exitValue: Int

    /**
     * 所有的日志输出，包括 std 和  err
     */
    val all: List<String>

    /**
     * std输出
     */
    val text: String

    /**
     * err 输出
     */
    val error: String

    /**
     * 是否成功
     */
    fun isSuccess(): Boolean {
        return exitValue == 0
    }

    /**
     * 检测是否成功，否则会抛出异常
     */
    @Throws(ProcessExecException::class)
    fun check(errorMessage: String): ProcessResult
}

/**
 * 输出行信息
 */
data class ResultLine(
    val msg: String,
    val error: Boolean = false,
    val time: LocalDateTime = LocalDateTime.now(),
)

/**
 * 更友好的输出结果，一般用于日志调试
 */
fun ProcessResult.display(): String {
    val r = this as RealProcessResult
    return r.display()
}