package cn.swustmc.yudream.yudreamCore.module;

import cn.swustmc.yudream.yudreamCore.api.db.Delete;
import cn.swustmc.yudream.yudreamCore.api.db.Insert;
import cn.swustmc.yudream.yudreamCore.api.db.Select;
import cn.swustmc.yudream.yudreamCore.api.db.Update;
import cn.swustmc.yudream.yudreamCore.common.utils.SqlUtils;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlManager {

    private static SqlManager instance;

    public static SqlManager getInstance() {
        if (instance == null) {
            instance = new SqlManager();
        }
        return instance;
    }

    private final Map<Class<?>, Object> proxyMap = new HashMap<>();

    private final SqlUtils sqlUtils = new SqlUtils();

    public <T> T getMapper(Class<?> clazz, DataSource dataSource){
        if (proxyMap.containsKey(clazz)) {
            return (T) proxyMap.get(clazz);
        }
        return (T) proxyMap.put(clazz, getProxy(clazz, dataSource));
    }

    private <T> T getProxy(Class<?> clazz, DataSource dataSource) {
        ClassLoader classLoader = clazz.getClassLoader();
        Class<?>[] classes = new Class[]{clazz};
        InvocationHandler invocation = (proxy, method, args) -> {
            Annotation an = method.getAnnotations()[0];
            Class<? extends Annotation> annotationType = an.annotationType();
            Method valueMethod = annotationType.getMethod("value");
            String sql = (String) valueMethod.invoke(an);
            Object obj = args == null ? null : args[0];
            if (annotationType == Insert.class)
                this.update(dataSource, sql, obj);
            else if (annotationType == Update.class)
                this.update(dataSource, sql, obj);
            else if (annotationType == Delete.class)
                this.update(dataSource, sql, obj);
            else if (annotationType == Select.class) {
                Class<?> returnType = method.getReturnType();
                if (returnType == List.class) {
                    Type type = method.getGenericReturnType();
                    ParameterizedType realReturnType = (ParameterizedType) type;
                    Type[] patterns = realReturnType.getActualTypeArguments();
                    Type patternType = patterns[0];
                    Class<?> resultType = (Class<?>) patternType;
                    return this.selectList(dataSource, sql,obj,resultType);
                }else {
                    return this.selectOne(dataSource, sql,obj,returnType);
                }
            }else {
                System.out.println("无法处理的注解: " + annotationType.getName());
            }
            return null;
        };
        return (T) Proxy.newProxyInstance(classLoader, classes, invocation);
    }

    private <T> T selectOne(DataSource dataSource, String sql, Object obj, Class<?> resultType) {
        return (T) selectList(dataSource, sql, obj, resultType).get(0);
    }

    private <T> List<T> selectList(DataSource dataSource, String sql, Object obj, Class<?> resultType) {
        List<T> list = new ArrayList<>();
        SQLAndKey sqlAndKey = sqlUtils.parseSQL(sql);
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlAndKey.getSql());
            if (obj != null) {
                sqlUtils.handlerParameter(statement, obj, sqlAndKey.getList());
            }
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                T result = (T) sqlUtils.handlerResult(rs, resultType);
                list.add(result);
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("查询错误: " + e.getMessage());
        }
        return list;
    }

    private void update(DataSource dataSource, String sql, Object obj) {
        if (sql == null || sql.isEmpty()) return;
        SQLAndKey sqlAndKey = sqlUtils.parseSQL(sql);
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlAndKey.getSql());
            if (obj != null) {
                sqlUtils.handlerParameter(statement, obj, sqlAndKey.getList());
            }
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("更新错误: " + e.getMessage());
        }
    }

    public static class SQLAndKey {

        private final StringBuilder sql;
        private final List<String> list;

        public SQLAndKey(StringBuilder sql, List<String> list) {
            this.sql = sql;
            this.list = list;
        }

        public String getSql() {
            return sql.toString();
        }

        public List<String> getList() {
            return list;
        }
    }
}
