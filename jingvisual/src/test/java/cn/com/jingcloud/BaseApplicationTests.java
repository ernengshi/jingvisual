package cn.com.jingcloud;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

/**
 * 测试用例基类，用于MVC测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JingVisualApplication.class)
@Transactional //打开的话测试之后数据可自动回滚
public class BaseApplicationTests extends MockMvcResultMatchers {

	@Autowired
	WebApplicationContext webApplicationContext;

	protected MockMvc mockMvc;


	@Before
	public void setUp() throws Exception {
//		mockMvc = Mo ckMvcBuilders.standaloneSetup(new DemoCtrl()).build();
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}


	protected String buildGetReq(String path,MultiValueMap<String, String> params) throws Exception{

		if (params==null){
			return mockMvc.perform(MockMvcRequestBuilders.get(path)).andReturn().getResponse().getContentAsString();
		}else{
			return mockMvc.perform(MockMvcRequestBuilders.get(path).params(params)).andReturn().getResponse().getContentAsString();
		}
	}

	protected String buildPostReq(String path,MultiValueMap<String, String> params) throws Exception{
		if (params==null){
			return mockMvc.perform(MockMvcRequestBuilders.post(path)).andReturn().getResponse().getContentAsString();
		}else{
			return mockMvc.perform(MockMvcRequestBuilders.post(path).params(params)).andReturn().getResponse().getContentAsString();
		}
	}

	/**
	 * json的post提交方式
	 * @param path
	 * @param json
	 * @return
	 * @throws Exception
	 */
	protected String buildPostReq(String path, String json) throws Exception{
		String result = null;
		if (json==null){
			result = mockMvc.perform(MockMvcRequestBuilders.post(path)).andReturn().getResponse().getContentAsString();
		}else{
			result = mockMvc.perform(MockMvcRequestBuilders.post(path).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andReturn().getResponse().getContentAsString();
		}
//		String resultCode =  GsonUtil.gsonToMaps(result).get("resultCode").toString();
//		if (resultCode.equals("1")) {
//			throw new RuntimeException(GsonUtil.gsonToMaps(result).get("resultMsg").toString());
//		}
		return result;
	}

	/**
	 * json的put提交方式
	 * @param path
	 * @param json
	 * @return
	 * @throws Exception
	 */
	protected String buildPutReq(String path, String json) throws Exception{
		String result = null;
		if (json == null) {
			result = mockMvc.perform(MockMvcRequestBuilders.put(path)).andReturn().getResponse().getContentAsString();
		} else {
			result = mockMvc.perform(MockMvcRequestBuilders.put(path).contentType(MediaType.APPLICATION_JSON_UTF8).content(json)).andReturn().getResponse().getContentAsString();
		}
//		String resultCode =  GsonUtil.gsonToMaps(result).get("resultCode").toString();
//		if (resultCode.equals("1")) {
//			throw new RuntimeException(GsonUtil.gsonToMaps(result).get("resultMsg").toString());
//		}
		return result;

	}

	protected String buildDelReq(String path,MultiValueMap<String, String> params) throws Exception{
		if (params==null){
			return mockMvc.perform(MockMvcRequestBuilders.delete(path)).andReturn().getResponse().getContentAsString();
		}else{
			return mockMvc.perform(MockMvcRequestBuilders.delete(path).params(params)).andReturn().getResponse().getContentAsString();
		}
	}

	/**
	 * 断言代码片断
	 * @throws Exception
	 */
//	mockMvc.perform(request)
//				.andExpect(status().isOk())
//				.andExpect(content().string(equalTo("[{\"id\":1,\"name\":\"测试大师\",\"age\":20}]")));

}
