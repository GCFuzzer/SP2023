package edu.berkeley.cs.jqf.examples.GadgetChain;

import com.sun.tools.javac.parser.UnicodeReader;
import sun.misc.Unsafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectTest {
    public static Object objectInit(String classname) throws Exception {
        Class<?> target_classname = Class.forName(classname);
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        return unsafe.allocateInstance(target_classname);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(objectInit("org.apache.commons.collections4.comparators.TransformingComparator"));
        System.out.println(objectInit("org.apache.commons.collections4.comparators.TransformingComparator").getClass());

        List<String> lines = Files.readAllLines(new File("chain_ysoserial.txt").toPath());

        for(String line : lines){
            // Do whatever you want
            System.out.println(line);
            break;
        }
    }
}
