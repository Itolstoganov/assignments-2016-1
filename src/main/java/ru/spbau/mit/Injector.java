package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public final class Injector {
    private Injector() {
    }

    private static final Map<String, DepNode> vertices = new HashMap<>();
    static List <String> implClassNames;

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        DepNode root = new DepNode(rootClassName, 0);
        root.makeDeps();
        return root.obj;
    }

    private static class DepNode {
        private boolean isVisited;
        private ArrayList<DepNode> children;
        private String name;
        private String implName;
        private int level;
        private Object obj;

        private DepNode(String oth_name, int oth_level) {
            isVisited = false;
            children = new ArrayList<>();
            name = oth_name;
            implName = "";
            level = oth_level;
        }

        void makeDeps () throws ClassNotFoundException, AmbiguousImplementationException, ImplementationNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
            Class clazz = Class.forName(name);
            Constructor[] allConstructors = clazz.getDeclaredConstructors();
            Constructor constructor = allConstructors[0];
            Class <?>[] ptypes = constructor.getParameterTypes();
            List<String> pnames = new ArrayList<>();
            for (int i = 0; i < ptypes.length; i++) {
                pnames.add(i, ptypes[i].getName());
            }
            for (int i = 0; i < pnames.size(); i++) {
                if (!vertices.containsKey(pnames.get(i))) {
                    DepNode node = new DepNode(pnames.get(i), level + 1);
                    vertices.put(pnames.get(i), node);
                }
                else if (vertices.get(pnames.get(i)).level > level + 1) {
                    vertices.get(pnames.get(i)).level  = level + 1;
                }
                children.add(vertices.get(pnames.get(i)));
            }
            if (level != 0) {
                int impls = 0;
                Class impl;
                List<String> implz = implClassNames;
                for (String name : implClassNames) {
                    Class other = Class.forName(name);
                    if (clazz.isAssignableFrom(other)) {
                        impls += 1;
                        impl = other;
                    }
                }
                if (impls == 0)
                    throw new ImplementationNotFoundException();
                if (impls > 1)
                    throw new AmbiguousImplementationException();

                implName = impl.getName();
            }

            for (DepNode node : children) {
                node.makeDeps();
            }

            List <Object> params = new ArrayList<>();
            for (DepNode node : children) {
                 params.add(node.obj);
            }
            Object obj = (Object) constructor.NewInstance(params);
        }
    }
}
