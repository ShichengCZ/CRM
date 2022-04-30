package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.*;

import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);


    @Override
    public List<User> getUserList() {

        List<User> userList = userDao.getUserList();

        return userList;
    }

    @Override
    public List<Activity> getActivityList(String content) {
        List<Activity> activityList = activityDao.getActivityListByContent2(content);
        return activityList;
    }

    @Override
    public List<Contacts> getContactsList(String contactsName) {
        List<Contacts> contactsList = contactsDao.getContactsList(contactsName);

        return contactsList;
    }

    @Override
    public List<Tran> getTransactionList(int pageNo,int pageSize,Map<String,Object> map2) {

        int pageOver = (pageNo - 1)*pageSize;

        /*String pageOver1 = String.valueOf(pageOver);
        String pageSize1 = String.valueOf(pageSize);*/

        map2.put("pageOver",pageOver);
        map2.put("pageSize",pageSize);

        List<Tran> tranList = tranDao.getTransactionList(map2);

        return tranList;

    }

    @Override
    public int getTransactionNum(Map<String,Object> map) {

        int total = tranDao.getTransactionNum(map);

        return total;

    }

    @Override
    public boolean save(Tran t, String customerName) {
        /*
            交易添加业务：
                在做添加之前，参数t里面就少了一项信息，就是客户的主键，customerId

                先处理客户相关需求

                (1)判断customerName,根据客户名称在客户表进行精确查询
                    如果有这个客户，则取出这个客户的id，封装到t对象中
                    如果没有这个客户，则在客户表新建一条客户信息，然后将新建的客户的id取出，封装到t对象中

                (2)经过以上的操作，t对象的中心就全了，需要执行添加交易的操作

                (3)添加交易完成后，需要创建一条交易历史


         */
        boolean flag = true;

        Customer customer = customerDao.getCustomerByName(customerName);

        if (customer == null) {


            //cus为空，则需要创建客户
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateTime(t.getCreateTime());
            customer.setCreateBy(t.getCreateBy());
            customer.setNextContactTime(t.getNextContactTime());
            customer.setOwner(t.getOwner());

            //添加客户

            int count1 = customerDao.save(customer);
            if (count1 != 1){
                flag = false;
            }

        }

        //通过以上对于客户的处理，不论是查询出来已有的客户，还是以前没有，我们新增的客户，总之客户已经有了，客户id也有了
        t.setCustomerId(customer.getId());

        //添加交易

        int count2 = tranDao.save(t);

        if (count2 != 1){

            flag = false;

        }

        //添加交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(t.getId());
        tranHistory.setMoney(t.getMoney());
        tranHistory.setStage(t.getStage());
        tranHistory.setExpectedDate(t.getExpectedDate());
        tranHistory.setCreateBy(t.getCreateBy());
        tranHistory.setCreateTime(t.getCreateTime());

        int count3 = tranHistoryDao.save(tranHistory);

        if (count3 != 1){
            flag = false;
        }

        return flag;



    }

    @Override
    public Tran detail(String id) {

        Tran t = tranDao.getTransactionById(id);

        return t;


    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {

        List<TranHistory> thList = tranHistoryDao.getHistoryListByTranId(tranId);

        return thList;

    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag = true;

        //改变交易阶段
        int count1 = tranDao.changeStage(t);

        if (count1 != 1){
            flag = false;
        }

        //交易阶段改变后，生成一条交易里斯
        TranHistory th = new TranHistory();

        th.setId(UUIDUtil.getUUID());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(t.getEditBy());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        //添加交易历史

        int count2 = tranHistoryDao.save(th);
        if (count2 != 1){
            flag = false;
        }



        return flag;

    }

    @Override
    public Map<String, Object> getCharts() {
        //取得total

        int total = tranDao.getTotal();

        //取得dataList

        List<Map<String,String>> dataList = tranDao.getCharts();

        //将total和dataList保存到map中返回map

        Map<String,Object> map = new HashMap<>();

        map.put("total",total);
        map.put("dataList",dataList);

        return map;


    }


}
