package com.github.liushuixiaoxia.cmdkit.core

import com.github.liushuixiaoxia.cmdkit.CmdLogback

internal object Global {

    const val DEFAULT_TIMEOUT = -1L

    var logEnable: Boolean = true

    var timeout = -1L

    var logback: CmdLogback? = DefaultCmdLogback(logEnable)

    fun setup(logEnable: Boolean, logback: CmdLogback?, timeout: Long = DEFAULT_TIMEOUT) {
        Global.logEnable = logEnable
        Global.logback = logback
        Global.timeout = timeout
    }
}


internal class EmptyLogback() : CmdLogback {
    override fun log(s: String) {

    }

    override fun output(s: String, error: Boolean) {
    }
}

internal class DefaultCmdLogback(private val logEnable: Boolean) : CmdLogback {

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