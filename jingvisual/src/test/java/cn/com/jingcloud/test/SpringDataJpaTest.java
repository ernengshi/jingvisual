/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import cn.com.jingcloud.domain.dao.p.RedisUserRepository;
import cn.com.jingcloud.domain.dao.s.MessageRepository;
import cn.com.jingcloud.domain.entity.p.User;
import cn.com.jingcloud.domain.entity.s.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import cn.com.jingcloud.domain.dao.p.UserRepository;
import cn.com.jingcloud.domain.entity.p.RedisUser;
import cn.com.jingcloud.domain.entity.p.RedisUserVO;
import cn.com.jingcloud.utils.page.UserQuery;
import cn.com.jingcloud.vo.p.UserVO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Spring-data-jpa的能力远不止本文提到的这些，由于本文主要以整合介绍为主， 对于Spring-data-jpa的使用只是介绍了常见的使用方式。
 * 诸如@Modifying操作、分页排序、原生SQL支持以及与Spring MVC的结合使用等等内容就不在本文中详细展开， 这里先挖个坑，后续再补文章填坑，
 * 如您对这些感兴趣可以关注我博客或简书，同样欢迎大家留言交流想法。
 *
 * @author liyong
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataJpaTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RedisUserRepository redisUserRepository;

    @Before
    public void setUp() {
//        userRepository.deleteAll();
//        messageRepository.deleteAll();
    }

    @Test
    @Transactional(value = "transactionManagerPrimary")
    public void testAddSingle() throws Exception {
        //中文
        userRepository.save(new User("张三", 10));

    }

    /**
     * 事务的测试
     *
     * @throws Exception
     */
    @Test
    @Transactional(value = "transactionManagerPrimary")
    public void test() throws Exception {
        // 创建10条记录
        userRepository.save(new User("AAA", 10));
        userRepository.save(new User("BBB", 20));
        userRepository.save(new User("CCC", 30));
        userRepository.save(new User("DDD", 40));
        userRepository.save(new User("EEE", 50));
        userRepository.save(new User("FFF", 60));
        userRepository.save(new User("GGG", 70));
        userRepository.save(new User("HHHH", 80));
        userRepository.save(new User("III", 90));
        userRepository.save(new User("JJJ", 100));
        // 测试findAll, 查询所有记录
        Assert.assertEquals(10, userRepository.findAll().size());
        // 测试findByName, 查询姓名为FFF的User
        Assert.assertEquals(60, userRepository.findByName("FFF").getAge().longValue());
        // 测试findUser, 查询姓名为FFF的User
        Assert.assertEquals(60, userRepository.findUser("FFF").get(0).getAge().longValue());
        // 测试findByNameAndAge, 查询姓名为FFF并且年龄为60的User
        Assert.assertEquals("FFF", userRepository.findByNameAndAge("FFF", 60).getName());
        // 测试删除姓名为AAA的User
        userRepository.delete(userRepository.findByName("AAA"));
        // 测试findAll, 查询所有记录, 验证上面的删除是否成功
        Assert.assertEquals(9, userRepository.findAll().size());
    }

    /////////////////////////////////////////////////////////////////////////
    ////////////////////////下边是多数据源时候的情况/////////////////////////////
    /////////////////////////////////////////////////////////////////////////
