package com.github.liushuixiaoxia.cmdkit

import com.github.liushuixiaoxia.cmdkit.core.Global
import com.github.liushuixiaoxia.cmdkit.core.CmdEngine
import com.github.liushuixiaoxia.cmdkit.core.RealCmdReq
import java.io.File

object CmdKit {

    /**
     * 配置全局属性
     */
    @JvmStatic
    fun setup(
        logEnable: Boolean,
        logback: CmdLogback? = null,
        timeout: Long = Global.DEFAULT_TIMEOUT,
    ) {
        Global.setup(logEnable, logback, timeout)
    }

    /**
     * 仅执行，默认输出，忽略错误，返回结果码
     */
    @JvmStatic
    @JvmOverloads
    fun run(
        cmd: String,
        ws: File? = null,
        timeout: Long = Global.DEFAULT_TIMEOUT,
        env: Map<String, String>? = null,
        redirectError: Boolean = false,
        callback: CmdCallback? = null,
    ): Int {
        return exec(cmd, ws, true, timeout, env, redirectError, callback).exitValue
    }

    /**
     * 执行，不输出，返回结果, 默认不会检测结果
     */
    @JvmStatic
    @JvmOverloads
    fun call(
        cmd: String,
        ws: File? = null,
        timeout: Long = Global.DEFAULT_TIMEOUT,
        env: Map<String, String>? = null,
        check: Boolean = false,
        redirectError: Boolean = false,
        callback: CmdCallback? = null,
    ): CmdResult {
        return exec(cmd, ws, false, timeout, env, redirectError, callback).apply {
            if (check) {
                check("call $cmd failed")
            }
        }
    }

    /**
     * 最原始的方法，低级方法，需要指定详细参数
     */
    @JvmStatic
    @JvmOverloads
    fun exec(
        cmd: String,
        ws: File?,
        output: Boolean,
        timeout: Long = Global.DEFAULT_TIMEOUT,
        env: Map<String, String>? = null,
        redirectError: Boolean = false,
        callback: CmdCallback? = null,
    ): CmdResult {
        val req = newCmd(listOf(cmd), ws)
        (req as RealCmdReq).also {
            it.logEnable = output
            it.timeout = timeout
            it.env = env
            it.redirectError = redirectError
            it.cmdCallback = callback
        }
        return exec(req)
    }

    /**
     * 创建一个Process，不处理
     */
    @JvmStatic
    @JvmOverloads
    fun process(
        cmd: String,
        ws: File? = null,
        timeout: Long = Global.DEFAULT_TIMEOUT,
        env: Map<String, String>? = null,
        redirectError: Boolean = false,
        // callback: CmdCallback? = null,
    ): Process {
        val req = newCmd(listOf(cmd), ws)
        (req as RealCmdReq).also {
            it.logEnable = false
            it.timeout = timeout
            it.env = env
            it.redirectError = redirectError
            // it.cmdCallback = callback
        }
        return CmdEngine(req as RealCmdReq).create()
    }

    /**
     * 手动创建执行，可详细自定义参数
     */
    @JvmStatic
    fun newCmd(cmdList: List<String>, workspace: File? = null): CmdReq {
        return RealCmdReq(cmdList = cmdList, workspace = workspace)
    }

    /**
     * 执行
     */
    @JvmStatic
    fun exec(req: CmdReq): CmdResult {
        return CmdEngine(req as RealCmdReq).exec()
    }

    /**
     * 获取已有进程执行的结果
     */
    @JvmStatic
    fun result(p: Process, output: Boolean = false): CmdResult {
        val req = newCmd(listOf(), null)
        req.logEnable = output
        return CmdEngine(req as RealCmdReq).result(p)
    }
}