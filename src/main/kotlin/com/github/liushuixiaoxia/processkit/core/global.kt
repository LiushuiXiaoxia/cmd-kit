package com.github.liushuixiaoxia.processkit.core

import com.github.liushuixiaoxia.processkit.ProcessLogback

internal object Global {

    const val DEFAULT_TIMEOUT = -1L

    var logEnable: Boolean = true

    var timeout = -1L

    var processLogback: ProcessLogback = DefaultLogback(logEnable)

    fun setup(logEnable: Boolean, processLogback: ProcessLogback, timeout: Long = DEFAULT_TIMEOUT) {
        Global.logEnable = logEnable
        Global.processLogback = processLogback
        Global.timeout = timeout
    }
}


internal class EmptyLogback() : ProcessLogback {
    override fun log(s: String) {

    }

    override fun output(s: String, error: Boolean) {
    }
}

internal class DefaultLogback(private val logEnable: Boolean) : ProcessLogback {
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