//    @Autowired
//    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void findUser() throws Exception {
        userRepository.findUser("aaa");

        System.out.println("################");

    }

    @Test
    public void testMulDatasource() throws Exception {
        userRepository.save(new User("aaa", 10));
        userRepository.save(new User("bbb", 20));
        userRepository.save(new User("ccc", 30));
        userRepository.save(new User("ddd", 40));
        userRepository.save(new User("eee", 50));
        Assert.assertEquals(5, userRepository.findAll().size());
        messageRepository.save(new Message("o1", "aaaaaaaaaa"));
        messageRepository.save(new Message("o2", "bbbbbbbbbb"));
        messageRepository.save(new Message("o3", "cccccccccc"));
        Assert.assertEquals(3, messageRepository.findAll().size());
    }

    @Test
    public void testCache() throws Exception {
        User u1 = userRepository.findByName("BBB");
        System.out.println("第一次查询：" + u1.getAge());
        User u2 = userRepository.findByName("BBB");
        System.out.println("第二次查询：" + u2.getAge());
        u2.setAge(30);
        userRepository.save(u2);
        User u3 = userRepository.findByName("BBB");
        System.out.println("第三次查询：" + u3.getAge());
    }

    @Test
    public void testFindUser() throws Exception {
        List<User> users = userRepository.findUser("AAA");
        for (User user : users) {
            System.out.println("#########" + user.getName() + "  " + user.getAge());
        }
    }

    @Test
    public void testFindUserNative() throws Exception {
        List<User> users = userRepository.findUserNative("AAAas");
        for (User user : users) {
            System.out.println("#########" + user.getName() + "  " + user.getAge());
        }
    }

    @Test
    public void testLike() throws Exception {
        List<User> users = userRepository.findByNameLikeAndAgeGreaterThan("B", 10);
        for (User user : users) {
            System.out.println("#########" + user.getName() + "  " + user.getAge());
        }
    }

    @Test
    public void testIncreaseSalary() throws Exception {
        int id = userRepository.increaseSalary("AAAas", 291L);
        System.out.println("#########" + id);

    }

    /**
     * 无条件分页查询
     *
     * @throws Exception
     */
    @Test
    public void testPage() throws Exception {
        int pageNumber = 2;
        int pageSize = 1;
        PageRequest request = this.buildPageRequest(pageNumber, pageSize, new Sort(Direction.ASC, "id"));
        Page<User> sourceCodes = this.userRepository.findAll(request);
        List<User> users = sourceCodes.getContent();
        for (User user : users) {
            System.out.println("#########" + user.getName() + "  " + user.getAge());
        }
    }

    //构建PageRequest
    private PageRequest buildPageRequest(int pageNumber, int pagzSize, Sort sort) {
        return new PageRequest(pageNumber - 1, pagzSize, sort);
    }

    /**
     * annotation 注解 带条件的JPQL分页查询(带过滤和排序)
     *
     * @throws Exception
     */
    @Test
    public void testGetPage() throws Exception {
        int pageNumber = 1;
        int pageSize = 10;
        PageRequest request = new PageRequest(pageNumber - 1, pageSize, Direction.DESC, new String[]{"id", "name"});
        Page<User> sourceCodes = this.userRepository.getPage("a", 0, request);
        List<User> users = sourceCodes.getContent();
        for (User user : users) {
            System.out.println("#########" + user.getName() + "  " + user.getAge());
        }
    }

    /**
     * 不带查询条件的
     *
     * @throws Exception
     */
    @Test
    public void testFindUserNoCriteria() throws Exception {
        int pageNumber = 1;
        int pageSize = 10;
        Pageable pageable = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.ASC, "id");
        Page<User> page = userRepository.findAll(pageable);
        List<User> users = page.getContent();
        for (User user : users) {
            System.out.println("#########" + user.getName() + "  " + user.getAge());
        }
    }

    /**
     * 带查询条件的 PageRequest pageNumber从0开始的
     *
     * @throws Exception
     */
    @Test
    public void testFindUserCriteria() throws Exception {
        int pageNumber = 1;
        int pageSize = 10;
        UserQuery userQuery = new UserQuery();
        userQuery.setAge(1);
//        userQuery.setName("a");
        Pageable pageable = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.ASC, "id");
        Page<User> pageContent = userRepository.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != userQuery.getName() && !"".equals(userQuery.getName())) {
//                    list.add(criteriaBuilder.equal(root.get("name").as(String.class), userQuery.getName()));
                    list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + userQuery.getName() + "%"));
                }

                //Int
                if (userQuery.getAge() != null && userQuery.getAge() > 0) {
                    list.add(criteriaBuilder.equal(root.get("age"), userQuery.getAge()));
                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        List<User> users = pageContent.getContent();
        System.out.println("###########" + users.size());
        for (User user : users) {
            System.out.println("#########" + user.getName() + "  " + user.getAge());
        }
    }

    @Test
    public void testMul() {
        List<RedisUserVO> list = userRepository.findMul();
        for (RedisUserVO vo : list) {
            System.out.println("#########" + vo.getName());
        }
    }

    @Test
    public void test22() throws Exception {
//        RedisUser u1 = redisUserRepository.findByName("AAA",0);
        RedisUser u1 = redisUserRepository.findByName("AAA");
        System.out.println("第一次查询：" + u1.getAge());
    }
        /**
         * 使用 hibernate @OneToOne 模式 有点麻烦 所以不建议使用
         * https://www.tianmaying.com/tutorial/spring-jpa-query 多表复杂查询
         *
         * @throws Exception
         */
        @Test
        public void testFindUserCriteriaTables() throws Exception {
//        int pageNumber = 1;
//        int pageSize = 10;
//        Pageable pageable = new PageRequest(pageNumber - 1, pageSize);
//        Page<User> pageContent = userRepository.findByUser(0, pageable);
//        List<User> users = pageContent.getContent();
//        System.out.println("###########" + users.size());
//        for (User user : users) {
//            System.out.println("#########" + user.getName() + "  " + user.getAge());
//        }
        }

        /**
         * jpa 多表关联查询 由于返回值是一个List<Object[]>转关VO对象比较麻烦 此处建议使用spring
         * 的jdbctemplate来做处理返回map
         *
         * @throws Exception
         */
        @Test
        public void testfindByUserVO1() throws Exception {
            List<Object> result = userRepository.findByUserVO1();

            List<HashMap<String, Object>> list = listArrayToListMap(result, new String[]{"id", "name", "age", "sex"});
            for (HashMap<String, Object> map : list) {
                UserVO vo = new UserVO();
                BeanUtils.populate(vo, map);
                System.out.println("######" + vo.getSex() + " name: " + vo.getName());
            }

        }

        /**
         * 不能使用 会报错
         *
         * Note, that we currently don’t support execution of dynamic sorting
         * for native queries as we’d have to manipulate the actual query
         * declared and we cannot do this reliably for native SQL. You can
         * however use native queries for pagination by specifying the count
         * query yourself:
         *
         * @throws Exception
         */
        @Test
        public void testfindByUserVOSort() throws Exception {

//        List<Object> result = userRepository.findByUserVOSort(new Sort("name"));
//
//        List<HashMap<String, Object>> list = listArrayToListMap(result, new String[]{"id", "name", "age", "sex"});
//        for (HashMap<String, Object> map : list) {
//            UserVO vo = new UserVO();
//            BeanUtils.populate(vo, map);
//            System.out.println("######" + vo.getSex() + " name: " + vo.getName());
//        }
        }
        /**
         * 将List<Object[]>数组对象转换成List<HashMap<String, Object>>集合对象
         */
    public static List<HashMap<String, Object>> listArrayToListMap(List<Object> list, String... keys) {
        if (list == null || list.size() == 0) {
            return null;
        }
        List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < list.size(); i++) {
            if (!(list.get(i) instanceof Map)) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                Object[] m = (Object[]) list.get(i);
                for (int j = 0; j < keys.length; j++) {
                    map.put(keys[j], m[j]);
                }
                mapList.add(map);
            }
        }
        return mapList;
    }
    @Autowired
    @Qualifier("primaryJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    /**
     * 返回map使用BeanUtils.populate(vo, map)手动转换
     *
     * @throws Exception
     */
    @Test
    public void testfindByUserVOBySpringJdbcTemplateMap() throws Exception {
        String sql = "select u.id,u.name,u.age,r.sex from user u, redis_user r where r.userid=u.id";
//        List<UserVO> list = jdbcTemplate.queryForList(sql, UserVO.class, null);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : list) {
            UserVO vo = new UserVO();
            BeanUtils.populate(vo, map);
            System.out.println("######" + vo.getSex());
        }
    }

    /**
     * 使用 BeanPropertyRowMapper查询
     *
     * @throws Exception
     */
    @Test
    public void testfindByUserVOBySpringJdbcTemplateBeanPropertyRowMapper() throws Exception {
        String sql = "select u.id,u.name,u.age,r.sex from user u, redis_user r where r.userid=u.id";
//        List<UserVO> list = jdbcTemplate.queryForList(sql, UserVO.class, null);
        BeanPropertyRowMapper<UserVO> rm = new BeanPropertyRowMapper<UserVO>(UserVO.class);
        rm.setPrimitivesDefaultedForNullValue(true);

        List<UserVO> list = jdbcTemplate.query(sql, rm);
        for (UserVO vo : list) {
            System.out.println("######" + vo.getSex());
        }
    }

    /**
     * 使用RowMapper查询
     *
     * @throws Exception
     */
    @Test
    public void testfindByUserVOBySpringJdbcTemplateRowMapper() throws Exception {
        String sql = "select u.id,u.name,u.age,r.sex from user u, redis_user r where r.userid=u.id";
//        List<UserVO> list = jdbcTemplate.queryForList(sql, UserVO.class, null);
        BeanPropertyRowMapper<UserVO> rm = new BeanPropertyRowMapper<UserVO>(UserVO.class);
        rm.setPrimitivesDefaultedForNullValue(true);

        List<UserVO> list = jdbcTemplate.query(sql, new UserVOMapper());
        for (UserVO vo : list) {
            System.out.println("######" + vo.getSex());
        }
    }

    private static final class UserVOMapper implements RowMapper {

        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserVO vo = new UserVO();
            vo.setId(rs.getLong("id"));
            vo.setAge(rs.getInt("age"));
            vo.setName(rs.getString("name"));
            vo.setSex(rs.getInt("sex"));
            return vo;
        }
    }
}
