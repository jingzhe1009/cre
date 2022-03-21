/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.bonc.frame.util;

import org.springframework.util.ReflectionUtils;
import scala.annotation.meta.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class to handle reflection on java objects.
 * The class contains static methods to call reflection
 * methods, catch any exceptions, converting them
 * to ReflectExceptions.
 */
// CheckStyle:FinalClassCheck OFF - backward compatible
public class ReflectUtil {

    /**
     * private constructor
     */
    private ReflectUtil() {
    }

    /**
     * Create an instance of a class using the constructor matching
     * the given arguments.
     *
     * @since Ant 1.8.0
     */
    public static <T> T newInstance(Class<T> ofClass,
                                    Class<?>[] argTypes,
                                    Object[] args) throws ReflectException {
        try {
            Constructor<T> con = ofClass.getConstructor(argTypes);
            return con.newInstance(args);
        } catch (Exception t) {
            throwReflectException(t);
            return null; // NotReached
        }
    }

    /**
     * Call a method on the object with no parameters.
     *
     * @param obj        the object to invoke the method on.
     * @param methodName the name of the method to call
     * @return the object returned by the method
     */
    public static Object invoke(Object obj, String methodName) throws ReflectException {
        try {
            Method method;
            method = obj.getClass().getMethod(
                    methodName, (Class[]) null);
            return method.invoke(obj, (Object[]) null);
        } catch (Exception t) {
            throwReflectException(t);
            return null; // NotReached
        }
    }

    /**
     * Call a method on the object with no parameters.
     * Note: Unlike the invoke method above, this
     * calls class or static methods, not instance methods.
     *
     * @param obj        the object to invoke the method on.
     * @param methodName the name of the method to call
     * @return the object returned by the method
     */
    public static Object invokeStatic(Object obj, String methodName) throws ReflectException {
        try {
            Method method;
            method = ((Class<?>) obj).getMethod(
                    methodName, (Class[]) null);
            return method.invoke(obj, (Object[]) null);
        } catch (Exception t) {
            throwReflectException(t);
            return null; // NotReached
        }
    }

    /**
     * Call a method on the object with one argument.
     *
     * @param obj        the object to invoke the method on.
     * @param methodName the name of the method to call
     * @param argType    the type of argument.
     * @param arg        the value of the argument.
     * @return the object returned by the method
     */
    public static Object invoke(
            Object obj, String methodName, Class<?> argType, Object arg)
            throws ReflectException {
        try {
            Method method;
            method = obj.getClass().getMethod(
                    methodName, new Class[]{argType});
            return method.invoke(obj, new Object[]{arg});
        } catch (Exception t) {
            throwReflectException(t);
            return null; // NotReached
        }
    }

    /**
     * Call a method on the object with two argument.
     *
     * @param obj        the object to invoke the method on.
     * @param methodName the name of the method to call
     * @param argType1   the type of the first argument.
     * @param arg1       the value of the first argument.
     * @param argType2   the type of the second argument.
     * @param arg2       the value of the second argument.
     * @return the object returned by the method
     */
    public static Object invoke(
            Object obj, String methodName, Class<?> argType1, Object arg1,
            Class<?> argType2, Object arg2) throws ReflectException {
        try {
            Method method;
            method = obj.getClass().getMethod(
                    methodName, new Class[]{argType1, argType2});
            return method.invoke(obj, new Object[]{arg1, arg2});
        } catch (Exception t) {
            throwReflectException(t);
            return null; // NotReached
        }
    }

    /**
     * Get the value of a field in an object.
     *
     * @param obj       the object to look at.
     * @param fieldName the name of the field in the object.
     * @return the value of the field.
     * @throws ReflectException if there is an error.
     */
    public static Object getField(Object obj, String fieldName)
            throws ReflectException {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception t) {
            throwReflectException(t);
            return null; // NotReached
        }
    }

    public static String getFieldStringValue(Object o, String fieldName) throws ReflectException {
        try {
            final Class<?> clazz = o.getClass();
//            final Field field = clazz.getDeclaredField(fieldName);
//            field.setAccessible(true);
//            return field.get(o).toString();

            Field field1 = ReflectionUtils.findField(clazz, fieldName);
            ReflectionUtils.makeAccessible(field1);
            return ReflectionUtils.getField(field1, o).toString();
        } catch (Exception e) {
            throwReflectException(e);
            return null; // NotReached
        }
    }

    public static void setField(Object obj, String fieldName, Object value)
            throws ReflectException {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception t) {
            throwReflectException(t);
        }
    }

    public static Method findMethod(String className, String methodName) throws ReflectException {
        final Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectException("没有找到类[" + className + "]");
        }
        final Method[] methods = clazz.getDeclaredMethods();
        Method m = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                m = method;
                break;
            }
        }
        return m;
    }

    /**
     * A method to convert an invocationTargetException to
     * a buildexception and throw it.
     *
     * @param t the invocation target exception.
     * @throws ReflectException the converted exception.
     */
    public static void throwReflectException(Exception t)
            throws ReflectException {
        throw toReflectException(t);
    }

    /**
     * A method to convert an invocationTargetException to
     * a buildexception.
     *
     * @param t the invocation target exception.
     * @return the converted exception.
     * @since ant 1.7.1
     */
    public static ReflectException toReflectException(Exception t) {
        if (t instanceof InvocationTargetException) {
            Throwable t2 = ((InvocationTargetException) t)
                    .getTargetException();
            if (t2 instanceof ReflectException) {
                return (ReflectException) t2;
            }
            return new ReflectException(t2);
        } else {
            return new ReflectException(t);
        }
    }

    /**
     * A method to test if an object responds to a given
     * message (method call)
     *
     * @param o          the object
     * @param methodName the method to check for
     * @return true if the object has the method.
     * @throws ReflectException if there is a problem.
     */
    public static boolean respondsTo(Object o, String methodName)
            throws ReflectException {
        try {
            Method[] methods = o.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(methodName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception t) {
            throw toReflectException(t);
        }
    }
}