/*
 * www.pconline.com.cn - 1997-2010.
 */

package cn.com.jingcloud.utils.repository;

/**
 *
 * @author xhchen
 */
public interface SqlPageBuilder {
	public String buildPageSql(String sql, int pageNo, int pageSize);
}
