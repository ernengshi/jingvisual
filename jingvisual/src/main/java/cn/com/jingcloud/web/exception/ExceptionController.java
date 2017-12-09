/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web.exception;

import cn.com.jingcloud.exception.JingVisualException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @author liyong
 *
 */
@ApiIgnore
@Controller
public class ExceptionController {

    /**
     * html页面形式的
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/e_html")
    public String e_html() throws Exception {
        throw new Exception("发生错误");
    }

    /**
     * json形式的
     *
     * @return
     * @throws JingVisualException
     */
    @RequestMapping("/e_json")
    public String e_json() throws JingVisualException {
        throw new JingVisualException("发生错误2");
    }
}
