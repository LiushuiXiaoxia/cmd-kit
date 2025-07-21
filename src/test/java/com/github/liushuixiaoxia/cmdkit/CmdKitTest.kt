package com.github.liushuixiaoxia.cmdkit

import org.junit.Test


class CmdKitTest {

    @Test
    fun setup() {
        CmdKit.setup(
            false,
            object : CmdLogback {
                override fun log(s: String) {
                    println(s)
                }

                override fun output(s: String, error: Boolean) {
                    println("> $s")
                }
            },
        )

        val ret = CmdKit.run("git status")
        println("ret = $ret")
    }


    @Test
    fun test() {
        val r = CmdKit.call("ls -alh")
        println("r = $r")
    }

    @Test
    fun test3() {
        val r = CmdKit.call("ls -alh")
        println(r.display())
    }

    @Test
    fun test4() {
        val r = CmdKit.call("ls -alh")
        r.all.forEach { println(it) }
    }

    @Test
    fun test5() {
        val r = CmdKit.process("ls -alh")
        r.inputStream.bufferedReader().forEachLine { println(it) }
        println("r = ${r.waitFor()}")
    }

    @Test
    fun testCheck() {
        val r = CmdKit.call("ls -alh", ws = null)
        r.check("ls fail")
    }
}