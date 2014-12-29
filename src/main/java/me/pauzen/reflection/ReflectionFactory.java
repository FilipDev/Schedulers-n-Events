package me.pauzen.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/*
 * Written by FilipDev on 12/24/14 12:19 AM.
 */

public final class ReflectionFactory {

    private static Map<Class, List<Field>>                           HIERARCHIC_CACHED_CLASS_FIELDS  = new HashMap<>();
    private static Map<Class, List<Field>>                           CACHED_CLASS_FIELDS             = new HashMap<>();
    private static Map<Class, List<Field>>                           CACHED_CLASS_STATIC_FIELDS      = new HashMap<>();
    private static Map<Entry<Class, String>, Field>                  CACHED_FIELDS                   = new HashMap<>();
    private static Map<Class, List<Method>>                          HIERARCHIC_CACHED_CLASS_METHODS = new HashMap<>();
    private static Map<Class, List<Method>>                          CACHED_CLASS_METHODS            = new HashMap<>();
    private static Map<Class, List<Method>>                          CACHED_CLASS_STATIC_METHODS     = new HashMap<>();
    private static Map<Entry<Class, Entry<String, Class[]>>, Method> CACHED_METHODS                  = new HashMap<>();
    private static Map<Entry<Class, Entry<String, Class[]>>, Method> CACHED_HIERARCHIC_METHODS       = new HashMap<>();

    private ReflectionFactory() {
    }

    /**
     * Removes final modifier from a Field object.
     *
     * @param field Field to remove modifier from.
     */
    public static void removeFinal(Field field) {
        try {
            Field modifier = Field.class.getDeclaredField("modifiers");
            modifier.setAccessible(true);
            modifier.set(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts an array of classes to an array of Strings with the String being the canonical name of the class.
     *
     * @param classes The class array to convert to a String array.
     * @return The String array.
     */
    public static String[] toStringArray(Class... classes) {
        String[] strings = new String[classes.length];
        for (int i = 0; i < classes.length; i++) strings[i] = classes[i].getCanonicalName();
        return strings;
    }

    /**
     * Finds the field in the class hierarchy.
     *
     * @param clazz The class where to start the search.
     * @param name  The name of the field to find.
     * @return Either the found field, or a null value if one is not found.
     */
    public static Field getField(Class clazz, String name) {
        if (CACHED_FIELDS.containsKey(new Entry<>(clazz, name))) return CACHED_FIELDS.get(new Entry<>(clazz, name));
        for (Field field : getFieldsHierarchic(clazz))
            if (field.getName().equals(name)) {
                field.setAccessible(true);
                CACHED_FIELDS.put(new Entry<>(clazz, name), field);
                return field;
            }
        return null;
    }

    /**
     * Finds all fields in the class and all its super classes.
     *
     * @param clazz The class to return all found fields from.
     * @return Set of all found fields in the class.
     */
    public static List<Field> getFieldsHierarchic(Class clazz) {
        if (HIERARCHIC_CACHED_CLASS_FIELDS.containsKey(clazz)) return HIERARCHIC_CACHED_CLASS_FIELDS.get(clazz);
        List<Field> fields = new ArrayList<>();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            List<Field> fields1 = getFields(clazz);
            Collections.addAll(fields, fields1.toArray(new Field[fields1.size()]));
        }
        HIERARCHIC_CACHED_CLASS_FIELDS.put(clazz, fields);
        return fields;
    }

    /**
     * Finds all fields only in the specified class.
     *
     * @param clazz The class to return all found fields from.
     * @return Set of all fields in the class.
     */
    public static List<Field> getFields(Class clazz) {
        if (CACHED_CLASS_FIELDS.containsKey(clazz)) return CACHED_CLASS_FIELDS.get(clazz);
        List<Field> fields = new ArrayList<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        CACHED_CLASS_FIELDS.put(clazz, fields);
        return fields;
    }

    /**
     * Finds only the static fields within a class and all its super classes.
     *
     * @param clazz The class to search for static fields.
     * @return Set of the static fields in the class.
     */
    public static List<Field> getStaticFieldsHierarchic(Class clazz) {
        if (CACHED_CLASS_STATIC_FIELDS.containsKey(clazz)) return CACHED_CLASS_STATIC_FIELDS.get(clazz);
        List<Field> fields = new ArrayList<>();
        for (Field field : getFieldsHierarchic(clazz))
            if (Modifier.isStatic(field.getModifiers())) fields.add(field);
        CACHED_CLASS_STATIC_FIELDS.put(clazz, fields);
        return fields;
    }

    /**
     * Finds only static fields only in the specified class.
     *
     * @param clazz The class to return all found static fields from.
     * @return Set of all static fields in the class.
     */
    public static List<Field> getStaticFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : getFields(clazz))
            if (Modifier.isStatic(field.getModifiers())) fields.add(field);
        return fields;
    }

