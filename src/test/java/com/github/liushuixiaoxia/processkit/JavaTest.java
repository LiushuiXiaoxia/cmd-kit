package com.github.liushuixiaoxia.processkit;

import org.testng.annotations.Test;

public class JavaTest {

    @Test
    public void testRun() {
        ProcessKit.run("ls -alh", null, 0, null);
    }

    @Test
    public void testCall() {
        var ret = ProcessKit.call("ls -alh", null, 10, null, false);
        System.out.println("ret = " + ret);
    }
}
