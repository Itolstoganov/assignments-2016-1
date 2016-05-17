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

public class Serialization {

    public static void serialize(Object target, String filename) throws FileNotFoundException, UnsupportedEncodingException, InvocationTargetException, IllegalAccessException {
        Class<?> c = target.getClass();
        Method[] methods = c.getMethods();
        PrintWriter out = new PrintWriter(filename, "UTF-8");
        List<Method> getters = Arrays.stream(methods)
                .filter(Serialization::isGetter)
                .collect(Collectors.toList());
        for(Method getter : getters) {
            String returnType = (getter.getReturnType()).toString();
            if(Objects.equals(returnType, "int"))
            {
                out.print("int ");
            }
            if(Objects.equals(returnType, "class java.lang.String"))
            {
                out.print("string ");
            }
            String name = getter.getName().substring(3);
            out.print(name + ' ');
            Object returnValue = getter.invoke(target);
            out.println(returnValue);
        }
        out.close();
    }

    public static void deserialize(String fullClassName, String filename) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
        Class c = Class.forName(fullClassName);
        Object target = c.newInstance();
        Reader reader = new FileReader(filename);
        boolean isEmpty = true;
        Stream<String> lines = Files.lines(Paths.get(filename));
     //   lines.forEach(s ->


        //);
    }

//    private static boolean Setter(Object target, String params) throws NoSuchMethodException {
//        String[] words = params.split(" ");
//        String name = "set" + words[0];
//        String value = words[1];
//
//        Method method = target.getClass().getMethod(name);
//        method.invoke(obj, arg1, arg2,...);
//    }

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
