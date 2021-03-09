package com.nowcoder.community.service;


import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
// @Scope("prototype") // 默认为single(bean在容器管理过程只实例化一次），改成prototype后每次访问bean都会生成一个新的实例)

// 用容器管理bean的初始化
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao; // 处理查询业务时调用

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }
    @PostConstruct // 方法会在构造器之后调用
    public void init(){ // 初始化方法
        System.out.println("初始化AlphaService");
    }

    @PreDestroy // 方法会在销毁对象前调用
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }
}
