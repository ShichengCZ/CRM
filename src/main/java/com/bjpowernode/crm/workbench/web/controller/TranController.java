package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PagenationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到交易控制器");

        String path = request.getServletPath();

        if ("/workbench/transaction/add.do".equals(path)) {

           add(request, response);

        } else if ("/workbench/transaction/getActivityList.do".equals(path)) {

            getActivityList(request, response);

        }else if ("/workbench/transaction/getContactsList.do".equals(path)) {

            getContactsList(request, response);

        }else if ("/workbench/transaction/getCustomerName.do".equals(path)) {

            getCustomerName(request, response);

        }else if ("/workbench/transaction/getTransactionList.do".equals(path)) {

            getTransactionList(request, response);

        }else if ("/workbench/transaction/save.do".equals(path)) {

            save(request, response);

        }else if ("/workbench/transaction/detail.do".equals(path)) {

            detail(request, response);

        }else if ("/workbench/transaction/getHistoryListByTranId.do".equals(path)) {

            getHistoryListByTranId(request, response);

        }else if ("/workbench/transaction/changeStage.do".equals(path)) {

            changeStage(request, response);

        }else if ("/workbench/transaction/getCharts.do".equals(path)) {

            getCharts(request, response);

        }



    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得交易阶段数量统计图表的数据");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        /*

            业务层为我们返回
            total,
            dataList

            通过map打包以上两项进行返回

         */

         Map<String,Object> map = ts.getCharts();

        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入改变阶段操作");

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User) request.getSession().getAttribute("user")).getName();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditTime(editTime);
        t.setEditBy(editBy);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.changeStage(t);


        Map<String,Object> pMap = (Map<String, Object>) this.getServletContext().getAttribute("pMap");
        String possibility = (String) pMap.get(stage);
        t.setPossibility(possibility);


        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("t",t);

        PrintJson.printJsonObj(response,map);




    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入根据交易id查询交易历史操作");

        String tranId = request.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranHistory> thList = ts.getHistoryListByTranId(tranId);

        for (TranHistory th:thList
             ) {
            String stage = th.getStage();

            Map<String,String> map = (Map<String, String>) this.getServletContext().getAttribute("pMap");

            String possibility = map.get(stage);

            th.setPossibility(possibility);

        }

        PrintJson.printJsonObj(response,thList);


    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran t = ts.detail(id);

        //处理可能性
        /*
            阶段t
            阶段和可能性之间的对应关系 pMap


         */

        String stage = t.getStage();
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");

        String possibility = pMap.get(stage);

        t.setPossibility(possibility);

        request.setAttribute("t",t);

        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {


        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");


        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateBy(createBy);
        t.setCreateTime(createTime);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(t,customerName);

        if (flag){

            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");

        }


    }

    private void getTransactionList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("aaa");

        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String customer = request.getParameter("customer");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String contact = request.getParameter("contact");

        Map<String,Object> map = new HashMap<>();

        map.put("owner",owner);
        map.put("name",name);
        map.put("customer",customer);
        map.put("stage",stage);
        map.put("type",type);
        map.put("source",source);
        map.put("contact",contact);



        PagenationVO<Tran> vo = new PagenationVO<>();

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        int pageNo1 = Integer.valueOf(pageNo);
        int pageSize1 = Integer.valueOf(pageSize);


        List<Tran> tranList = ts.getTransactionList(pageNo1,pageSize1,map);


        TranService ts1 = (TranService) ServiceFactory.getService(new TranServiceImpl());

        int total = ts1.getTransactionNum(map);

        vo.setTotal(total);

        vo.setDataList(tranList);

        PrintJson.printJsonObj(response,vo);


    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入获取用户名称操作");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        String name = request.getParameter("name");

        List<String> sList= cs.getCustomerName(name);

        PrintJson.printJsonObj(response,sList);
    }

    private void getContactsList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入获取联系人列表操作");

        String contactsName = request.getParameter("contactsName");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<Contacts> contactsList = ts.getContactsList(contactsName);

        PrintJson.printJsonObj(response,contactsList);



    }

    private void getActivityList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入获取活动列表页面");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        String content = request.getParameter("content");

        List<Activity> activityList = ts.getActivityList(content);

        PrintJson.printJsonObj(response,activityList);

    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入获取用户列表操作");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<User> userList = ts.getUserList();

        request.setAttribute("userList",userList);

        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);

    }
}



































