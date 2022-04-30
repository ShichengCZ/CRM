package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PagenationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到市场活动控制器");

        String path = request.getServletPath();

        if ("/workbench/clue/getUserList.do".equals(path)) {

            getUserList(request, response);

        } else if ("/workbench/clue/save.do".equals(path)) {

            save(request, response);

        }else if ("/workbench/clue/pageList.do".equals(path)) {

            pageList(request, response);

        }else if ("/workbench/clue/detail.do".equals(path)) {

            detail(request, response);

        }else if ("/workbench/clue/getActivityByClueId.do".equals(path)) {

            getActivityByClueId(request, response);

        }else if ("/workbench/clue/unbund.do".equals(path)) {

            unbund(request, response);

        }else if ("/workbench/clue/getActivityListByContentAndNoAso.do".equals(path)) {

            getActivityListByContentAndNoAso(request, response);

        }else if ("/workbench/clue/bund.do".equals(path)) {

            bund(request, response);

        }else if ("/workbench/clue/getActivityListByName.do".equals(path)) {

            getActivityListByName(request, response);

        }else if ("/workbench/clue/convert.do".equals(path)) {

            convert(request, response);

        }

    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("进入客户线索转换交易操作");

        String clueId = request.getParameter("clueId");

        System.out.println(clueId);

        String flag = request.getParameter("flag");

        Tran t = null;

        String createBy = ((User) request.getSession().getAttribute("user")).getName();

        if ("a".equals(flag)){

            t = new Tran();

            String id = UUIDUtil.getUUID();
            String money = request.getParameter("money");
            String expectedDate = request.getParameter("expectedDate");
            String name = request.getParameter("name");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");


            String createTime = DateTimeUtil.getSysTime();

            t.setId(id);
            t.setMoney(money);
            t.setExpectedDate(expectedDate);
            t.setName(name);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);


        }
        //让业务层根据t是否为null，是否创建交易

        /*
            为业务层传递的参数
            1.必须传递的参数clueId,有了这个clueId之后，我们才能知道要转换哪条记录
            2.必须传递参数t,因为在线索转换的过程中，有可能会临时创建一笔交易（业务层接受的t也有可能是个null）

         */
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag1 = cs.convert(clueId,t,createBy);

        if (flag1){

            response.sendRedirect(request.getContextPath() + "/workbench/clue/index.jsp");

        }


    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入模糊查询活动列表操作");

        String name = request.getParameter("content");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> aList = as.getActivityListByName(name);

        PrintJson.printJsonObj(response,aList);
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入绑定关联事件");

        String cid = request.getParameter("cid");

        System.out.println(cid);

        String[] aids = request.getParameterValues("aid");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.bund(cid,aids);

        PrintJson.printJsonFlag(response,true);


    }

    private void getActivityListByContentAndNoAso(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入模糊查询");

        String content = request.getParameter("searchContent");
        String id = request.getParameter("id");


        Map<String,String> map = new HashMap<>();

        map.put("content",content);
        map.put("id",id);

        ActivityService cs = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> aList = cs.getActivityListByContentAndNoAso(map);

        PrintJson.printJsonObj(response,aList);

    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入接触关系操作");

        String id = request.getParameter("id");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.unbund(id);

        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityByClueId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("跳转到根据clueid查Activity操作");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

       List<Activity> aList = as.getActivityByClueId(id);

        PrintJson.printJsonObj(response,aList);


    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("跳转到线索的详细信息页");

        String id = request.getParameter("id");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        Clue c = cs.detail(id);

        request.setAttribute("clue",c);

        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);




    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入页面展示操作");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String pageNoStr = request.getParameter("pageNo");
        //每页展现的记录数
        int pageNo = Integer.valueOf(pageNoStr);
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo - 1) * pageSize;

        Map<String,Integer> map = new HashMap<>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        PagenationVO<Clue> vo = cs.pageList(map);

        PrintJson.printJsonObj(response,vo);

    }

    private void getUserList (HttpServletRequest request, HttpServletResponse response){

            System.out.println("获取用户列表");

            UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

            List<User> userList = us.getUserList();

            PrintJson.printJsonObj(response, userList);

        }


        private void save (HttpServletRequest request, HttpServletResponse response){

            System.out.println("进入线索保存操作");

            String id = UUIDUtil.getUUID();
            String fullname = request.getParameter("fullname");
            String appellation = request.getParameter("appellation");
            String owner = request.getParameter("owner");
            String company = request.getParameter("company");
            String job = request.getParameter("job");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String website = request.getParameter("website");
            String mphone = request.getParameter("mphone");
            String state = request.getParameter("state");
            String source = request.getParameter("source");
            String createBy = request.getParameter("createBy");
            String createTime = DateTimeUtil.getSysTime();
            String description = request.getParameter("description");
            String contactSummary = request.getParameter("contactSummary");
            String nextContactTime = request.getParameter("nextContactTime");
            String address = request.getParameter("address");

            Clue clue = new Clue();

            clue.setId(id);
            clue.setFullname(fullname);
            clue.setAppellation(appellation);
            clue.setOwner(owner);
            clue.setCompany(company);
            clue.setJob(job);
            clue.setEmail(email);
            clue.setPhone(phone);
            clue.setWebsite (website);
            clue.setMphone(mphone);
            clue.setState(state);
            clue.setSource(source);
            clue.setCreateBy(createBy);
            clue.setCreateTime(createTime);
            clue.setDescription (description);
            clue.setContactSummary(contactSummary);
            clue.setNextContactTime(nextContactTime);
            clue.setAddress(address);

            ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

            boolean flag = cs.save(clue);

            PrintJson.printJsonFlag(response,flag);

        }
    }

































