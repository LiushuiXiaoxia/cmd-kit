package com.github.liushuixiaoxia.cmdkit

import org.junit.Test
import java.io.File

class CmdDslTest {

    @Test(expected = CmdExecException::class)
    fun testDsl() {
        runCmd("exit 1") {
            checkResult("cmd run fail")
        }
    }

    @Test
    fun testExample2() {
        val tmp = File("build/tmp", "t-${System.currentTimeMillis()}").canonicalFile
        println("tmp = $tmp")
        callCmd("mkdir -p $tmp").check("make dir fail")
        callCmd("git clone git@github.com:LiushuiXiaoxia/cmd-kit.git $tmp").check("git check failed")
        runCmd("cd $tmp && git status && git log -5")
        runCmd("cd $tmp && ./gradlew clean assemble")
        runCmd("rm -rf $tmp")
        runCmd("ls -lh")

        println("$tmp exists = ${tmp.exists()}")
    }

    @Test
    fun testDsl3() {
        process("ls -alh").asCmdResult(true)
    }
}