package cn.mycommons.processkit

import org.testng.Assert
import org.testng.annotations.Test
import java.io.File

class ProcessKitTestCall {

    @Test
    fun testSuccess() {
        println("before testSuccess")
        val ret = ProcessKit.call("mkdir -p build/tmp-${System.currentTimeMillis()}")
        println("after testSuccess, ret = $ret")
    }

    @Test
    fun testFail() {
        println("before testFail")
        val ret = ProcessKit.call("ls -alh dir-${System.currentTimeMillis()}")
        println("after testFail, ret = $ret")
    }

    @Test(expectedExceptions = [ProcessExecException::class])
    fun testCheck() {
        println("before testFail")
        val ret = ProcessKit.call("ls -alh dir-${System.currentTimeMillis()}", check = true)
        println("after testFail, ret = $ret")
    }

    @Test
    fun testGit() {
        ProcessKit.call("git --version")
        ProcessKit.call("git status")
        ProcessKit.call("git log --oneline -5")
    }

    @Test
    fun testWs() {
        val ws = File(System.getProperty("user.home"))
        ProcessKit.call("ls -alh", ws)
        ProcessKit.call("pwd", ws)
    }

    @Test
    fun testEnv() {
        val env = mutableMapOf<String, String>(
            "hello" to "world",
        )
        ProcessKit.call("echo \$hello", env = env)
        ProcessKit.call("echo \$hello123", env = env)
    }

    @Test
    fun testShell() {
        val f = File("build", "${System.currentTimeMillis()}.log")
        val cmd = "grep '@Test' -R . > ${f.absolutePath}"
        ProcessKit.call(cmd)

        val cmd2 = "find . -name \"*.log\"  | xargs rm -rf  "
        val ret = ProcessKit.call(cmd2)
        Assert.assertEquals(ret.exitValue, 0)
        Assert.assertEquals(f.exists(), false)
    }

    @Test
    fun testShell2() {
        val cmd = "cd ~; ls -lh"
        ProcessKit.call(cmd).check("shell")
    }
}