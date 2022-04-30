package com.bjpowernode.crm.workbench.service.impl;


import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PagenationVO;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean save(Clue clue) {
        int count = clueDao.save(clue);

        return count == 1?true:false;
    }

    @Override
    public PagenationVO<Clue> pageList(Map<String, Integer> map) {

        int count = clueDao.pageListNum();

        List<Clue> list = clueDao.pageList(map);

        PagenationVO<Clue> vo = new PagenationVO<>();

        vo.setTotal(count);
        vo.setDataList(list);

        return vo;


    }

    @Override
    public Clue detail(String id) {

        Clue c = clueDao.detail(id);

        return c;
    }

    @Override
    public boolean unbund(String id) {

        int count = clueDao.unbund(id);

        return count == 1? true:false;
    }

    @Override
    public boolean bund(String cid, String[] aids) {
        boolean flag = true;



        for (String aid:aids
             ) {
            //取得每一个aid和cid做关联

            ClueActivityRelation car = new ClueActivityRelation();

            String id = UUIDUtil.getUUID();

            car.setId(id);
            car.setActivityId(aid);
            car.setClueId(cid);

            int count = clueActivityRelationDao.bund(car);

            flag = count == 1?true:false;
        }

        return flag;



    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {


        String createTime = DateTimeUtil.getSysTime();

        boolean flag = true;

        //1.通过线索id获取线索对象，(线索对象当中封装了线索的信息)

        Clue c = clueDao.getById(clueId);

        //2.通过线索对象提取客户信息，当该客户不存在的时候，新建客户(根据公司的名称精确匹配，判断该客户是否存在)

        String company = c.getCompany();
        Customer cus = customerDao.getCustomerByName(company);

        if (cus == null) {
            //没这个客户，需要新建一个

            cus = new Customer();

            cus.setId(UUIDUtil.getUUID());
            cus.setAddress(c.getAddress());
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setOwner(c.getOwner());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setName(c.getCompany());
            cus.setDescription(c.getDescription());
            cus.setCreateTime(createTime);
            cus.setCreateBy(createBy);
            cus.setContactSummary(c.getContactSummary());

            //添加客户

            int count1 = customerDao.save(cus);

            if (count1 != 1) {
                flag = false;
            }

            //经过第二步处理后，客户的信息已经拥有了，将来在处理其他表的时候，如果要使用客户id
            //直接使用cus.getId()即可
        }
            //3.通过线索对象提取联系人信息，保存联系人
            Contacts con = new Contacts();

            con.setId(UUIDUtil.getUUID());
            con.setSource(c.getSource());
            con.setOwner(c.getOwner());
            con.setNextContactTime(c.getNextContactTime());
            con.setMphone(c.getMphone());
            con.setJob(c.getJob());
            con.setFullname(c.getFullname());
            con.setEmail(c.getEmail());
            con.setDescription(c.getDescription());
            con.setCustomerId(cus.getId());
            con.setCreateTime(createTime);
            con.setCreateBy(createBy);
            con.setContactSummary(c.getContactSummary());
            con.setAddress(c.getAddress());
            con.setAppellation(c.getAppellation());

            //添加联系人
            int count2 = contactsDao.save(con);

            if (count2 != 1){
                flag = false;
            }

            //经过第三部的处理后，联系人的信息已经处理好了，如果以后需要联系人的id的时候，只需要
            //con.getId();

            //4.线索备注转换到客户备注以及联系人备注
            //查询出与该线索关联的备注信息列表

            List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);

            //取出每一条线索备注
            for (ClueRemark clueRemark:clueRemarkList
                 ) {

                //主要转换到客户备注和联系人备注的就是这个备注信息
                String noteContent = clueRemark.getNoteContent();

                //创建客户备注对象，添加客户备注

                //创建联系人备注对象，添加联系人
                CustomerRemark customerRemark = new CustomerRemark();
                customerRemark.setId(UUIDUtil.getUUID());
                customerRemark.setCreateBy(createBy);
                customerRemark.setCreateTime(createTime);
                customerRemark.setCustomerId(cus.getId());
                customerRemark.setEditFlag("0");
                customerRemark.setNoteContent(noteContent);

                int count3 = customerRemarkDao.save(customerRemark);
                if (count3 != 1){
                    flag = false;
                }

                ContactsRemark contactsRemark = new ContactsRemark();
                contactsRemark.setId(UUIDUtil.getUUID());
                contactsRemark.setCreateBy(createBy);
                contactsRemark.setCreateTime(createTime);
                contactsRemark.setContactsId(con.getId());
                contactsRemark.setEditFlag("0");
                contactsRemark.setNoteContent(noteContent);

                int count4 = contactsRemarkDao.save(contactsRemark);
                if (count4 != 1){
                    flag = false;
                }
            }

            //“线索和市场活动”的关系转换到“联系人和市场活动”的关系
            //查询处该条线索关联的市场活动，查询与市场活动的关联关系列表

            List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
            //遍历出每一条与市场活动关联的关联关系记录

            for (ClueActivityRelation clueActivityRelation:clueActivityRelationList){
                //从每一条遍历出来的记录中取出关联的市场活动id

                String activityId = clueActivityRelation.getActivityId();

                //创建联系人与市场活动的关联关系对象，让第三步生成的联系人与市场活动做关联
                ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
                contactsActivityRelation.setId(UUIDUtil.getUUID());
                contactsActivityRelation.setActivityId(activityId);
                contactsActivityRelation.setContactsId(con.getId());
                //添加联系人与市场活动的关联关系
                int count5 = contactsActivityRelationDao.save(contactsActivityRelation);

                if (count5 != 1) {
                    flag = false;
                }
            }

                //如果有创建教育需求，创建一条交易

                if (t != null) {
                    /*
                        t对象在controller里面已经封装好的信息如下：
                            id,money,name,expectedDate,stage,activityId,createBy,createTime

                        接下来可以通过第一步生成的c对象，取出一些信息，继续完善对t对象的封装

                     */

                    t.setSource(c.getSource());
                    t.setOwner(c.getOwner());
                    t.setNextContactTime(c.getNextContactTime());
                    t.setDescription(c.getDescription());
                    t.setCustomerId(cus.getId());
                    t.setContactsId(con.getId());
                    t.setContactSummary(c.getContactSummary());

                    int count6 = tranDao.save(t);
                    if (count6 != 1) {
                        flag = false;
                    }

                    TranHistory th = new TranHistory();

                    th.setId(UUIDUtil.getUUID());
                    th.setCreateBy(createBy);
                    th.setCreateTime(createTime);
                    th.setExpectedDate(t.getExpectedDate());
                    th.setStage(t.getStage());
                    th.setMoney(t.getMoney());
                    th.setTranId(t.getId());

                    int count7 = tranHistoryDao.save(th);
                    if (count7 != 1){
                        flag = false;
                    }

                }

                //8.删除线索备注
        for (ClueRemark clueRemark:clueRemarkList
             ) {

            int count8 = clueRemarkDao.delete(clueRemark);

            if (count8 != 1){

                flag = false;

            }


        }

        //删除线索和市场活动的关系

        for (ClueActivityRelation clueActivityRelation: clueActivityRelationList
             ) {

            int count9 = clueActivityRelationDao.delete(clueActivityRelation);

            if (count9 != 1) {
                flag = false;

            }
        }


        //删除线索

        int count10 = clueDao.delete(clueId);
        if (count10 != 1){

            flag = false;

        }


        return flag;



    }


}
