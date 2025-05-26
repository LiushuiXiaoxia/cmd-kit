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
        callback: CmdCallback? = null,
    ): Int {
        return exec(cmd, ws, true, timeout, env, callback).exitValue
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
        callback: CmdCallback? = null,
    ): CmdResult {
        return exec(cmd, ws, false, timeout, env, callback).apply {
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
        callback: CmdCallback? = null,
    ): CmdResult {
        val req = newCmd(listOf(cmd), ws)
        (req as RealCmdReq).also {
            it.logEnable = output
            it.timeout = timeout
            it.env = env
            it.cmdCallback = callback
        }
        return exec(req)
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