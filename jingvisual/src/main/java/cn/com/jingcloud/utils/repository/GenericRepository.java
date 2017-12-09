package cn.com.jingcloud.utils.repository;

import cn.com.jingcloud.utils.constant.RepositoryConstant;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 *
 * @author liyong
 */
public class GenericRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    IdGenerator idGenerator;
    private String database = "MySQL";
    ORMHelper orm = new ORMHelper();
    MySQLPageBuilder mySQLPageBuilder = new MySQLPageBuilder();
    OraclePageBuilder oraclePageBuilder = new OraclePageBuilder();

    private static final Logger LOG = LoggerFactory.getLogger(GenericRepository.class);
    

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

//    public long create(Object obj) {
//
//        final String INSERT_SQL = "insert into my_test (name) values(?)";
//        final String name = "Rob";
//
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbcTemplate.update(
//                new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
//                PreparedStatement ps
//                        = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
//                ps.setString(1, name);
//                return ps;
//            }
//        },
//                keyHolder);
//        return keyHolder.getKey().longValue();
//    }
    private long getObjectId(Object obj) {
        long id = 0;
        try {
            id = orm.getObjectId(obj);
        } catch (Exception e) {
            throw new RuntimeException("get id of object error", e);
        }
        return id;
    }

    public long create(Object obj) {
        String table = orm.getTableName(obj.getClass());//表名
        String key = orm.getKey(obj.getClass());//id
        long id = getObjectId(obj);
        if (id == 0) {
            id = idGenerator.generate(table, key);
        }

        String sql = orm.getCreateSql(obj.getClass(), id);
        LOG.debug(sql);
        namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(obj));

        return id;
    }

    public int delete(Object obj) {
        String sql = orm.getDeleteSql(obj.getClass());
        LOG.debug(sql);
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(obj));
    }

    /**
     * <1>表名字必须是对象的小写
     *
     * <2>主键必须叫id
     *
     * <3>对象名字如果是驼峰，则对应的就要在 驼峰前增加_
     *
     * CloudClassRoom cloud_class_room
     *
     * @param <T>
     * @param id
     * @param type
     */
    public <T> void delete(long id, Class<T> type) {
        String camel = type.getSimpleName();
        String sql = "delete from " + UnderlineCamelUtil.camel2Underline(camel) + " where  id  = ?";
        LOG.debug(sql);
        jdbcTemplate.update(sql, id);
    }
//    public <T> void delete(long id, Class<T> type) {
//        String camel = type.getSimpleName();
//        String sql = "delete from " + UnderlineCamelUtil.camel2Underline(camel) + " where  id  = :id";
//        Map<String, Long> paramMap = Collections.singletonMap("id", id);
//        namedParameterJdbcTemplate.update(sql, paramMap);
//    }

    public void update(Object obj) {
        String sql = orm.getUpdateSql(obj.getClass());
        LOG.debug(sql);
        namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(obj));

    }

    /**
     * 表名字必须是Object的小写 主键必须叫id
     *
     * @param <T>
     * @param id
     * @param type
     * @return
     */
    public <T> T find(long id, Class<T> type) {
        BeanPropertyRowMapper<T> rm = new BeanPropertyRowMapper<T>(type);
        rm.setPrimitivesDefaultedForNullValue(true);
        String camel = type.getSimpleName();
        String sql = "select * from " + UnderlineCamelUtil.camel2Underline(camel) + " where id = ?";

        T t = null;
        try {
            t = jdbcTemplate.queryForObject(sql, rm, id);
        } catch (EmptyResultDataAccessException e) {
        }

        return t;
    }
