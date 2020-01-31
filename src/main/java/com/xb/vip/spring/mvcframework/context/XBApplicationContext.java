package com.xb.vip.spring.mvcframework.context;

import com.xb.vip.spring.mvcframework.annotation.XBAutowired;
import com.xb.vip.spring.mvcframework.annotation.XBController;
import com.xb.vip.spring.mvcframework.annotation.XBService;
import com.xb.vip.spring.mvcframework.beans.XBBeanWrapper;
import com.xb.vip.spring.mvcframework.beans.config.XBBeanDefinition;
import com.xb.vip.spring.mvcframework.beans.config.XBBeanPostProcessor;
import com.xb.vip.spring.mvcframework.beans.suport.XBBeanDefinitionReader;
import com.xb.vip.spring.mvcframework.beans.suport.XBDefaultListableBeanFactory;
import com.xb.vip.spring.mvcframework.core.XBBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class XBApplicationContext extends XBDefaultListableBeanFactory implements XBBeanFactory{

    private String[] configLoactions;
    private XBBeanDefinitionReader reader;

    //单例IOC容器缓存
    //用来保证注册是单例的容器
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<String,Object>();

    //通用的IOC容器
    //用来存储所有的被代理过的对象
    private Map<String,XBBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, XBBeanWrapper>();


    public XBApplicationContext(String... configLoactions) {
        this.configLoactions = configLoactions;

        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh()throws Exception{
        //1. 定位，定位配置文件
        reader = new XBBeanDefinitionReader(this.configLoactions);

        //2. 加载，加载配置文件，扫描相关的类，把他们封装成BeanDefinition
        List<XBBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3. 注册，把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);

        //4. 初始化，把不是延时加载的类提前初始化
        doAutowrited();

    }

    private void doAutowrited() {
        /**
         * 只处理非延时加载的情况
         */
        for(Map.Entry<String,XBBeanDefinition> beanDefinitionEntry: super.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<XBBeanDefinition> beanDefinitions) throws Exception{
        for (XBBeanDefinition beanDefinition: beanDefinitions) {
            if(super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The \"" + beanDefinition.getFactoryBeanName() + "\" is exists!!");
            }

            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }

        log.info("容器初始化完毕");
    }


    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }


    //依赖注入，从这里开始，读取BeanDefinition中的信息
    //然后通过反射机制创建一个实例并返回
    //Spring的做法是，不会把原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1. 保留原来的OOP关系
    //2. 需要对它进行扩展、增强（为了以后的AOP打基础）
    public Object getBean(String beanName) throws Exception {
        XBBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);

        try {
            //生产通知事件
            XBBeanPostProcessor beanPostProcessor = new XBBeanPostProcessor();

            Object instance = instantiateBean(beanDefinition);
            if(null == instance){return null;}

            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

            XBBeanWrapper beanWrapper = new XBBeanWrapper(instance);

            this.factoryBeanInstanceCache.put(beanName,beanWrapper);

            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);

            //根据配置，处理DI依赖
            populateBean(beanName, instance);

            //通过这样调用，相当于给我们自己留有了可操作的空间
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void populateBean(String beanName, Object instance) {

        Class clazz = instance.getClass();

        if(!(clazz.isAnnotationPresent(XBController.class) ||
             clazz.isAnnotationPresent(XBService.class))){
            return;
        }

        Field [] fields = clazz.getDeclaredFields();

        for (Field field: fields) {
            if(!field.isAnnotationPresent(XBAutowired.class)){continue;}

            XBAutowired autowired = field.getAnnotation(XBAutowired.class);

            String autowiredBeanName = autowired.value().trim();

            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);

            try {
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    //传一个BeanDefinition,就返回一个实例Bean
    private Object instantiateBean(XBBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();

        try {
            //因为根据Class才能确定一个类是否有实例
            if(this.factoryBeanInstanceCache.containsKey(className)){
                instance = this.factoryBeanInstanceCache.get(className);
            }else{
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfi(){
        return this.reader.getConfig();
    }

}