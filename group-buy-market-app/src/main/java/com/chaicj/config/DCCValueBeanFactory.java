package com.chaicj.config;

import com.chaicj.types.annotations.DCCValue;
import com.chaicj.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DCCValueBeanFactory implements BeanPostProcessor {

    private static final String BASE_CONFIG_PATH = "group_buy_market_dcc_";

    private final RedissonClient redissonClient;

    private final Map<String, Object> dccObjGroup = new HashMap<>();

    public DCCValueBeanFactory(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Bean("dccTopic")
    public RTopic dccRedisTopicListener() {
        RTopic topic = redissonClient.getTopic("group_buy_market_dcc");
        topic.addListener(String.class, (charSequence, s) -> {
            String[] split = s.split(Constants.SPLIT);
            // 获取值
            String attribute = split[0];
            String key = BASE_CONFIG_PATH.concat(attribute);
            String value = split[1];

            // 设置值
            RBucket<String> bucket = redissonClient.getBucket(key);
            boolean exists = bucket.isExists();
            if (!exists) return;

            Object targetBean = dccObjGroup.get(key);
            if (targetBean == null) return;

            Class<?> targetBeanClass = targetBean.getClass();
            if (AopUtils.isAopProxy(targetBean)) {
                targetBeanClass = AopUtils.getTargetClass(targetBean);
            }

            try {
                Field declaredField = targetBeanClass.getDeclaredField(attribute);
                declaredField.setAccessible(true);
                declaredField.set(targetBean, value);
                declaredField.setAccessible(false);
                log.info("DCC 节点监听，动态设置值 {} {}", key, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return topic;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 注意；增加 AOP 代理后，获得类的方式要通过 AopProxyUtils.getTargetClass(bean); 不能直接 bean.class 因为代理后类的结构发生变化，这样不能获得到自己的自定义注解了。
        Class<?> targetClass = bean.getClass();
        Object targetBean = bean;
        if (AopUtils.isAopProxy(bean)) {
            targetClass = AopUtils.getTargetClass(bean);
            targetBean = AopProxyUtils.getSingletonTarget(bean);
        }
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (!field.isAnnotationPresent(DCCValue.class)) continue;
            DCCValue dccValue = field.getAnnotation(DCCValue.class);
            String value = dccValue.value();
            if (StringUtils.isBlank(value)) {
                throw new RuntimeException(field.getName() + " @DCCValue is not config value config case 「isSwitch/isSwitch:1」");
            }
            String[] splits = value.split(":");
            String key = BASE_CONFIG_PATH.concat(splits[0]);
            String defaultValue = splits.length == 2 ? splits[1] : "";

            // 设置值
            String setValue = defaultValue;
            try {
                RBucket<String> bucket = redissonClient.getBucket(key);
                boolean exists = bucket.isExists();
                if (!exists) {
                    bucket.set(defaultValue);
                } else {
                    setValue = bucket.get();
                }
                field.setAccessible(true);
                field.set(targetBean, setValue);
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            dccObjGroup.put(key, targetBean);
        }
        return bean;
    }
}
