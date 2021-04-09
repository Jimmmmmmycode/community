package com.nowcoder.community.annotation;

/***
 * 描述该方法是否需要登录才能访问
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 该注解应用在方法上
@Retention(RetentionPolicy.RUNTIME) // 该注解运行时有效
public @interface LoginRequired {

}
