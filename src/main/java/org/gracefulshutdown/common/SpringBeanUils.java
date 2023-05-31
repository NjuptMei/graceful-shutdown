package org.gracefulshutdown.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class SpringBeanUils implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SpringBeanUils.class);

    private static ApplicationContext applicationContext;
    public static <T> Map<String, Object> beanConvertMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        try {
            BeanMap beanMap = BeanMap.create(bean);
            beanMap.forEach((key, value) -> {
                if (!Objects.isNull(value)) {
                    map.put(key.toString(), value);
                }
            });
        } catch (IllegalArgumentException ee) {
            logger.error("beanConvertMap失败：", ee);
        }
        return map;
    }

    public static Object getBeanByClassName(String className) throws Exception{
        Object obj = null;
        try {
            obj = applicationContext.getBean(className);
        } catch (BeansException ee) {
            logger.error("通过className获取bean文件失败");
            throw new Exception(ee);
        }
        return obj;
    }

    public static <T> T getBeanByClass(Class<T> clazz) {
        T bean;
        try {
            bean = applicationContext.getBean(clazz);
        } catch (BeansException ee) {
            logger.error("通过className获取bean文件失败");
            return null;
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUils.applicationContext = applicationContext;
    }
}
