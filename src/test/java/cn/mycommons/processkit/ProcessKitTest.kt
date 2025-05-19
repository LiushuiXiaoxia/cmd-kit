package cn.mycommons.processkit

import org.testng.annotations.Test

class ProcessKitTest {


    @Test
    fun test() {
        val r = ProcessKit.call("ls -alh")
        println("r = $r")
    }

    @Test
    fun test3() {
        val r = ProcessKit.call("ls -alh")
        println(r.display())
    }

    @Test
    fun test4() {
        val r = ProcessKit.call("ls -alh")
        r.all.forEach { println(it) }
    }

    @Test
    fun testCheck() {
        val r = ProcessKit.call("ls -alh", ws = null)
        r.check("ls fail")
    }
}