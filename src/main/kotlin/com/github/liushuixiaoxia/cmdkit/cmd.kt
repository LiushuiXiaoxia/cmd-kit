package com.github.liushuixiaoxia.cmdkit

import com.github.liushuixiaoxia.cmdkit.core.RealCmdResult
import java.io.File
import java.time.LocalDateTime

interface CmdLogback {
    /**
     * 带调试信息的日志, 非执行日志
     */
    fun log(s: String)

    /**
     * 执行日志
     */
    fun output(s: String, error: Boolean)
}

interface CmdCallback {

    fun onReceive(line: ResultLine)

    fun onComplete(result: CmdResult)
}

/**
 * 执行异常
 */
class CmdExecException(msg: String, e: Throwable? = null) : RuntimeException(msg, e)

interface CmdReq {
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
    var cmdLogback: CmdLogback?

    /**
     * 执行回调
     */
    var cmdCallback: CmdCallback?

    /**
     * 合并标准错误和标准输出
     */
    var redirectError: Boolean
}

/**
 * 获取结果
 */
fun CmdReq.exec(): CmdResult {
    return CmdKit.exec(this)
}

interface CmdResult {

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
    @Throws(CmdExecException::class)
    fun check(errorMessage: String): CmdResult
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
fun CmdResult.display(): String {
    val r = this as RealCmdResult
    return r.display()
}