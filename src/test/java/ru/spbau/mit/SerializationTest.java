package ru.spbau.mit;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

public class SerializationTest {

    public class TestClass {
        public double other;
        public int value;
        public String name;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getOther() {
            return other;
        }

        public void setOther(double other) {
            this.other = other;
        }
    }

//    @Test
//    public void TestFields() throws NoSuchFieldException, ClassNotFoundException {
//        Class<?> c = Class.forName("SerializationTest$TestClass");
//        Field f = c.getField("a");
//        Field f2 = c.getField("s");
//        System.out.format("Type: %s%n", f.getType());
//        System.out.format("Type: %s%n", f2.getType());
//        System.out.format("GenericType: %s%n", f.getGenericType());
//        System.out.format("GenericType: %s%n", f2.getGenericType());
//    }
    @Test
    public void testSerialize() throws InvocationTargetException, FileNotFoundException, IllegalAccessException, UnsupportedEncodingException {
        TestClass testClass = new TestClass();
        testClass.setName("albert");
        testClass.setOther(4.15);
        testClass.setValue(55);
        Serialization.serialize(testClass, "src/test/resources/test01");
    }

}