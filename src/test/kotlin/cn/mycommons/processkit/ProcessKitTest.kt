package cn.mycommons.processkit

import org.testng.annotations.Test

class ProcessKitTest {


    @Test
    fun test() {
        val r = ProcessKit.exec("ls -alh")
        println("r = $r")
    }

    @Test
    fun test3() {
        val r = ProcessKit.exec("ls -alh")
        println(r.display())
    }

    @Test
    fun test4() {
        val r = ProcessKit.exec("ls -alh")
        r.all.forEach { println(it) }
    }

    @Test
    fun testCheck() {
        val r = ProcessKit.exec("ls -alh", ws = null)
        r.check("ls fail")
    }
}