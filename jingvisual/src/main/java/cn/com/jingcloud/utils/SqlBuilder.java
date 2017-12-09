/*
 * Copyright 1997-2012
 *
 * http://www.pconline.com.cn
 *
 */
package cn.com.jingcloud.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chenxiaohu
 */
public class SqlBuilder {

    protected StringBuilder sqlBuf = new StringBuilder();
    protected List values = new ArrayList();

    public SqlBuilder appendSql(String sql) {
        sqlBuf.append(sql);
        return this;
    }

    public SqlBuilder appendValue(Object value) {
        sqlBuf.append('?');
        values.add(value);
        return this;
    }

    public SqlBuilder appendValues(Object[] values) {
        sqlBuf.append('(');
        for (int i = 0, c = values.length; i < c; ++i) {
            sqlBuf.append('?').append(',');
            this.values.add(values[i]);
        }
        int last = sqlBuf.length() - 1;
        if (last > 0 && sqlBuf.charAt(last) == ',') {
            sqlBuf.setCharAt(last, ')');
        }

        return this;
    }

    public String getSql() {
        return sqlBuf.toString();
    }

    public Object[] getValues() {
        return values.toArray();
    }

    public static void main(String[] args) {
        SqlBuilder buf = new SqlBuilder();
        buf.appendSql("select * from gl_bar where bar_id >");
        buf.appendValue(1);
        buf.appendSql(" order by bar_id");
        System.out.println(buf.getSql());
        System.out.println(buf.getValues()[0]);
    }
}