//    public <T> T find(long id, Class<T> type) {
//        BeanPropertyRowMapper<T> rm = new BeanPropertyRowMapper<T>(type);
//        rm.setPrimitivesDefaultedForNullValue(true);
//        String camel = type.getSimpleName();
//        String sql = "select * from " + Underline2Camel.camel2Underline(camel) + " where id = :id";
//
//        T t = null;
//        try {
//            Map<String, Long> paramMap = Collections.singletonMap("id", id);
//
//            t = namedParameterJdbcTemplate.queryForObject(sql, paramMap, rm);
//        } catch (EmptyResultDataAccessException e) {
//        }
//
//        return t;
//    }

    /**
     * 查询任意一个结果对象
     *
     * @param <T>
     * @param type
     * @param sql
     * @param args
     * @return
     */
    public <T> T findFirst(Class<T> type, String sql, Object... args) {
        BeanPropertyRowMapper<T> rm = new BeanPropertyRowMapper<T>(type);
        rm.setPrimitivesDefaultedForNullValue(true);

        if (database.equalsIgnoreCase("MySQL")) {
            String _sql = sql + " limit 1";
            try {
                LOG.debug(_sql);
                return jdbcTemplate.queryForObject(_sql, rm, args);
            } catch (EmptyResultDataAccessException ex) {
                return null;
            }
        }
        if (database.equalsIgnoreCase("Oracle")) {
            String _sql
                    = "select * from (select A.*, rownum as r__n from ( "
                    + sql
                    + ") A) B where B.r__n = 1";
            try {
                return jdbcTemplate.queryForObject(_sql, rm, args);
            } catch (EmptyResultDataAccessException ex) {
                return null;
            }
        }
        throw new IllegalStateException("Datebase not supported !" + database);
    }

    /**
     * 查询任意一个结果对象
     *
     * @param <T>
     * @param type
     * @param sql
     * @param args
     * @return
     */
    public <T> T findFirst(Class<T> type, String sql, Map<String, Object> paramMap) {
        BeanPropertyRowMapper<T> rm = new BeanPropertyRowMapper<T>(type);
        rm.setPrimitivesDefaultedForNullValue(true);

        if (database.equalsIgnoreCase("MySQL")) {
            String _sql = sql + " limit 1";
            try {
                LOG.debug(_sql);
                return namedParameterJdbcTemplate.queryForObject(_sql, paramMap, rm);
            } catch (EmptyResultDataAccessException ex) {
                return null;
            }
        }
        if (database.equalsIgnoreCase("Oracle")) {
            String _sql
                    = "select * from (select A.*, rownum as r__n from ( "
                    + sql
                    + ") A) B where B.r__n = 1";
            try {
                return namedParameterJdbcTemplate.queryForObject(_sql, paramMap, rm);
            } catch (EmptyResultDataAccessException ex) {
                return null;
            }
        }
        throw new IllegalStateException("Datebase not supported !" + database);
    }

    public <T> List<T> page(Class<T> type, String sql, int pageNo, int pageSize, Map<String, Object> paramMap) {
        SqlPageBuilder pageBuilder = mySQLPageBuilder;
        if (database.equalsIgnoreCase("Oracle")) {
            pageBuilder = oraclePageBuilder;
        }

        BeanPropertyRowMapper<T> rm = new BeanPropertyRowMapper<T>(type);
        rm.setPrimitivesDefaultedForNullValue(true);
        return namedParameterJdbcTemplate.query(
                pageBuilder.buildPageSql(sql, pageNo, pageSize), paramMap,
                rm);
    }

    public <T> List<T> page(Class<T> type, String sql, int pageNo, int pageSize, Object... args) {
        SqlPageBuilder pageBuilder = mySQLPageBuilder;
        if (database.equalsIgnoreCase("Oracle")) {
            pageBuilder = oraclePageBuilder;
        }

        BeanPropertyRowMapper<T> rm = new BeanPropertyRowMapper<T>(type);
        rm.setPrimitivesDefaultedForNullValue(true);
        return jdbcTemplate.query(
                pageBuilder.buildPageSql(sql, pageNo, pageSize),
                rm, args);
    }

    public int count(String sql, Map<String, Object> paramMap) {
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public int count(String sql, Object... args) {
        return jdbcTemplate.queryForObject(sql, args, Integer.class);
    }

    public <T> List<T> list(Class<T> type, String sql, Object... args) {
//        if (orm.needCache(type)) {
//            List<Long> idList = simpleJdbcTemplate.query(sql, idRowMapper, args);
//            List<T> result = new ArrayList<T>();
//            for (Long id : idList) {
//                result.add(find(id, type));
//            }
//            return result;
//        } else {
        BeanPropertyRowMapper<T> rm = new BeanPropertyRowMapper<T>(type);
        rm.setPrimitivesDefaultedForNullValue(true);
        return jdbcTemplate.query(sql, rm, args);
//        }
    }

    public <T> List<T> list(Class<T> type, String sql, int limit, Object... args) {
//        if (orm.needCache(type)) {
//            List<Long> idList = simpleJdbcTemplate.query(sql, idRowMapper, args);
//            List<T> result = new ArrayList<T>();
//            for (Long id : idList) {
//                result.add(find(id, type));
//            }
//            return result;
//        } else {
        BeanPropertyRowMapper<T> rm = new BeanPropertyRowMapper<T>(type);
        rm.setPrimitivesDefaultedForNullValue(true);
        return jdbcTemplate.query(this.getLimitSql(sql, limit), rm, args);
//        }
    }

    public <T> List<T> list(Class<T> type, String sql, Map<String, Object> paramMap) {
//        if (orm.needCache(type)) {
//            List<Long> idList = namedParameterJdbcTemplate.query(sql, paramMap, idRowMapper);
//            List<T> result = new ArrayList<T>();
//            for (Long id : idList) {
//                result.add(find(id, type));
//            }
//            return result;
//        } else {
        BeanPropertyRowMapper<T> rm = new BeanPropertyRowMapper<T>(type);
        rm.setPrimitivesDefaultedForNullValue(true);
        return namedParameterJdbcTemplate.query(sql, paramMap, rm);
//        }
    }

    public <T> List<T> list(Class<T> type, String sql, Map<String, Object> paramMap, int limit) {
//        if (orm.needCache(type)) {
//            List<Long> idList = namedParameterJdbcTemplate.query(sql, paramMap, idRowMapper);
//            List<T> result = new ArrayList<T>();
//            for (Long id : idList) {
//                result.add(find(id, type));
//            }
//            return result;
//        } else {
        BeanPropertyRowMapper<T> rm = new BeanPropertyRowMapper<T>(type);
        rm.setPrimitivesDefaultedForNullValue(true);
        return namedParameterJdbcTemplate.query(this.getLimitSql(sql, limit), paramMap, rm);
//        }
    }

    public List<Long> listId(String sql, Map<String, Object> paramMap) {
        List<Long> idList = namedParameterJdbcTemplate.query(sql, paramMap, idRowMapper);
        return idList;
    }

    public List<Long> listId(String sql, Map<String, Object> paramMap, int limit) {
        List<Long> idList = namedParameterJdbcTemplate.query(this.getLimitSql(sql, limit), paramMap, idRowMapper);
        return idList;
    }

    public List<Long> listId(String sql, Object... args) {
        List<Long> idList = jdbcTemplate.query(sql, idRowMapper, args);
        return idList;
    }

    public List<Long> listId(String sql, int limit, Object... args) {
        List<Long> idList = jdbcTemplate.query(this.getLimitSql(sql, limit), idRowMapper, args);
        return idList;
    }

    public Map<?, ?> queryForMap(String sql, Object[] paramValues) {
//		LOG.debug(StringUtil.concat("[queryForMap] 1. sql:", sql));

        Map<?, ?> map = jdbcTemplate.queryForMap(sql, paramValues);

        return map;
    }

    public Map<?, ?> queryForMap(String sql, int limit, Object[] paramValues) {
//		LOG.debug(StringUtil.concat("[queryForMap] 1. sql:", sql));

        Map<?, ?> map = jdbcTemplate.queryForMap(this.getLimitSql(sql, limit), paramValues);

        return map;
    }

    public Map<?, ?> queryForMap(String sql, Map<String, Object> paramMap) {
//		LOG.debug(StringUtil.concat("[queryForMap] 1. sql:", sql));

        Map<?, ?> map = namedParameterJdbcTemplate.queryForMap(sql, paramMap);

        return map;
    }

    public Map<?, ?> queryForMap(String sql, Map<String, Object> paramMap, int limit) {
//		LOG.debug(StringUtil.concat("[queryForMap] 1. sql:", sql));

        Map<?, ?> map = namedParameterJdbcTemplate.queryForMap(this.getLimitSql(sql, limit), paramMap);

        return map;
    }

    public static final RowMapper<Long> idRowMapper = new RowMapper<Long>() {
        @Override
        public Long mapRow(ResultSet rs, int i) throws SQLException {
            return rs.getLong(1);
        }
    };

    /**
     * create/update/delete
     *
     * @param sql
     * @param obj
     */
    public void tryUpdate(String sql, Object obj) {

        namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(obj));

    }

    public int tryUpdate(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    public int tryUpdate(String sql, Map<String, Object> paramMap) {
        //update xx set = :xx where id = :xx
        return namedParameterJdbcTemplate.update(sql, paramMap);
    }

    private String getLimitSql(String sql, int limit) {
        if (limit > 0) {
            if (limit > RepositoryConstant.MAX_RECORDS) {
                limit = RepositoryConstant.MAX_RECORDS;
            }

            SqlPageBuilder pageBuilder = mySQLPageBuilder;
            if (database.equalsIgnoreCase("Oracle")) {
                pageBuilder = oraclePageBuilder;
            }
            sql = pageBuilder.buildPageSql(sql, 1, limit);
        }
        return sql;
    }

    public long getDBTimestamp() {
        if (database.equalsIgnoreCase("Oracle")) {
            String sql = " select sysdate from dual ";
            List<java.sql.Timestamp> list = jdbcTemplate.query(sql, tsRowMapper);
            return list.get(0).getTime();
        } else {
            return 0;
        }
    }
    public static final RowMapper<java.sql.Timestamp> tsRowMapper = new RowMapper<java.sql.Timestamp>() {
        @Override
        public java.sql.Timestamp mapRow(ResultSet rs, int i) throws SQLException {
            return rs.getTimestamp(1);//数据库时间
        }
    };
}
