package com.github.liushuixiaoxia.cmdkit

import org.junit.Assert
import org.junit.Test
import java.io.File

class CmdKitTestRun {

    @Test
    fun testSuccess() {
        println("before testSuccess")
        val ret = CmdKit.run("mkdir -p build/tmp-${System.currentTimeMillis()}")
        println("after testSuccess, ret = $ret")
    }

    @Test
    fun testFail() {
        println("before testFail")
        val ret = CmdKit.run("ls -alh dir-${System.currentTimeMillis()}")
        println("after testFail, ret = $ret")
    }

    @Test
    fun testGit() {
        CmdKit.run("git --version")
        CmdKit.run("git status")
        CmdKit.run("git log --oneline -5")
    }

    @Test
    fun testWs() {
        val ws = File(System.getProperty("user.home"))
        CmdKit.run("ls -alh", ws)
        CmdKit.run("pwd", ws)
    }

    @Test
    fun testEnv() {
        val env = mutableMapOf<String, String>(
            "hello" to "world",
        )
        CmdKit.run("echo \$hello", env = env)
        CmdKit.run("echo \$hello123", env = env)
    }

    @Test
    fun testShell() {
        val f = File("build", "${System.currentTimeMillis()}.log")
        val cmd = "grep '@Test' -R . > ${f.absolutePath}"
        CmdKit.run(cmd)

        val cmd2 = "find . -name \"*.log\"  | xargs rm -rf  "
        val ret = CmdKit.run(cmd2)
        Assert.assertEquals(ret, 0)
        Assert.assertEquals(f.exists(), false)
    }

    @Test
    fun testShell2() {
        val cmd = "cd ~; ls -lh"
        CmdKit.run(cmd)
    }
}