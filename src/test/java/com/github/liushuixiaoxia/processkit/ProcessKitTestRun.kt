package com.github.liushuixiaoxia.processkit

import org.testng.Assert
import org.testng.annotations.Test
import java.io.File

class ProcessKitTestRun {

    @Test
    fun testSuccess() {
        println("before testSuccess")
        val ret = ProcessKit.run("mkdir -p build/tmp-${System.currentTimeMillis()}")
        println("after testSuccess, ret = $ret")
    }

    @Test
    fun testFail() {
        println("before testFail")
        val ret = ProcessKit.run("ls -alh dir-${System.currentTimeMillis()}")
        println("after testFail, ret = $ret")
    }

    @Test
    fun testGit() {
        ProcessKit.run("git --version")
        ProcessKit.run("git status")
        ProcessKit.run("git log --oneline -5")
    }

    @Test
    fun testWs() {
        val ws = File(System.getProperty("user.home"))
        ProcessKit.run("ls -alh", ws)
        ProcessKit.run("pwd", ws)
    }

    @Test
    fun testEnv() {
        val env = mutableMapOf<String, String>(
            "hello" to "world",
        )
        ProcessKit.run("echo \$hello", env = env)
        ProcessKit.run("echo \$hello123", env = env)
    }

    @Test
    fun testShell() {
        val f = File("build", "${System.currentTimeMillis()}.log")
        val cmd = "grep '@Test' -R . > ${f.absolutePath}"
        ProcessKit.run(cmd)

        val cmd2 = "find . -name \"*.log\"  | xargs rm -rf  "
        val ret = ProcessKit.run(cmd2)
        Assert.assertEquals(ret, 0)
        Assert.assertEquals(f.exists(), false)
    }

    @Test
    fun testShell2() {
        val cmd = "cd ~; ls -lh"
        ProcessKit.run(cmd)
    }
}