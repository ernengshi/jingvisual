/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.service;

import cn.com.jingcloud.utils.repository.Pager;
import cn.com.jingcloud.vo.PlatformVO;

/**
 *
 * @author liyong
 */
public interface PlatformService {

    Pager<PlatformVO> pageIaasPlatform(int pageNo, int pageSize);

    Pager<PlatformVO> pageDaasPlatform(int pageNo, int pageSize);
}
