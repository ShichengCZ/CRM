package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PagenationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    boolean save(Clue clue);

   PagenationVO<Clue> pageList(Map<String, Integer> map);

    Clue detail(String id);

    boolean unbund(String id);


    boolean bund(String cid, String[] aids);

    boolean convert(String clueId, Tran t, String createBy);
}
