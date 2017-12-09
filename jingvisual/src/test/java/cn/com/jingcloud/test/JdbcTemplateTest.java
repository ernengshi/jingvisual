/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import cn.com.jingcloud.domain.entity.p.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * https://examples.javacodegeeks.com/enterprise-java/spring/jdbc/spring-jdbctemplate-example/
 * spring 的 JdbcTemplate 测试
 *
 * @author liyong
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcTemplateTest {

    @Before
    public void setUp() {
        // 准备，清空user表
//        userSerivce.deleteAllUsers();
//        jdbcTemplate1.update("DELETE  FROM  user ");
//        jdbcTemplate2.update("DELETE  FROM  message ");
    }

    @Autowired
    @Qualifier("primaryJdbcTemplate")
    protected JdbcTemplate jdbcTemplate1;
    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    protected JdbcTemplate jdbcTemplate2;

    @Autowired
    @Qualifier("primaryNamedParameterJdbcTemplate")
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void testJdbcTemplate() throws Exception {
        // 往第一个数据源中插入两条数据
        jdbcTemplate1.update("insert into user(id,name,age) values(?, ?, ?)", 1, "aaa", 20);
        jdbcTemplate1.update("insert into user(id,name,age) values(?, ?, ?)", 2, "bbb", 30);
        // 往第二个数据源中插入一条数据，若插入的是第一个数据源，则会主键冲突报错
        jdbcTemplate2.update("insert into message(id,name,content) values(?, ?, ?)", 1, "aaa", "bbbbbb");
        // 查一下第一个数据源中是否有两条数据，验证插入是否成功
        Assert.assertEquals("2", jdbcTemplate1.queryForObject("select count(1) from user", String.class));
        // 查一下第一个数据源中是否有两条数据，验证插入是否成功
        Assert.assertEquals("1", jdbcTemplate2.queryForObject("select count(1) from message", String.class));

//        jdbcTemplate1.queryForObject(sql, args, argTypes, rowMapper);
//        注：其中argTypes 可以使用java.sql.Types常量类中的常量值
    }

    /**
     *
     * 为啥spring 要开发这个 NamedParameterJdbcTemplate 他解决了在可变参数中的匹配不正确 在参数列表中 ？
     * 对应的索引号 如果出现了新增参数改变起来很不方便而且还容易处错误这就是它的由来 传参数方式不同
     *
     * jdbcTemplate
     *
     * Object[] args = new Object[] {"x", "y"}; String sql = "select * from foo
     * where a = ? and b = ?"; jdbcTemplate.query(sql, args,
     * resultSetExtractor);
     *
     *
     * namedParameterJdbcTemplate String sql = "select * from foo where a = :mya
     * and b = :myb"; Map<String, Object> argMap = new
     * HashMap<String, Object>(); argMap.put("mya", "x"); argMap.put("myb",
     * "y"); namedParameterJdbcTemplate.query(sql, argMap, resultSetExtractor);
     *
     *
     * map传参数 这个 jdbcTemplate.queryForObject(sql, requiredType)
     * 中的requiredType应该为基础类型，和String类型。 不能够返回来User,想要返回User就要使用RowMapper
     *
     * @throws Exception
     */
    @Test
    public void testNamedParameterJdbcTemplate() throws Exception {
        String sql = " select u.name from user u where u.id=:id ";
        Map<String, Object> argMap = new HashMap<String, Object>();
        argMap.put("id", 289L);
        String u = namedParameterJdbcTemplate.queryForObject(sql, argMap, String.class);
        System.out.println("################" + u);
    }

    /**
     * BeanPropertySqlParameterSource
     */
    @Test
    public void testNamedParameterJdbcTemplate_BeanPropertySqlParameterSource() throws Exception {
        String sql = "select * from user u where u.id=:id ";
        User u = new User();
        u.setId(289L);
        SqlParameterSource sps = new BeanPropertySqlParameterSource(u);
        User user = (User) namedParameterJdbcTemplate.
                queryForObject(sql, sps, new BeanPropertyRowMapper(User.class));
        System.out.println("################" + user.getName());
    }

    /**
     * queryForInt(..), queryForLong(..) or queryForObject(..)这都是返回来单一值得方法
     * queryForObject 不使用RowMapper的话，则返回来的数据Object就是基础类型，和String类型
     */
    @Test
    public void testNamedParameterJdbcTemplate_MapSqlParameterSource() throws Exception {
        User user = new User();
        user.setId(289L);
        String sql = "select * from user u where u.id=:id ";
        //这种是一种简单的链式编程内部返回自身  
        SqlParameterSource sps = new MapSqlParameterSource().addValue("id", user.getId());
        User u = (User) namedParameterJdbcTemplate.
                queryForObject(sql, sps, new BeanPropertyRowMapper(User.class));
        System.out.println("################" + u.getName());

    }

    public List<Long> listId(String sql, Object... args) {
        List<Long> idList = jdbcTemplate1.query(sql, idRowMapper, args);
        System.out.println(jdbcTemplate1.getFetchSize() + "**********" + jdbcTemplate1.getMaxRows());

        return idList;
    }

    public static RowMapper<Long> idRowMapper = new RowMapper<Long>() {
        @Override
        public Long mapRow(ResultSet rs, int i) throws SQLException {
            return rs.getLong(1);
        }
    };

    @Test
    public void testJdbcTemplateMaxRow() {

        String sql = " select t.id from t_log t where t.id > ? ";

        List<Long> ids = listId(sql, new Object[]{0});
        for (Long id : ids) {
            System.out.println(id);
        }
    }

    /**
     * namedParameterJdbcTemplate 返回 jdbctemplate
     */
    @Test
    public void testGetJdbcOperations() {
        namedParameterJdbcTemplate.getJdbcOperations();
//        jdbcTemplate1.
    }

    /**
     * jdbctemplate batch
     *
     * @param users
     * @return
     */
    public int[] batchUpdate(final List<User> users) {
        int[] updateCounts = jdbcTemplate1.batchUpdate(
                "update user set name = ? where id = ?",
                new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, users.get(i).getName());
                ps.setLong(2, users.get(i).getId().longValue());
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });
        return updateCounts;
    }

    public int[] batchUpdate2(final List<User> users) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(users.toArray());
        int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(
                "update user set name = :name where id = :id",
                batch);
        return updateCounts;
    }

    public int[] batchUpdate3(final List<User> users) {
        List<Object[]> batch = new ArrayList<Object[]>();
        for (User user : users) {
            Object[] values = new Object[]{
                user.getName(),
                user.getId()};
            batch.add(values);
        }
        int[] updateCounts = jdbcTemplate1.batchUpdate(
                "update user set name = ? where id = ?",
                batch);
        return updateCounts;
    }

    /**
     * 按照n批次来执行批量任务
     *
     * @param users
     * @return
     */
    public int[][] batchUpdate(final Collection<User> users) {
        Collection<Object[]> batch = new ArrayList<Object[]>();
        for (User user : users) {
            Object[] values = new Object[]{
                user.getName(),
                user.getId()};
            batch.add(values);
        }
        int[][] updateCounts = jdbcTemplate1.batchUpdate(
                "update t_actor set first_name = ?, last_name = ? where id = ?",
                users,
                100,
                new ParameterizedPreparedStatementSetter<User>() {
            @Override
            public void setValues(PreparedStatement ps, User argument) throws SQLException {
                ps.setString(1, argument.getName());
                ps.setLong(2, argument.getId());

            }
        });
        return updateCounts;
    }

//    Updating (INSERT/UPDATE/DELETE) with jdbcTemplate
    void update() {
        jdbcTemplate1.update(
                "insert into t_actor (first_name, last_name) values (?, ?)",
                "Leonor", "Watling");

        jdbcTemplate1.update(
                "update t_actor set = ? where id = ?",
                "Banjo", 5276L);

        jdbcTemplate1.update(
                "delete from actor where id = ?",
                Long.valueOf(1L));
    }

    //多数据源 分布式事务 管理
    //做此项测试使用@Transactional时候 需要注释掉PrimaryConfig.java and SecondaryConfig.java的@Configuration
    //因为@Transactional不说明情况下使用的是jpa PrimaryConfig里边的事务，测试时候数据会回滚，从而影响这里本来进行的jta Atomikos分布式事务
//    @Transactional
////    @Rollback(false)
//    @Test
//    public void testAtomikosTran() {
//        jdbcTemplate1.update("insert into user(name,age) values( ?, ?)", "aaa", 20);
//        if (1 > 0) {
//            throw new RuntimeException("测试Atomikos分布式事务");
//        }
//        jdbcTemplate2.update("insert into message(name,content) values( ?, ?)", "aaa", "bbbbbb");
//    }
}
