package com.nowcoder.community.config;


import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean  // Bean注解,该Bean一定会被Spring容器管理
    // 配置就是实例化Kptcha接口
    public Producer kaptchaProducer(){
        Properties properties = new Properties() ;//  封装properties文件中的数据
        // 设置验证码图片参数
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32");
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0"); // 验证码字体颜色为黑色
        properties.setProperty("kaptcha.textproducer.char.string","1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"); // 生成字符集的范围
        properties.setProperty("kaptcha.textproducer.char.length","4"); // 生成字符的个数
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise"); // 不需要加噪声
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties); // 所有配置项都是config去配的,而config需要依赖Properties对象
        kaptcha.setConfig(config);  // 用config去配置kaptcha
        return kaptcha; // 返回Producer默认的实现类的对象，该对象在bean注解的作用下会自动装配到Spring容器
    }
    // 通过容器可得到Producer实例,通过实例中两个方法可得到


}
