package ru.spbau.mit;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Serialization {

    public static final int THREE = 3;

    private Serialization() {
    }

    public static void serialize(Object target, String filename) throws FileNotFoundException,
            UnsupportedEncodingException, InvocationTargetException, IllegalAccessException {
        Class<?> c = target.getClass();
        Method[] methods = c.getMethods();
        PrintWriter out = new PrintWriter(filename, "UTF-8");
        List<Method> getters = Arrays.stream(methods)
                .filter(Serialization::isGetter)
                .collect(Collectors.toList());
        for (Method getter : getters) {
            String returnType = (getter.getReturnType()).toString();
            if (Objects.equals(returnType, "int")) {
                out.print("int ");
            }
            if (Objects.equals(returnType, "class java.lang.String")) {
                out.print("string ");
            }
            String name = getter.getName().substring(THREE);
            out.print(name + ' ');
            Object returnValue = getter.invoke(target);
            out.println(returnValue);
        }
        out.close();
    }

    public static Object deserialize(String fullClassName, String filename) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException, IOException {
        Class c = Class.forName(fullClassName);
        Object target = c.newInstance();
        Reader reader = new FileReader(filename);
        boolean isEmpty = true;
        Stream<String> lines = Files.lines(Paths.get(filename));
        lines.forEach(s -> {
                    try {
                        setter(target, s);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );
        return target;
    }

    private static void setter(Object target, String params) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        String[] words = params.split(" ");
        String type = words[0];
        String name = "set" + words[1];
        String value = words[2];

        if (Objects.equals(type, "int")) {
            int valuei = Integer.parseInt(value);
            Method method = target.getClass().getMethod(name, int.class);
            method.invoke(target, valuei);
        }
        if (Objects.equals(type, "string")) {
            Method method = target.getClass().getMethod(name, java.lang.String.class);
            method.invoke(target, value);
        }

    }

    private static boolean isGetter(Method method) {
        if (!method.getName().startsWith("get")) {
            return false;
        }
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        if (!int.class.equals(method.getReturnType()) && !String.class.equals(method.getReturnType())) {
            return false;
        }
        return true;
    }
}
