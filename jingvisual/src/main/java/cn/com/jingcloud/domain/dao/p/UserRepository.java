/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.domain.dao.p;

import cn.com.jingcloud.domain.entity.p.RedisUserVO;
import cn.com.jingcloud.domain.entity.p.User;
import cn.com.jingcloud.domain.entity.p.RedisUser;
import java.util.List;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * https://spring.io/guides/gs/accessing-data-jpa/
 * Spring-data-jpa的一大特性:通过解析方法名创建查询 由于Spring-data-jpa依赖于Hibernate 所以JPQL语句 有点类似
 * hql </br>
 * 主数据源
 *
 *
 * （1）可以通过自定义的 JPQL 完成 UPDATE 和 DELETE 操作。 注意： JPQL 不支持使用 INSERT； （2）在
 *
 * @Query注解中编写 JPQL 语句， 但必须使用 @Modifying 进行修饰. 以通知 SpringData， 这是一个 UPDATE 或
 * DELETE 操作 （3）UPDATE 或 DELETE 操作需要使用事务，此时需要定义 Service 层，在 Service 层的方法上添加事务操作；
 * （4）默认情况下，SpringData 的每个方法上有事务， 但都是一个只读事务。 他们不能完成修改操作。
 *
 * @author liyong
 */
@CacheConfig(cacheNames = "users")
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Cacheable(key = "#p0")
    User findByName(String name);

    //如果缓存需要更新，且不干扰方法的执行,可以使用注解@CachePut,
    //@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，
    //并将执行结果以键值对的形式存入指定的缓存中。
    @CachePut(key = "#p0.name")
    @Modifying//通知jpa这是一个update或者delete操作
    @Override
    User save(User user);

    User findByNameAndAge(String name, Integer age);

    @Query("select u from User u where u.name=:name")
    List<User> findUser(@Param("name") String name);

    /**
     * 可以如下加上参数以使用原生查询：nativeQuery = true
     *
     * @param name
     * @return
     */
    @Query(value = "select * from user u where u.name=:name", nativeQuery = true)
    List<User> findUserNative(@Param("name") String name);

    /**
     * JPQL语句 出了使用?+数字的方式代替参数，还可以使用如下方式：
     */
    @Query("from User u where u.name like %?1% and u.age>?2")
    List<User> findByNameLikeAndAgeGreaterThan(String name, Integer age);

    //@Query 也可以用来修改和删除等，加上@Modifying即可：
    @Modifying
    @Transactional
    @Query("update User u set u.name = ?1 where u.id = ?2")
    public int increaseSalary(String name, long id);

    /**
     * *
     * 分页查询 JPQL语句
     *
     * @param name
     * @param pageable
     * @return
     */
    @Query("SELECT u FROM User u WHERE u.name like %:name% and u.age > :age")
    Page<User> getPage(@Param("name") String name, @Param("age") Integer age, Pageable pageable);

    /**
     * 多表复杂查询 使用map http://bbs.csdn.net/topics/392064279
     * 将List<Object[]>数组对象转换成List<HashMap<String, Object>>集合对象
     * http://blog.csdn.net/cg_perfect/article/details/52985877
     * 这种复杂多表关联查询sql建议使用spring jdbctemplate
     *
     *
     *
     * Note, that we currently don’t support execution of dynamic sorting for
     * native queries as we’d have to manipulate the actual query declared and
     * we cannot do this reliably for native SQL. You can however use native
     * queries for pagination by specifying the count query yourself:
     *
     */
    //@Query("select new map(u.id,u.name,u.age,r.userid) from User u, RedisUser r where r.userid=u.id")
    @Query(value = "select u.id,u.name,u.age,r.sex from user u, redis_user r where r.userid=u.id order by u.name asc ", nativeQuery = true)
    //@Query("select new map(u.id,u.name,u.age) from User u")
    public List<Object> findByUserVO1();

    /**
     * jpa多表查询，使用new 的时候新建一个vo出来包含多表字段
     *
     * @return
     */
//    @Query(value = "select u.id,u.name,u.age,r.sex from user u, redis_user r where r.userid=u.id order by u.name asc ", nativeQuery = true)
    @Query(value = "select new cn.com.jingcloud.domain.entity.p.RedisUserVO(u.id, u.name, u.age, r.sex, r.userid) "
            + " from User u, RedisUser r where r.userid=u.id order by u.name asc")
    public List<RedisUserVO> findMul();

    /**
     * jpa文档中不建议对原生态sql进行动态排序,可能不太可靠; 动态排序可以使用 Pageable pageable 或者 Sort sort 即
     * new Sort(Direction.ASC, "id")
     */
//     @Query(value = "select u.id,u.name,u.age,r.sex from user u, redis_user r where r.userid=u.id ", nativeQuery = true)
//   @Query("select new map(u.id,u.name,u.age) from User u")
//    public List<Object> findByUserVOSort(Sort sort);
//    使用hibernate OneToOne 不太方便 不建议使用
//    @Query("select user from User user join user.ruser ruser where ruser.id > ?1")
//    Page<User> findByUser(long id, Pageable pageable);
}
