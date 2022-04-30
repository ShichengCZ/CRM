package com.bjpowernode.settings.test;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import org.apache.ibatis.type.DateTypeHandler;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/*
    Junit:
        单元测试
        是外来实际项目开发中，用来代替主方法main的


 */
public class ActivityTest {

    @Test
    public void testSave(){
        Activity a = new Activity();
        a.setId(UUIDUtil.getUUID());
        a.setName("宣传推广会");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.save(a);

        //断言（添加，修改，删除操作多用）
        Assert.assertEquals(true,flag);
    }

    @Test
    public void testUpdate(){

    }

}
