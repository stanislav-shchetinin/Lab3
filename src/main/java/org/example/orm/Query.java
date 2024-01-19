package org.example.orm;

import org.example.orm.annotations.ColumnsName;
import org.example.orm.annotations.LookInside;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Query<T> {

    private static final Map<Class<?>, Method> classGetMethodMap;
    private static final Map<Class<?>, Method> classSetMethodMap;

    static {
        classGetMethodMap = new HashMap<>();
        try {
            classGetMethodMap.put(Double.class, PreparedStatement.class.getMethod("setDouble", int.class, double.class));
            classGetMethodMap.put(Integer.class, PreparedStatement.class.getMethod("setInt", int.class, int.class));
            classGetMethodMap.put(Boolean.class, PreparedStatement.class.getMethod("setBoolean", int.class, boolean.class));
            classGetMethodMap.put(Date.class, PreparedStatement.class.getMethod("setDate", int.class, Date.class));
            classGetMethodMap.put(Long.class, PreparedStatement.class.getMethod("setLong", int.class, long.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    static {
        classSetMethodMap = new HashMap<>();
        try {
            classSetMethodMap.put(Double.class, ResultSet.class.getMethod("getDouble", String.class));
            classSetMethodMap.put(Integer.class, ResultSet.class.getMethod("getInt", String.class));
            classSetMethodMap.put(Boolean.class, ResultSet.class.getMethod("getBoolean", String.class));
            classSetMethodMap.put(Date.class, ResultSet.class.getMethod("getDate", String.class));
            classSetMethodMap.put(Long.class, ResultSet.class.getMethod("getLong", String.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void insert(Connection connection, String tableName, T obj) throws
            InvocationTargetException, NoSuchMethodException, IllegalAccessException, SQLException {

        Map<String, Object> map = mapColumnNameAndObject(obj);

        try (PreparedStatement preparedStatement = connection.prepareStatement(getQueryString(tableName, map))) {
            int num = 0;
            for (Object objectFromField : map.values()){
                ++num;
                classGetMethodMap.get(objectFromField.getClass()).invoke(preparedStatement, num, objectFromField);
            }
            preparedStatement.executeUpdate();
        }

    }

    public List<T> select(Connection connection, String tableName, Class<T> clazz) throws
            InvocationTargetException, NoSuchMethodException, IllegalAccessException, SQLException, InstantiationException{
        List<T> res = new ArrayList<>();
        String query = String.format("SELECT * FROM %s", tableName);

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                T obj = clazz.getConstructor().newInstance();
                fillObject(resultSet, obj);
                res.add(obj);
            }
        }
        return res;
    }

    public static void fillObject(ResultSet resultSet, Object obj) throws
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (Field field : obj.getClass().getDeclaredFields()){
            if (field.isAnnotationPresent(LookInside.class)) {
                fillObject(resultSet, getGetterFromName(obj, field.getName()).invoke(obj));
            } else if (field.isAnnotationPresent(ColumnsName.class)) {
                ColumnsName annotation = field.getAnnotation(ColumnsName.class);
                Method setter = getSetterFromField(obj, field);
                Object arg = classSetMethodMap.get(field.getType()).invoke(resultSet, annotation.value());
                setter.invoke(obj, arg);
            }
        }
    }

    public static String getQueryString(String tableName, Map<String, Object> map) {
        StringBuilder res = new StringBuilder(String.format("INSERT INTO %s(", tableName));
        int count = 0;
        for (String columnName : map.keySet()){
            ++count;
            res.append(columnName);
            if (count != map.keySet().size()){
                res.append(",");
            }
        }
        res.append(")").append(" VALUES(");
        while (count > 0){
            --count;
            res.append("?");
            if (count != 0){
                res.append(",");
            }
        }
        res.append(")");
        return res.toString();
    }

    public static Map<String, Object> mapColumnNameAndObject(Object obj) throws
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<String, Object> res = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()){
            Method getter = getGetterFromName(obj, field.getName());
            if (field.isAnnotationPresent(LookInside.class)){
                Map<String, Object> resInside = mapColumnNameAndObject(getter.invoke(obj));
                res.putAll(resInside);
            } else if (field.isAnnotationPresent(ColumnsName.class)){
                ColumnsName annotation = field.getAnnotation(ColumnsName.class);
                res.put(annotation.value(), getter.invoke(obj));
            }
        }
        return res;
    }

    public static Method getGetterFromName(Object obj, String nameVar) throws NoSuchMethodException {
        String getterName = getGetterName(nameVar);
        return obj.getClass().getMethod(getterName);
    }

    public static Method getSetterFromField(Object obj, Field field) throws NoSuchMethodException {
        String setterName = getSetterName(field.getName());
        return obj.getClass().getMethod(setterName, field.getType());
    }

    public static String getGetterName(String nameVar){
        return "get" + firstLetterUpper(nameVar);
    }

    public static String getSetterName(String nameVar){
        return "set" + firstLetterUpper(nameVar);
    }

    public static String firstLetterUpper(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
