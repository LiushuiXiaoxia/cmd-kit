package com.github.liushuixiaoxia.processkit

import org.testng.Assert
import org.testng.annotations.Test
import java.io.File

class ProcessKitTestTimeout {

    val cmd = """
            echo "Hello World"
            echo 1
            sleep 2
            echo 2
            echo "OK1"
            echo "ERR1" >&2
            echo "OK2"
            echo "ERR2" >&2
            sleep 2
            echo 3
            echo "Hello World ~~~"
        """.trimIndent()

    val f: File by lazy {
        File.createTempFile("ProcessKitTestTimeoutTest", ".sh").apply { writeText(cmd) }
    }


    @Test
    fun testLongExecute() {
        val ret = ProcessKit.run("bash ${f.absolutePath}")
        println("ret = $ret")

        val result = ProcessKit.call("bash ${f.absolutePath}")
        println("result = $result")
    }

    @Test
    fun testTimeout() {
        val ret = ProcessKit.run("bash ${f.absolutePath}", timeout = 3)
        println("ret = $ret")
        Assert.assertNotEquals(ret, 0)
    }

    @Test(expectedExceptions = [ProcessExecException::class])
    fun testTimeout2() {
        runCatching {
            ProcessKit.call("bash ${f.absolutePath}", timeout = 3).check("timeout")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test
    fun testCallback() {
        ProcessKit.setup(false, null)
        ProcessKit.run("bash ${f.absolutePath}", callback = object : ProcessCallback {
            override fun onReceive(line: ResultLine) {
                println("> ${line.time} ${line.msg}")
            }

            override fun onComplete(result: ProcessResult) {
                println("result = $result")
            }
        })
    }
}