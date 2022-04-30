package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran t);

    List<Tran> getTransactionList(Map<String,Object> map);

    int getTransactionNum(Map<String,Object> map);

    Tran getTransactionById(String id);

    int changeStage(Tran t);

    int getTotal();

    List<Map<String,String>> getCharts();

}
