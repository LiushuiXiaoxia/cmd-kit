package cn.mycommons.processkit.core

import cn.mycommons.processkit.ProcessLog

internal object Global {

    var logEnable: Boolean = true

    var timeout = -1L

    var processLog: ProcessLog = DefaultLog(logEnable)

    fun setup(logEnable: Boolean, processLog: ProcessLog, timeout: Long = -1) {
        Global.logEnable = logEnable
        Global.processLog = processLog
        Global.timeout = timeout
    }
}


internal class EmptyLog() : ProcessLog {
    override fun log(s: String) {

    }

    override fun output(s: String, error: Boolean) {
    }
}

internal class DefaultLog(private val logEnable: Boolean) : ProcessLog {
    override fun log(s: String) {
        // println(s)
    }

    override fun output(s: String, error: Boolean) {
        if (!logEnable) {
            return
        }
        if (error) {
            System.err.println(s)
        } else {
            println(s)
        }
    }
}