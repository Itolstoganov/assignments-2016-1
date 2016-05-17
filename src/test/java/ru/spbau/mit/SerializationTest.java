package ru.spbau.mit;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

class TestClass {
    public double other;
    public int value;
    public String name;

    public void TestClass() {
        this.other = 0;
        this.value = 0;
        this.name = "name";
    }
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

public class SerializationTest {

    @Test
    public void testSerialize() throws InvocationTargetException, IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        TestClass testClass = new TestClass();
        testClass.setName("albert");
        testClass.setOther(4.15);
        testClass.setValue(55);
        Serialization.serialize(testClass, "src/test/resources/test01");
        String name = (testClass.getClass()).getName();
        TestClass testClass1 = new TestClass();
        testClass1 = (TestClass) Serialization.deserialize(name,  "src/test/resources/test01");
        assertEquals(testClass1.getName(), testClass.getName());
        assertEquals(testClass1.getValue(), testClass.getValue());
        assertEquals(testClass1.getOther(), 0, 0.001);
    }

}