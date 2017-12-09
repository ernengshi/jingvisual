package cn.com.jingcloud.utils.repository;

import cn.com.jingcloud.domain.entity.generic.User;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author liyong
 */
public class ORMHelper {

    ConcurrentHashMap<Class, List<String>> metaMap = new ConcurrentHashMap<Class, List<String>>();//表名 字段
    ConcurrentHashMap<Class, Method> methodMap = new ConcurrentHashMap<Class, Method>();//对象  对象方法

    public ORMHelper() {
    }

    public long getObjectId(Object obj) throws Exception {
        Method method = methodMap.get(obj.getClass());

        if (method == null) {
            String methodName = "getId";
            method = obj.getClass().getMethod(methodName);
            methodMap.put(obj.getClass(), method);
        }

        return ((Long) method.invoke(obj));
    }

    
    public String getCreateSql(Class type, long id) {
        String key = getKey(type);
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(getTableName(type)).append(" (");
        List<String> fieldList = getFieldList(type);
        for (String field : fieldList) {
            sb.append(field).append(",");
        }
        sb.setCharAt(sb.length() - 1, ')');
        sb.append(" values (");
        for (String field : fieldList) {
            if (key.equalsIgnoreCase(field)) {
                sb.append(id).append(',');
            } else {
                sb.append(':').append(field).append(',');
            }
        }
        sb.setCharAt(sb.length() - 1, ')');//替换最后一个为 括号
        return sb.toString();
    }

    public String getUpdateSql(Class type) {
        String key = getKey(type);
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(getTableName(type));
        sb.append(" set ");
        List<String> fieldList = getFieldList(type);
        for (String field : fieldList) {
            if (!key.equalsIgnoreCase(field)) {
                sb.append(field).append("=:").append(field).append(',');
            } else {
                key = field;
            }
        }
        sb.setCharAt(sb.length() - 1, ' ');
        sb.append("where ").append(key).append("=:").append(key);

        return sb.toString();
    }

    public String getDeleteSql(Class type) {
        String key = getKey(type);
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ").append(getTableName(type));
        List<String> fieldList = getFieldList(type);
        for (String field : fieldList) {
            if (key.equalsIgnoreCase(field)) {
                key = field;
                break;
            }
        }
        sb.append(" where ").append(key).append("=:").append(key);

        return sb.toString();
    }

    public String getTableName(Class type) {
        return UnderlineCamelUtil.camel2Underline(type.getSimpleName());
    }

    public String getKey(Class type) {
        return "id";
    }

    List<String> getFieldList(Class type) {
        List<String> result = metaMap.get(type);

        if (result == null) {
            result = new ArrayList<String>();
            Field[] fields = type.getDeclaredFields();//获取对象对应的字段
            for (Field field : fields) {
                String fieldName = field.getName();
                if (!Modifier.isTransient(field.getModifiers()) && !"serialVersionUID".equals(fieldName)) {
                    result.add(fieldName);
                }
            }
            metaMap.put(type, result);
        }

        return result;
    }

    
    boolean needCache(Class type) {
        try {
            return ((Boolean) (type.getMethod("needCache").invoke(Class.forName(type.getName()).newInstance()))).booleanValue();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(new ORMHelper().getUpdateSql(User.class));
        System.out.println(new ORMHelper().getCreateSql(User.class,1));
        System.out.println(new ORMHelper().getDeleteSql(User.class));
    }
}
