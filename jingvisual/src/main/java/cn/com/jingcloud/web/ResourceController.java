/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.web;

import cn.com.jingcloud.domain.entity.generic.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.com.jingcloud.utils.constant.JingVisualConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;
import cn.com.jingcloud.service.ResourceService;

/**
 *
 * @author liyong
 */
@Controller
@Api(value = "平台资源接口")
public class ResourceController {

    private final static Logger LOG = LoggerFactory.getLogger(ResourceController.class);
    @Autowired
    ResourceService resourceService;

    /**
     * async
     *
     * @param urlParam
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/showZone", method = RequestMethod.GET)
    @ResponseBody
    public String showZone(@RequestParam("urlParam") String urlParam) {
        String result = "";
        Future<String> task = resourceService.getRemotePostResult(urlParam);
        while (true) {

            if (task.isDone()) {
                LOG.debug("deal async task is Done: " + urlParam);

                try {
                    result = task.get();
                } catch (InterruptedException e) {
                    LOG.error("showZone InterruptedException: " + urlParam + e.getMessage());
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {//多线程submit里异常将被封装在此
                    LOG.error("showZone ExecutionException" + urlParam + e.getMessage());
//            if (e.getCause() instanceof RuntimeException) {
//                throw (RuntimeException) e.getCause();
//            }
                }
                break;
            }

        }

        return result;
    }

    /**
     * chart
     *
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "batch", method = RequestMethod.GET)
    public String batch() {
        return "batch";
    }

    /**
     * svg
     *
     * @return
     */
    @RequestMapping(value = "svg", method = RequestMethod.GET)
    public String svg() {
        return "svg/svg";
    }

    /**
     * css
     *
     * @return
     */
    @RequestMapping(value = "css", method = RequestMethod.GET)
    public String css() {
        return "svg/index";
    }
}
