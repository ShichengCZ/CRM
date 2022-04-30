package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.*;
import com.bjpowernode.crm.vo.PagenationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


public class ActivityController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到市场活动控制器");

        String path = request.getServletPath();

        if("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);

        }else if("/workbench/activity/save.do".equals(path)){

            save(request,response);

        }else if("/workbench/activity/pageList.do".equals(path)){

            pageList(request,response);

        }else if("/workbench/activity/delete.do".equals(path)){

            delete(request,response);

        }else if("/workbench/activity/getUserListAndActivity.do".equals(path)){

            getUserListAndActivity(request,response);

        }else if("/workbench/activity/update.do".equals(path)){

            update(request,response);

        }else if("/workbench/activity/detail.do".equals(path)){

            detail(request,response);

        }else if("/workbench/activity/getRemarkListByAid.do".equals(path)){

            getRemarkListByAid(request,response);

        }else if("/workbench/activity/deleteRemarkById.do".equals(path)){

            deleteRemarkById(request,response);

        }else if("/workbench/activity/saveRemark.do".equals(path)){

            saveRemark(request,response);

        }else if("/workbench/activity/updateRemark.do".equals(path)){

            updateRemark(request,response);

        }

    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入修改评论操作");

        String id = request.getParameter("id");

        String noteContent = request.getParameter("content");

        String editTime = DateTimeUtil.getSysTime();

        String editBy = ((User) request.getSession().getAttribute("user")).getName();

        String editFlag = "1";
        ActivityRemark ar = new ActivityRemark();

        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditTime(editTime);
        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        ar.setEditFlag(editFlag);



        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.updateRemark(ar);

        Map<String,Object> map = new HashMap<>();


        map.put("success",flag);
        map.put("ar",ar);


        PrintJson.printJsonObj(response,map);

    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入添加评论");

        String id = UUIDUtil.getUUID();
        String aid = request.getParameter("id");
        String content = request.getParameter("content");

        User user = (User) request.getSession().getAttribute("user");
        String username = user.getName();

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        ActivityRemark ar = new ActivityRemark();

           ar.setId(id);
           ar.setNoteContent(content);
           ar.setCreateTime(DateTimeUtil.getSysTime());
           ar.setCreateBy(username);
           ar.setActivityId(aid);
           ar.setEditFlag("0");


        Map<String,Object> map = new HashMap<>();

        boolean flag = as.saveRemark(ar);

        map.put("success",flag);
        map.put("ar",ar);


        PrintJson.printJsonObj(response,map);


    }

    private void deleteRemarkById(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入删除评论操作");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.deleteRemarkById(id);

        PrintJson.printJsonFlag(response,flag);
    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<ActivityRemark> list = as.getRemarkListByAid(id);

        PrintJson.printJsonObj(response,list);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入详细信息操作");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        Activity activity = as.detail(id);

        request.setAttribute("a",activity);


            //内部项目名，不需要写项目名
            request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);


    }

    private void update(HttpServletRequest request, HttpServletResponse response) {


        System.out.println("进入修改活动操作");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String systime = sdf.format(date);

        String editTime = systime;

        User user = (User) request.getSession().getAttribute("user");
        String username = user.getName();
        String editBy = username;

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setEditTime(editTime);
        a.setEditBy(editBy);

        boolean flag = as.update(a);

        PrintJson.printJsonFlag(response,flag);



    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入查询用户信息列表和根据市场活动id查询单挑记录");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        /*

        总结：
            controller调用service方法，返回值应该是什么

            前端要什么，Servlet就要从service层发送什么
            uList
            a

            用map即可，这种需求的复用率较低
         */

        Map<String,Object> map = as.getUserListAndActivity(id);

        PrintJson.printJsonObj(response,map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动的删除操作");

        String[] ids = request.getParameterValues("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag= as.delete(ids);

        PrintJson.printJsonFlag(response,flag);


    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询市场活动信息列表的操作（结合条件查询和列表查询+分页查询）");



        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNoStr = request.getParameter("pageNo");
        //每页展现的记录数
        int pageNo = Integer.valueOf(pageNoStr);
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo - 1) * pageSize;

        //vo是从后台往页面上传
        Map<String,Object> map = new HashMap<>();
        map.put("owner",owner);
        map.put("name",name);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        /*
            前端要什么，controller就返回什么
                        市场活动信息列表
                        查询的总条数

                        业务层拿到了以上两项信息，如果做返回
                        map
                        map.put("dataList":dataList)
                        map.put("total":total)
                        PrintJson map-->Json

                        vo

                        pagenationVO<T>
                            private int total;
                            private List<T> dataList;

                        pageNationVO<Activity> vo = new PagenationVO<>;
                        vo.setTatal(total);
                        vo.setDataList(dataList);
                        PrintJson vo---> json
                        将来分页查询，每个模块都有，所以我们选择使用一个通用vo，操作起来比较方便

         */
        PagenationVO<Activity> vo = as.pageList(map);

        PrintJson.printJsonObj(response,vo);



    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入保存活动操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String createTime = DateTimeUtil.getSysTime();
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();


       ActivityService us = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

       Activity a = new Activity();
       a.setId(id);
       a.setOwner(owner);
       a.setName(name);
       a.setStartDate(startDate);
       a.setEndDate(endDate);
       a.setCost(cost);
       a.setDescription(description);
       a.setCreateTime(createTime);
       a.setCreateBy(createBy);

       boolean flag = us.save(a);

       PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入获取用户姓名操作");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = us.getUserList();

        PrintJson.printJsonObj(response,userList);

    }


}




































