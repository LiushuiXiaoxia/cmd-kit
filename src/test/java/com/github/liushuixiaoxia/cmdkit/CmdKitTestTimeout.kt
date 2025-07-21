package com.github.liushuixiaoxia.cmdkit

import org.junit.Assert
import org.junit.Test
import java.io.File

class CmdKitTestTimeout {

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
        File.createTempFile("CmdKitTestTimeoutTest", ".sh").apply { writeText(cmd) }
    }


    @Test
    fun testLongExecute() {
        val ret = CmdKit.run("bash ${f.absolutePath}")
        println("ret = $ret")

        val result = CmdKit.call("bash ${f.absolutePath}")
        println("result = $result")
    }


    @Test
    fun testLongExecute2() {
        val r = CmdKit.process("bash ${f.absolutePath}", redirectError = true)
        r.inputStream.bufferedReader().lines().forEach { println(it) }
        println("r = ${r.waitFor()}")
    }

    @Test
    fun testTimeout() {
        val ret = CmdKit.run("bash ${f.absolutePath}", timeout = 3)
        println("ret = $ret")
        Assert.assertNotEquals(ret, 0)
    }

    @Test(expected = CmdExecException::class)
    fun testTimeout2() {
        runCatching {
            CmdKit.call("bash ${f.absolutePath}", timeout = 3).check("timeout")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    @Test(expected = CmdExecException::class)
    fun testTimeout3() {
        callCmd("bash ${f.absolutePath}") {
            timeout = 3
            checkResult("timeout")
        }
    }

    @Test
    fun testCallback() {
        CmdKit.setup(false, null)
        CmdKit.run("bash ${f.absolutePath}", callback = object : CmdCallback {
            override fun onReceive(line: ResultLine) {
                println("> ${line.time} ${line.msg}")
            }

            override fun onComplete(result: CmdResult) {
                println("result = $result")
            }
        })
    }
}