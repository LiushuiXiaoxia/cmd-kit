package com.github.liushuixiaoxia.cmdkit

import org.testng.annotations.Test
import java.io.File

class CmdTest {

    @Test
    fun process() {
        val p = Runtime.getRuntime().exec("ls -lh")
        val ret = p.asCmdResult()
        println("ret = $ret")
        println("pid = ${p.reflectPid()}")
    }

    @Test
    fun testExample() {
        val tmp = File("build/tmp", "t-${System.currentTimeMillis()}").canonicalFile
        println("tmp = $tmp")
        CmdKit.call("mkdir -p $tmp").check("make dir fail")
        CmdKit.call("git clone git@github.com:LiushuiXiaoxia/process-kit.git $tmp").check("git check failed")
        CmdKit.run("cd $tmp && git status && git log -5")
        CmdKit.run("cd $tmp && ./gradlew clean assemble")
        CmdKit.run("rm -rf $tmp")

        println("$tmp exists = ${tmp.exists()}")
    }

    @Test
    fun testExample2() {
        val tmp = File("build/tmp", "t-${System.currentTimeMillis()}").canonicalFile
        println("tmp = $tmp")
        callCmd("mkdir -p $tmp").check("make dir fail")
        callCmd("git clone git@github.com:LiushuiXiaoxia/process-kit.git $tmp").check("git check failed")
        runCmd("cd $tmp && git status && git log -5")
        runCmd("cd $tmp && ./gradlew clean assemble")
        runCmd("rm -rf $tmp")
        runCmd("ls -lh")

        println("$tmp exists = ${tmp.exists()}")
    }
}