package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes=CommunityApplication.class)  // 加上注解后Test中运行的代码以CommunityApplication为配置类
class CommunityApplicationTests implements ApplicationContextAware { // 实现ApplicationContextAware接口 - 获取Spring容器

	private ApplicationContext applicationContext ;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException { // ApplicationContext - Spring容器 （ 顶层父接口BeanFactory-Spring容器顶层接口 )
		this.applicationContext = applicationContext; // Spring容器在检测到实现了ApplicationContextAware的bean后把自身传进来
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());
		alphaDao = (AlphaDao)applicationContext.getBean("alphaHibernate");
		System.out.println(alphaDao.select());
	}

	@Test
	public void testBeanManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
		 alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()) );
	}

	@Autowired // 依赖注入 (给当前bean注入alphaDao)
	@Qualifier("alphaHibernate")  // 注入相应bean的名字

	private AlphaDao alphaDao; // Spring容器把AlphaDao注入给alphaDao成员属性
	// AlphaDao - bean依赖于接口 降低耦合度
	@Autowired
	private AlphaService alphaservice ;

	@Autowired
	private SimpleDateFormat simpleDateFormat ;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaservice);
		System.out.println(simpleDateFormat);
	}

  }
