package com.github.liushuixiaoxia.processkit;

import org.testng.annotations.Test;

public class JavaTest {

    @Test
    public void testRun() {
        ProcessKit.run("ls -alh");
    }

    @Test
    public void testCall() {
        var ret = ProcessKit.call("ls -alh");
        System.out.println("ret = " + ret);
    }
}