package io.github.jamienlu.discorridor.core.util;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class RpcMethodUtil {
    public static Set<String> objectMethodNames = Arrays.stream(Object.class.getDeclaredMethods())
            .map(Method::getName).collect(Collectors.toSet());

    public static boolean notPermissionMethod(String methodName) {
        return objectMethodNames.contains(methodName);
    }

}
