package com.zhiyuanzag.mySpring.demo.service.impl;

import com.zhiyuanzag.mySpring.demo.service.IDemoService;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYService;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author zhiyuanzhang9
 * @create 2021/5/22 18:26
 * @since 1.0
 */
@ZYService
public class IDemoServiceImpl implements IDemoService {

    @Override
    public String show(String name) {
        return "My Name is: " + name + ", from DemoService.";
    }
}
