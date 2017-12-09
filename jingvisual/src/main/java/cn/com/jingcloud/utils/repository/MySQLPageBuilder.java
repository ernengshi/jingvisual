/*
 * www.pconline.com.cn - 1997-2010.
 */

package cn.com.jingcloud.utils.repository;

/**
 *
 * @author xhchen
 */
public class MySQLPageBuilder implements SqlPageBuilder {
	@Override
	public String buildPageSql(String sql, int pageNo, int pageSize) {
		return sql + " LIMIT " + (pageNo - 1) * pageSize + "," + pageSize;
	}
}
