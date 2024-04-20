package io.github.jamienlu.discorridor.core.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
public class MockUtil {
    public static Object mock(Class type) {
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return 1;
        } else if(type.equals(Long.class) || type.equals(Long.TYPE)) {
            return 10000L;
        }
        if(Number.class.isAssignableFrom(type)) {
            return 1;
        }
        if(type.equals(String.class)) {
            return "mock_string";
        }
        return mockPojo(type);
    }

    @SneakyThrows
    private static Object mockPojo(Class type) {
        Object result = type.getDeclaredConstructor().newInstance();
        Field[] fields = type.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            Class<?> fType = f.getType();
            Object fValue = mock(fType);
            f.set(result, fValue);
        }
        return result;
    }
}
