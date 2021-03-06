package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {

    int save(Activity a);

    //int getTotalByCondition(Map<String, Object> map);

    List<Activity> getActivityListByCondition(Map<String, Object> map);

    int getTotalByCondition(Map<String, Object> map);

    int delete(String[] ids);

    Activity getActivitybyId(String id);

    int update(Activity a);

    Activity detail(String id);

    List<Activity> getActivityByClueId(String id);

    List<Activity> getActivityListByContent(Map<String,String> map);

    List<Activity> getActivityListByName(String name);

    List<Activity> getActivityListByContent2(String content);
}
