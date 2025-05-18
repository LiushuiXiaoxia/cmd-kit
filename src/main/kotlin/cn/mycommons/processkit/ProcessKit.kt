package cn.mycommons.processkit

import cn.mycommons.processkit.core.Global
import cn.mycommons.processkit.core.ProcessEngine
import cn.mycommons.processkit.core.RealProcessReq
import cn.mycommons.processkit.core.RealProcessResult
import java.io.File
import java.time.LocalDateTime
import java.util.*

object ProcessKit {

    /**
     * 配置全局属性
     */
    fun setup(logEnable: Boolean, processLog: ProcessLog, timeout: Long) {
        Global.setup(logEnable, processLog, timeout)
    }

    /**
     * 仅执行，默认输出，忽略错误，返回结果码
     */
    fun run(cmd: String, ws: File? = null, timeout: Long = Global.timeout): Int {
        return exec(cmd, ws, true).exitValue
    }

    /**
     * 执行，不输出，返回结果, 默认会检测直结果
     */
    fun call(cmd: String, ws: File? = null, timeout: Long = Global.timeout, check: Boolean = false): ProcessResult {
        return exec(cmd, ws, false).apply {
            if (check) {
                check("call $cmd failed")
            }
        }
    }

    /**
     * 最原始的方法，低级方法，需要指定详细参数
     */
    fun exec(
        cmd: String,
        ws: File? ,
        output: Boolean,
        timeout: Long = Global.timeout,
    ): ProcessResult {
        val req = newProcess(listOf(cmd), ws)
        (req as RealProcessReq).also {
            it.logEnable = output
            it.timeout = timeout
        }
        return exec(req)
    }

    /**
     * 执行
     */
    fun exec(req: ProcessReq): ProcessResult {
        return ProcessEngine(req as RealProcessReq).exec()
    }

    /**
     * 手动创建执行，可详细自定义参数
     */
    fun newProcess(cmdList: List<String>, workspace: File? = null): ProcessReq {
        return RealProcessReq(cmdList = cmdList, workspace = workspace)
    }

    /**
     * 获取已有进程执行的结果
     */
    fun result(p: Process, output: Boolean = false): ProcessResult {
        val req = newProcess(listOf(), null)
        req.logEnable = output
        return ProcessEngine(req as RealProcessReq).result(p)
    }
}