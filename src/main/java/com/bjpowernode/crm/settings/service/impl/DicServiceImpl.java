package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.settings.dao.DicTypeDao;
import com.bjpowernode.crm.settings.dao.DicValueDao;
import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {

    DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);
    DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);


    @Override
    public Map<String, List<DicValue>> getAll() {
        //将字典类型列表取出
        List<DicType> list = dicTypeDao.getTypeList();

        Map<String, List<DicValue>> map = new HashMap<>();

        for (DicType type: list
             ) {
            String typename = type.getCode();
            List<DicValue> dicList = dicValueDao.getValueByCode(typename);
            map.put(typename,dicList);
        }
        return map;
    }
}