    /**
     * Finds all methods in the class specified.
     *
     * @param clazz The class to return all found methods from.
     * @return Set of all methods in the class.
     */
    public static List<Method> getMethods(Class clazz) {
        if (CACHED_CLASS_METHODS.containsKey(clazz)) return CACHED_CLASS_METHODS.get(clazz);
        List<Method> methods = new ArrayList<>();
        Collections.addAll(methods, clazz.getDeclaredMethods());
        CACHED_CLASS_METHODS.put(clazz, methods);
        return methods;
    }

    /**
     * Finds all methods in the class specified, and its super classes.
     *
     * @param clazz The class to return all found methods from.
     * @return Set of all methods in the class hierarchy.
     */
    public static List<Method> getMethodsHierarchic(Class clazz) {
        if (HIERARCHIC_CACHED_CLASS_METHODS.containsKey(clazz)) return HIERARCHIC_CACHED_CLASS_METHODS.get(clazz);
        List<Method> methods = new ArrayList<>();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            List<Method> methods1 = getMethods(clazz);
            Collections.addAll(methods, methods1.toArray(new Method[methods1.size()]));
        }
        HIERARCHIC_CACHED_CLASS_METHODS.put(clazz, methods);
        return methods;
    }

    /**
     * Finds all static methods in the class specified.
     *
     * @param clazz The class to return all found static methods from.
     * @return Set of all static methods in the class.
     */
    public static List<Method> getStaticMethods(Class clazz) {
        List<Method> methods = new ArrayList<>();
        for (Method method : getMethods(clazz))
            if (Modifier.isStatic(method.getModifiers())) methods.add(method);
        return methods;
    }

    /**
     * Finds all static methods in the class specified and its super classes.
     *
     * @param clazz The class to return all found static methods from.
     * @return Set of all static methods in the class hierarchy.
     */
    public static List<Method> getStaticMethodsHierarchic(Class clazz) {
        if (CACHED_CLASS_STATIC_METHODS.containsKey(clazz)) return CACHED_CLASS_STATIC_METHODS.get(clazz);
        List<Method> methods = new ArrayList<>();
        for (Method method : getMethodsHierarchic(clazz))
            if (Modifier.isStatic(method.getModifiers())) methods.add(method);
        CACHED_CLASS_STATIC_METHODS.put(clazz, methods);
        return methods;
    }

    /**
     * Finds a method in the class specified.
     *
     * @param clazz  The class to find the method in.
     * @param name   The name of method to find.
     * @param params The class params of the method to find.
     * @return The found method. Null if not found.
     */
    public static Method getMethod(Class clazz, String name, Class... params) {
        Entry<Class, Entry<String, Class[]>> key = new Entry<>(clazz, new Entry<>(name, params));
        if (CACHED_METHODS.containsKey(key)) return CACHED_METHODS.get(key);
        for (Method method : getMethods(clazz))
            if (method.getName().equals(name)) {
                method.setAccessible(true);
                CACHED_METHODS.put(key, method);
                return method;
            }
        return null;
    }

    /**
     * Finds a method in the class specified and all its super classes.
     *
     * @param clazz  The class to find the method in.
     * @param name   The name of method to find.
     * @param params The class params of the method to find.
     * @return The found method. Null if not found.
     */
    public static Method getMethodHierarchic(Class clazz, String name, Class... params) {
        Entry<Class, Entry<String, Class[]>> key = new Entry<>(clazz, new Entry<>(name, params));
        if (CACHED_HIERARCHIC_METHODS.containsKey(key)) return CACHED_HIERARCHIC_METHODS.get(key);
        for (Method method : getMethodsHierarchic(clazz))
            if (method.getName().equals(name)) {
                method.setAccessible(true);
                CACHED_HIERARCHIC_METHODS.put(key, method);
                return method;
            }
        return null;
    }
}
