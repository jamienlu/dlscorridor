package cn.jamie.dlscorridor.core.util;

import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


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
