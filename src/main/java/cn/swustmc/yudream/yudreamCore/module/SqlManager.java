package cn.swustmc.yudream.yudreamCore.module;

import cn.swustmc.yudream.yudreamCore.api.db.Delete;
import cn.swustmc.yudream.yudreamCore.api.db.Insert;
import cn.swustmc.yudream.yudreamCore.api.db.Select;
import cn.swustmc.yudream.yudreamCore.api.db.Update;
import cn.swustmc.yudream.yudreamCore.common.utils.SqlUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class SqlManager {

    private static SqlManager instance;

    public static SqlManager getInstance() {
        if (instance == null) {
            instance = new SqlManager();
        }
        return instance;
    }

    private final SqlUtils sqlUtils = new SqlUtils();

    private final String driver = "com.mysql.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost:3306/database?useSSL=false";
    private final String username = "username";
    private final String password = "password";

    public <T> T getMapper(Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        Class<?>[] classes = new Class[]{clazz};
        InvocationHandler invocation = (proxy, method, args) -> {
            Annotation an = method.getAnnotations()[0];
            Class<? extends Annotation> annotationType = an.annotationType();
            Method valueMethod = annotationType.getMethod("value");
            String sql = (String) valueMethod.invoke(an);
            Object obj = args == null ? null : args[0];
            if (annotationType == Insert.class)
                this.update(sql, obj);
            else if (annotationType == Update.class)
                this.update(sql, obj);
            else if (annotationType == Delete.class)
                this.update(sql, obj);
            else if (annotationType == Select.class) {
                Class<?> returnType = method.getReturnType();
                if (returnType == List.class) {
                    Type type = method.getGenericReturnType();
                    ParameterizedType realReturnType = (ParameterizedType) type;
                    Type[] patterns = realReturnType.getActualTypeArguments();
                    Type patternType = patterns[0];
                    Class<?> resultType = (Class<?>) patternType;
                    return this.selectList(sql,obj,resultType);
                }else {
                    return this.selectOne(sql,obj,returnType);
                }
            }else {
                System.out.println("无法处理的注解: " + annotationType.getName());
            }
            return null;
        };
        return (T) Proxy.newProxyInstance(classLoader, classes, invocation);
    }

    public <T> T selectOne(String sql, Object obj, Class<?> resultType) {
        return (T) selectList(sql, obj, resultType).get(0);
    }

    public <T> List<T> selectList(String sql, Object obj, Class<?> resultType) {
        List<T> list = new ArrayList<>();
        try {
            Class.forName(driver);
            SQLAndKey sqlAndKey = sqlUtils.parseSQL(sql);
            Connection connection = DriverManager.getConnection(url, username, password);
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
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("查询错误: " + e.getMessage());
        }
        return list;
    }

    public void update(String sql, Object obj) {
        if (sql == null || sql.isEmpty()) return;
        SQLAndKey sqlAndKey = sqlUtils.parseSQL(sql);
        try {
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sqlAndKey.getSql());
            if (obj != null) {
                sqlUtils.handlerParameter(statement, obj, sqlAndKey.getList());
            }
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
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
