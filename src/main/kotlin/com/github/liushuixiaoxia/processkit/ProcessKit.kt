package com.github.liushuixiaoxia.processkit

import com.github.liushuixiaoxia.processkit.core.Global
import com.github.liushuixiaoxia.processkit.core.ProcessEngine
import com.github.liushuixiaoxia.processkit.core.RealProcessReq
import java.io.File

object ProcessKit {

    /**
     * 配置全局属性
     */
    @JvmStatic
    fun setup(
        logEnable: Boolean,
        logback: ProcessLogback? = null,
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
        callback: ProcessCallback? = null,
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
        callback: ProcessCallback? = null,
    ): ProcessResult {
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
        callback: ProcessCallback? = null,
    ): ProcessResult {
        val req = newProcess(listOf(cmd), ws)
        (req as RealProcessReq).also {
            it.logEnable = output
            it.timeout = timeout
            it.env = env
            it.processCallback = callback
        }
        return exec(req)
    }

    /**
     * 手动创建执行，可详细自定义参数
     */
    @JvmStatic
    fun newProcess(cmdList: List<String>, workspace: File? = null): ProcessReq {
        return RealProcessReq(cmdList = cmdList, workspace = workspace)
    }

    /**
     * 执行
     */
    @JvmStatic
    fun exec(req: ProcessReq): ProcessResult {
        return ProcessEngine(req as RealProcessReq).exec()
    }

    /**
     * 获取已有进程执行的结果
     */
    @JvmStatic
    fun result(p: Process, output: Boolean = false): ProcessResult {
        val req = newProcess(listOf(), null)
        req.logEnable = output
        return ProcessEngine(req as RealProcessReq).result(p)
    }
}