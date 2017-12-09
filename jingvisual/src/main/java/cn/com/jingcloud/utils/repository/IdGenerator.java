package cn.com.jingcloud.utils.repository;

public interface IdGenerator {
	long generate(String tableName, String columnName);
}
