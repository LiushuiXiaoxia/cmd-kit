package cn.mycommons.processkit

import org.testng.annotations.Test

class ProcessTest {

    @Test
    fun process() {
        val p = Runtime.getRuntime().exec("ls -lh")
        val ret = p.asProcessResult()
        println("ret = $ret")
        println("pid = ${p.reflectPid()}")
    }
}