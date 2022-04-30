package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PagenationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {

    ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);

    UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public boolean save(Activity a) {
        boolean flag = true;

        int count = activityDao.save(a);

        if (count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public PagenationVO<Activity> pageList(Map<String, Object> map) {

        int total = activityDao.getTotalByCondition(map);

        List<Activity> dataList = activityDao.getActivityListByCondition(map);

        PagenationVO<Activity> vo = new PagenationVO<>();

        vo.setDataList(dataList);

        vo.setTotal(total);

        return vo;
    }

    @Override
    public boolean delete(String[] ids) {

        boolean flag = true;

        //先查询出需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);
        //删除备注，返回收到影响的条数（实际删除的数量）
        int count2 = activityRemarkDao.deleteByAids(ids);
        //删除市场活动
        if (count2 != count1) {
            flag = false;
        }

        int count3 = activityDao.delete(ids);
        if (count3 != ids.length){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        /*
            目标：取uList和a
            之后打包成map返回
         */
        //取uList
        List<User> userList = userDao.getUserList();

        //取a

        Activity a = activityDao.getActivitybyId(id);

        Map<String, Object> map = new HashMap<>();
        map.put("uList",userList);
        map.put("a",a);

        return map;

    }

    @Override
    public boolean update(Activity a) {
        int count = activityDao.update(a);
        return count==1? true:false;
    }

    @Override
    public Activity detail(String id) {
        return activityDao.detail(id);
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String id) {
        return activityRemarkDao.getRemarkListByAid(id);
    }

    @Override
    public boolean deleteRemarkById(String id) {
        int count = activityRemarkDao.deleteRemarkById(id);
        return count == 1? true : false;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        int count = activityRemarkDao.saveRemark(ar);
        return count == 1? true : false;

    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        int count = activityRemarkDao.updateRemark(ar);

        return count == 1? true : false;
    }

    @Override
    public List<Activity> getActivityByClueId(String id) {

        List<Activity> aList = activityDao.getActivityByClueId(id);

        return aList;

    }

    @Override
    public List<Activity> getActivityListByContentAndNoAso(Map<String,String> map) {
        List<Activity> aList = activityDao.getActivityListByContent(map);
        return aList;
    }

    @Override
    public List<Activity> getActivityListByName(String name) {

        List<Activity> aList = activityDao.getActivityListByName(name);
        return aList;
    }
}
