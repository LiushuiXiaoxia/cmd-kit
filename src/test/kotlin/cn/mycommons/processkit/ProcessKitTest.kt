package cn.mycommons.processkit

import cn.mycommons.cn.mycommons.processkit.ProcessKit
import org.testng.annotations.Test

class ProcessKitTest {


    @Test
    fun test() {
        val r = ProcessKit.exec("ls -alh", null)
        println("r = $r")
    }

    @Test
    fun test3() {
        val r = ProcessKit.exec("ls -alh", null)
        println(r.display())
    }

    @Test
    fun test4() {
        val r = ProcessKit.exec("ls -alh", null)
        r.all.forEach { println(it) }
    }
}