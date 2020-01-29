package com.xb.vip.spring.mvcframework.beans.suport;

import com.xb.vip.spring.mvcframework.beans.config.XBBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对配置文件进行 查找、读取、解析
 * 主要完成对application.properties配置文件的解析工作
 * 通过构造方法获取从ApplicationContext传过来的locations配置文件路径，然后解析，扫描并保持所有相关的类并提供统一的访问入口。
 */
public class XBBeanDefinitionReader {
    private List<String> registBeanClasses = new ArrayList<String>();
    private  Properties config = new Properties();

    //固定配置文件中的key,相对于XML的规范
    private final String SCAN_PACKAGE = "scanPackage";

    public XBBeanDefinitionReader(String... locations) {

        //通过URL定位找到其所对应的的文件，然后转换为文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0]
                .replace("classpath:",""));

        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        //转换为文件路径，实际就是把.替换为/
        URL url = this.getClass().getClassLoader().getResource("/"+ scanPackage.replaceAll("\\.","/"));

        File classPath = new File(url.getFile());
        for (File file: classPath.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage + '.' + file.getName());
            }else{
                if(!file.getName().endsWith(".class")){
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class",""));
                registBeanClasses.add(className);
            }
        }
    }

    //把配置文件中扫描到的所有配置信息转换为XBBeanDefinition对象，以便之后的IOC操作
    public List<XBBeanDefinition> loadBeanDefinitions(){
        List<XBBeanDefinition> result = new ArrayList<XBBeanDefinition>();
        for (String className: registBeanClasses) {
            try {
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()){continue;}

                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 把每一个配置信息解析成一个BeanDefinitionvvbhhj
     * @param factoryBeanName
     * @param beanClassName
     * @return
     */
    private XBBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        XBBeanDefinition beanDefinition = new XBBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    /**
     * 类名首字母改为小写
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();

        //因为大小写字母的ASCII码相差32
        //而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算数运算，实际上就是对ASCII码做算数运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig(){
        return config;
    }
}
