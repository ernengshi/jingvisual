package cn.com.jingcloud.utils;

import cn.com.jingcloud.config.redis.RedisUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 170056 on 2017/10/19.
 */
@Component
public class BasicDataUtils {

    private final Logger logger = LoggerFactory.getLogger(BasicDataUtils.class);

    @Autowired
    private RedisUtils redisUtil;

    /**
     * 设置JSON
     *
     * @param key
     * @param object
     */
    public void setRedis(String key, Object object) {
        JSONObject json = (JSONObject) JSON.toJSON(object);

        redisUtil.set(key, json.toString());
    }

    /**
     * 设置JSON
     *
     * @param key
     * @param
     */
    public <T> void setRedisList(String key, List<T> list) {
        JSONArray json = new JSONArray();
        for (T t : list) {
            json.add((JSONObject) JSON.toJSON(t));
        }

        redisUtil.set(key, json.toString());
    }

    /**
     * 从radis取得对象
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getRedis(String key, Class<T> tClass) {
        String json = redisUtil.getJson(key);
        if (json == null) {
            return null;
        }
        T result = JSON.parseObject(json, tClass);
        return result;
    }

    /**
     * 从radis取得list对象
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> List<T> getRedisList(String key, Class<T> tClass) {
        String json = redisUtil.getJson(key);
        if (json == null) {
            return null;
        }
        List<T> result = JSON.parseArray(json, tClass);
        return result;
    }

    public static void main(String[] args) {
//        UserTest user = new UserTest();
//        user.setName("aa");
//        user.setValue("dd");
//        BasicDataUtils utils = new BasicDataUtils();
//       // utils.setRedis("aa",user);
//        List<UserTest> list = new ArrayList<>();
//        list.add(user);
//        utils.setRedisList("aa",list);
//        List<UserTest> user1 = utils.getRedisList("aa",UserTest.class);
//        System.out.println(user1.get(0).getValue());

//        System.out.println(VehicleInOutStatus.NOTINFACTORY.code);
    }
}
