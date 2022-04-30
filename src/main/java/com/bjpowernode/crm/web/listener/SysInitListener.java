package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        /*
            该方法事用来监听上下文域对象的方法，当服务器启动，上下文域对象创建
            对象创建完毕后，马上执行该方法
            event：该参数能够取得监听的对象
                监听的是什么对象，就可以通过该参数取得什么对象
                例如我们现在坚挺的上下文域对象，通过该参数就可以取得上下文域对象
         */
        System.out.println("上下文域对象创建了");

        ServletContext application = event.getServletContext();

        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        /*
         向业务层要7个list
            可以打包成为一个map
            业务层：
                map.put("appellation",dvList1);
                map.put("clueState",dvList2);
                map.put("stage",dvList3);
                ...
         */

       Map<String,List<DicValue>> map = ds.getAll();

        /*Set<String> myset = map.keySet();
        for (String str: myset
             ) {
            System.out.println(str);
            System.out.println(map.get(str));
        }
*/

        //将map解析为上下文域对象中保存的键值对
        Set<String> set = map.keySet();

        for (String key:set
             ) {
            List<DicValue> list = map.get(key);
            application.setAttribute(key,list);
        }

        //---------------------------------

        //数据字典处理完毕后，处理stage2Possibility.properties文件
        /*
            处理stage2Possibility.properties文件步骤
                1.解析该文件，将该属性文件中的键值对关系处理成为java中的键值对关系
                    Map<String(stage),String(possibility)> pMap = ...;

                2.pMap.put("01资质审查","10");

                3.pMap保存后，放在服务器缓存中
                application.setAttribute("pMap",pMap);

         */

        ResourceBundle bundle = ResourceBundle.getBundle("Stage2Possibility");


        Map<String,String> pMap = new HashMap<>();

        Enumeration<String> keys = bundle.getKeys();

        while (keys.hasMoreElements()){
            String stage = keys.nextElement();
            String possibility = bundle.getString(stage);
            pMap.put(stage,possibility);
        }

        application.setAttribute("pMap",pMap);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("上下文销毁了");
    }
}
