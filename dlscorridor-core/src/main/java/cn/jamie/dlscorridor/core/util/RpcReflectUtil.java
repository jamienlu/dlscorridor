package cn.jamie.dlscorridor.core.util;


import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 反射方法封装 吞掉反射异常
 *
 * @author jamieLu
 * @create 2024-03-12
 */
@Slf4j
public class RpcReflectUtil {
    private static final Map<String,Class<?>> basicTypeClassMap = Map.of("boolean",boolean.class,"byte",byte.class,"char",char.class,"short",short.class,"int",int.class,"float",float.class,"long",long.class,"double",double.class);

    /**
     * 解析方法签名参数
     *
     * @param method 方法
     * @return 方法签名
     */
    public static String analysisMethodSign(Method method) {
        StringBuilder methodSign = new StringBuilder(method.getName());
        Class<?>[] typeClasses = method.getParameterTypes();
        for (Class<?> typeClass : typeClasses) {
            methodSign.append("/").append(typeClass.getName());
        }
        return methodSign.toString();
    }

    /**
     * 识别方法签名类
     *
     * @param typeStr 签名类
     * @return Class
     */
    public static Class<?> findTypeClass(String typeStr) {
        Class<?> clazz = null;
        try {
            // 数组类型
            if (typeStr.startsWith("[")) {
                clazz = Class.forName(typeStr);
                // 基本类型
            } else if (basicTypeClassMap.containsKey(typeStr)) {
                clazz = basicTypeClassMap.get(typeStr);
            } else {
                // 对象引用类型
                clazz = Class.forName(typeStr);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return clazz;
    }
    /**
     * 获取实现类带指定注解的接口
     *
     * @param clazz 实现类
     * @param annotationType 接口注解
     * @return List
     */
    public static List<Class<?>> findAnnotationInterfaces(Class<?> clazz, Class<? extends Annotation> annotationType) {
        return Arrays.stream(clazz.getAnnotatedInterfaces())
                .filter(x -> x.isAnnotationPresent(annotationType))
                .map(x -> (Class<?>)x.getType()).collect(Collectors.toList());
    }
    /**
     * 根据方法签名获取类的方法
     *
     * @param clazz 查找类
     * @param methodSign 方法签名
     * @return Method
     */
    public static Method findMethod(Class<?> clazz, String methodSign) {
        String[] contents = methodSign.split("/", -1);
        String methodName = contents[0];
        try {
            if (contents.length == 1) {
                return clazz.getDeclaredMethod(methodName);
            } else {
                Class<?>[] clazzs = Arrays.stream(Arrays.copyOfRange(contents,1,contents.length))
                        .map(RpcReflectUtil::findTypeClass).toArray(Class<?>[]::new);
                return clazz.getDeclaredMethod(methodName,clazzs);
            }
        } catch (NoSuchMethodException e) {
           log.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * 查询类和其父类带指定注解的字段
     *
     * @param annotationClass 类
     * @param annotationType 注解类型
     * @return List
     */
    public static List<Field> findAnnotationFields(Class<?> annotationClass, Class<? extends Annotation> annotationType) {
        List<Field> result = new ArrayList<>();
        while (annotationClass != null) {
            Field[] allFileds = annotationClass.getDeclaredFields();
            result.addAll(Arrays.stream(allFileds)
                    .filter(x -> x.isAnnotationPresent(annotationType))
                    .toList());
            annotationClass = annotationClass.getSuperclass();
        }
        return result;
    }
}
