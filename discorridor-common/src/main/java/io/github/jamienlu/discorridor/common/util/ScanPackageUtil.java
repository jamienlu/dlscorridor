package io.github.jamienlu.discorridor.common.util;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-24
 */
public class ScanPackageUtil {
    public static List<Class<?>> scanClass(String prePackgeClass, final Class<? extends Annotation> annotationClass) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(prePackgeClass)));
        return new ArrayList<>(reflections.getTypesAnnotatedWith(annotationClass));
    }
    public static List<Method> scanMethods(String prePackgeClass, final Class<? extends Annotation> annotationClass) {
        Reflections reflections = new Reflections(prePackgeClass, Scanners.MethodsAnnotated);
        return new ArrayList<>(reflections.getMethodsAnnotatedWith(annotationClass));
    }
    public static List<Field> scanFields(String prePackgeClass, final Class<? extends Annotation> annotationClass) {
        Reflections reflections = new Reflections(prePackgeClass, Scanners.FieldsAnnotated);
        return new ArrayList<>(reflections.getFieldsAnnotatedWith(annotationClass));
    }
}
