package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    int save(Clue clue);

    int pageListNum();

    List<Clue> pageList(Map<String, Integer> map);

    Clue detail(String id);

    int unbund(String id);


    Clue getById(String clueId);

    int delete(String clueId);
}
