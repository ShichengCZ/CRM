package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PagenationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.domain.Clue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ActivityService {

    boolean save(Activity a);

    PagenationVO<Activity> pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String,Object> getUserListAndActivity(String id);

    boolean update(Activity a);

    Activity detail(String id);

    List<ActivityRemark> getRemarkListByAid(String id);

    boolean deleteRemarkById(String id);

    boolean saveRemark(ActivityRemark ar);

    boolean updateRemark(ActivityRemark ar);

    List<Activity> getActivityByClueId(String id);

    List<Activity> getActivityListByContentAndNoAso(Map<String,String> map);

    List<Activity> getActivityListByName(String name);
}
