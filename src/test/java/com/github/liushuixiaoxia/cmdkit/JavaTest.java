package com.github.liushuixiaoxia.cmdkit;


import org.junit.Test;

public class JavaTest {

    @Test
    public void testRun() {
        CmdKit.run("ls -alh");
    }

    @Test
    public void testCall() {
        var ret = CmdKit.call("ls -alh");
        System.out.println("ret = " + ret);
    }
}