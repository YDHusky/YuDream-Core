package cn.swustmc.yudream.yudreamCore.common.utils;

import cn.swustmc.yudream.yudreamCore.module.SqlManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class SqlUtils {

    public void handlerParameter(PreparedStatement statement, Object obj, List<String> KeyList) throws SQLException {
        Class<?> clazz = obj.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            statement.setInt(1, (Integer) obj);
        } else if (clazz == float.class || clazz == Float.class) {
            statement.setFloat(1, (Float) obj);
        } else if (clazz == double.class || clazz == Double.class) {
            statement.setDouble(1, (Double) obj);
        } else if (clazz == String.class) {
            statement.setString(1, (String) obj);
        } else {
            if (obj instanceof Map) {
                this.setMap(statement, obj, KeyList);
            } else {
                this.setObject(statement, obj, KeyList);
            }
        }
    }

    private void setMap(PreparedStatement statement, Object obj, List<String> KeyList) throws SQLException {
        Map<?,?> map = (Map<?,?>) obj;
        for (int i = 0; i < KeyList.size(); i++) {
            String key = KeyList.get(i);
            Object value = map.get(key);
            statement.setObject(i + 1, value);
        }
    }

    private void setObject(PreparedStatement statement, Object obj, List<String> KeyList) throws SQLException {
        try {
            Class<?> clazz = obj.getClass();
            for (int i = 0; i < KeyList.size(); i++) {
                String key = KeyList.get(i);
                Field field = clazz.getDeclaredField(key);
                field.setAccessible(true);
                Object value = field.get(obj);
                statement.setObject(i + 1,value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public SqlManager.SQLAndKey parseSQL(String sql) {
        StringBuilder newSQL = new StringBuilder();
        List<String> list = new ArrayList<>();
        while (true) {
            int left = sql.indexOf("#{");
            int right = sql.indexOf("}");
            if (left != -1 && right != -1 && left < right) {
                newSQL.append(sql, 0, left);
                newSQL.append("?");
                list.add(sql.substring(left + 2, right));
            } else {
                newSQL.append(sql);
                break;
            }
            sql = sql.substring(right + 1);
        }
        return new SqlManager.SQLAndKey(newSQL, list);
    }

    public Object handlerResult(ResultSet rs, Class<?> resultType) throws SQLException {
        Object result = null;
        if (resultType == int.class || resultType == Integer.class) {
            result = rs.getInt(1);
        } else if (resultType == float.class || resultType == Float.class) {
            result = rs.getFloat(1);
        } else if (resultType == double.class || resultType == Double.class) {
            result = rs.getDouble(1);
        } else if (resultType == String.class) {
            result = rs.getString(1);
        } else {
            if (resultType == Map.class) {
                result = getMap(rs);
            } else {
                result = getObject(rs, resultType);
            }
        }
        return result;
    }

    private Map<?,?> getMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i);
            Object columnValue = rs.getObject(columnName);
            map.put(columnName, columnValue);
        }
        return map;
    }

    private Object getObject(ResultSet rs, Class<?> resultType) throws SQLException {
        Object result = null;
        try {
            result = resultType.getDeclaredConstructor().newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Field field = resultType.getDeclaredField(columnName);
                field.setAccessible(true);
                Object columnValue = rs.getObject(columnName);
                field.set(result, columnValue);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
