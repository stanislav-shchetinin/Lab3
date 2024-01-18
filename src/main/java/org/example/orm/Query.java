package org.example.orm;

import org.example.orm.annotations.ColumnsName;
import org.example.orm.annotations.LookInside;

import javax.swing.text.html.parser.Entity;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Query {

    private static final Map<Class<?>, Method> classMethodMap;

    static {
        classMethodMap = new HashMap<>();
        try {
            classMethodMap.put(Double.class, PreparedStatement.class.getMethod("setDouble", int.class, double.class));
            classMethodMap.put(Integer.class, PreparedStatement.class.getMethod("setInt", int.class, int.class));
            classMethodMap.put(Boolean.class, PreparedStatement.class.getMethod("setBoolean", int.class, boolean.class));
            classMethodMap.put(Date.class, PreparedStatement.class.getMethod("setDate", int.class, Date.class));
            classMethodMap.put(Long.class, PreparedStatement.class.getMethod("setLong", int.class, long.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public static void insert(Connection connection, String tableName, Object obj) throws
            InvocationTargetException, NoSuchMethodException, IllegalAccessException, SQLException {

        Map<String, Object> map = mapColumnNameAndGetter(obj);

        try (PreparedStatement pstmt = connection.prepareStatement(getQueryString(tableName, map))) {
            int num = 0;
            for (Object objectFromField : map.values()){
                ++num;;
                classMethodMap.get(objectFromField.getClass()).invoke(pstmt, num, objectFromField);
            }
            pstmt.executeUpdate();
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

    public static Map<String, Object> mapColumnNameAndGetter(Object obj) throws
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<String, Object> res = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()){
            Method getter = getGetter(obj, field.getName());
            if (field.isAnnotationPresent(LookInside.class)){
                Map<String, Object> resInside = mapColumnNameAndGetter(getter.invoke(obj));
                res.putAll(resInside);
            } else if (field.isAnnotationPresent(ColumnsName.class)){
                ColumnsName annotation = field.getAnnotation(ColumnsName.class);
                res.put(annotation.value(), getter.invoke(obj));
            }
        }
        return res;
    }

    public static Method getGetter(Object obj, String nameVar) throws NoSuchMethodException {
        String getterName = getGetterName(nameVar);
        return obj.getClass().getMethod(getterName);
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
