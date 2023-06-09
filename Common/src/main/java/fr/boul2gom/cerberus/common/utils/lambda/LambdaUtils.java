package fr.boul2gom.cerberus.common.utils.lambda;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LambdaUtils {

    public static SerializedLambda serialize(Object lambda) throws LambdaException {
        try {
            final Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);

            return (SerializedLambda) writeReplace.invoke(lambda);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new LambdaException("Failed to serialize lambda", e);
        }
    }

    public static Class<?> getLambdaClass(SerializedLambda lambda) throws LambdaException {
        try {
            final String className = lambda.getImplClass().replace("/", ".");
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new LambdaException("Failed to get lambda class", e);
        }
    }

    public static Method getMethod(SerializedLambda lambda) throws LambdaException {
        final String methodName = lambda.getImplMethodName();
        final Class<?> clazz = LambdaUtils.getLambdaClass(lambda);

        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        throw new LambdaException("Failed to get lambda method");
    }
}
