package edu.berkeley.cs.jqf.examples.GadgetChain;

import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.ceil;
import static java.lang.Math.log;

public class generatorTest {

    private static final GeometricDistribution geom = new GeometricDistribution();
    private static final double MEAN_ARRAY_DEPTH = 1.2;

    public static Object objectInit (String classname) throws Exception {
        Class<?> target_classname = Class.forName(classname);
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        return unsafe.allocateInstance(target_classname);
    }

    public static Object generate() throws Exception {

        List<Object> dictionary = new ArrayList<>();
        dictionary.add(objectInit("java.util.PriorityQueue"));
        dictionary.add(objectInit("org.apache.commons.collections4.comparators.TransformingComparator"));
        dictionary.add(objectInit("org.apache.commons.collections4.functors.InvokerTransformer"));
        System.out.println(dictionary);

        for (int idx=0; idx<dictionary.size()-1; idx++) {
            Field[] target_field = dictionary.get(idx).getClass().getDeclaredFields();//获取所有属性
            for (Field field : target_field) {
                field.setAccessible(true);
                if (Modifier.isFinal(field.getModifiers())) {
                    if (field.getType().isAssignableFrom(dictionary.get(idx+1).getClass())) {
                        try {
                            field.set(dictionary.get(idx), dictionary.get(idx+1));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    continue;
                }
                if (field.getType().equals(int.class)) {
                    System.out.println(field.getName());
                }
            }
        }
        System.out.println(dictionary.get(1));
        Field[] fields = dictionary.get(1).getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field1 -> {
            //获取是否可访问
            boolean flag = field1.isAccessible();
            try {
                //设置该属性总是可访问
                field1.setAccessible(true);
                System.out.println("变量类型为:" + field1.getType().getName() + ", 成员变量" + field1.getName() + "的值为:" + field1.get(dictionary.get(1)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //还原可访问权限
            field1.setAccessible(flag);
        });

        System.out.println(dictionary.get(2));
        return dictionary.get(0);
    }

    public static int sampleGeometric(Random random, double mean) {
        double p = 1 / mean;
        double uniform = random.nextDouble();
        return (int) ceil(log(1 - uniform) / log(1 - p));
    }

    public static void main(String[] args) throws Exception {
        Object obj = generatorTest.generate();

        System.out.println(obj);

        Field[] fields = obj.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field1 -> {
            //获取是否可访问
            boolean flag = field1.isAccessible();
            try {
                //设置该属性总是可访问
                field1.setAccessible(true);
                System.out.println("变量类型为:" + field1.getType().getName() + ", 成员变量" + field1.getName() + "的值为:" + field1.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //还原可访问权限
            field1.setAccessible(flag);
        });

        Random random = new Random();
        SourceOfRandomness rand1 = new SourceOfRandomness(random);
        System.out.println(sampleGeometric(random, 2));
        System.out.println(geom.sampleWithMean(MEAN_ARRAY_DEPTH, rand1));
    }
}
