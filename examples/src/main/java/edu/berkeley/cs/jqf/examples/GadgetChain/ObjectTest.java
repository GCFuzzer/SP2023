package edu.berkeley.cs.jqf.examples.GadgetChain;

import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.AbstractStringGenerator;
import com.sun.tools.javac.parser.UnicodeReader;
import edu.berkeley.cs.jqf.examples.common.AlphaStringGenerator;
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

        Class clazz = objectInit("org.apache.commons.collections4.comparators.TransformingComparator").getClass();
        // 获取类中声明的字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 避免 can not access a member of class com.java.test.Person with modifiers "private"
            field.setAccessible(true);
            try {
                System.out.println(field.getName() + ":"+ field.get(objectInit("org.apache.commons.collections4.comparators.TransformingComparator")));
                System.out.println(field.getGenericType());
                System.out.println(field.getClass());
                System.out.println(field.getType());
                if (field.getType().isAssignableFrom(objectInit("org.apache.commons.collections4.comparators.TransformingComparator").getClass())) {
                    System.out.println(13123123);
                }
                System.out.println("----------------------");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        List<String> lines = Files.readAllLines(new File("chain_ysoserial.txt").toPath());

        for(String line : lines){
            // Do whatever you want
            System.out.println(line);
            break;
        }

        if (java.util.Comparator.class.isAssignableFrom(objectInit("org.apache.commons.collections4.comparators.TransformingComparator").getClass())) {
            System.out.println(1111);
        }
        if (Object.class.isAssignableFrom(int.class)) {
            System.out.println(3333);
        }
    }
}
