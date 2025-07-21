package com.github.liushuixiaoxia.cmdkit

import org.junit.Assert
import org.junit.Test
import java.io.File

class CmdKitTestCall {

    @Test
    fun testSuccess() {
        println("before testSuccess")
        val ret = CmdKit.call("mkdir -p build/tmp-${System.currentTimeMillis()}")
        println("after testSuccess, ret = $ret")
    }

    @Test
    fun testFail() {
        println("before testFail")
        val ret = CmdKit.call("ls -alh dir-${System.currentTimeMillis()}")
        println("after testFail, ret = $ret")
    }

    @Test(expected = CmdExecException::class)
    fun testCheck() {
        println("before testFail")
        val ret = CmdKit.call("ls -alh dir-${System.currentTimeMillis()}", check = true)
        println("after testFail, ret = $ret")
    }

    @Test
    fun testGit() {
        CmdKit.call("git --version")
        CmdKit.call("git status")
        CmdKit.call("git log --oneline -5")
    }

    @Test
    fun testWs() {
        val ws = File(System.getProperty("user.home"))
        CmdKit.call("ls -alh", ws)
        CmdKit.call("pwd", ws)
    }

    @Test
    fun testEnv() {
        val env = mutableMapOf<String, String>(
            "hello" to "world",
        )
        CmdKit.call("echo \$hello", env = env)
        CmdKit.call("echo \$hello123", env = env)
    }

    @Test
    fun testShell() {
        val f = File("build", "${System.currentTimeMillis()}.log")
        val cmd = "grep '@Test' -R . > ${f.absolutePath}"
        CmdKit.call(cmd)

        val cmd2 = "find . -name \"*.log\"  | xargs rm -rf  "
        val ret = CmdKit.call(cmd2)
        Assert.assertEquals(ret.exitValue, 0)
        Assert.assertEquals(f.exists(), false)
    }

    @Test
    fun testShell2() {
        val cmd = "cd ~; ls -lh"
        CmdKit.call(cmd).check("shell")
    }

    @Test
    fun testShell3() {
        val cmd = "cd ~; ls -lh"
        repeat(10) {
            val ret = CmdKit.call(cmd).check("shell")
            println("ret = $ret")
        }
    }
}