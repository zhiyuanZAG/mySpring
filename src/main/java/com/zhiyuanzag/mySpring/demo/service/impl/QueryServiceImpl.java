package com.zhiyuanzag.mySpring.demo.service.impl;

import com.zhiyuanzag.mySpring.demo.service.QueryService;
import com.zhiyuanzag.mySpring.mvcframwork.annotation.ZYService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author zhiyuanzhang9
 * @create 2021/7/12 17:03
 * @since 1.0
 */
@ZYService
public class QueryServiceImpl implements QueryService {

    @Override
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\"" + name + "\", time:\"" + time + "\"}";

        return json;
    }
}
