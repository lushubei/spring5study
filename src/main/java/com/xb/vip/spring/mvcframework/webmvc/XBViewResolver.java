package com.xb.vip.spring.mvcframework.webmvc;

import java.io.File;
import java.util.Locale;

/**
 * 原生Spring中的XBViewResolver主要完成模板名称和模板解析引擎的匹配。
 * 通过在Servlet中调用resolveViewName()方法来获得模板所对应的View.
 */

//设计这个类的主要目的是：
//1. 将一个静态文件变为一个冬天文件
//2. 根据用户传送不同的参数，产生不同的结果
//最终输出字符串，交给Response输出
public class XBViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;
    private String viewName;

    public XBViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        this.templateRootDir = new File(templateRootPath);
    }

    public XBView resolveViewName(String viewName, Locale locale) throws Exception{
        this.viewName = viewName;
        if(null==viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);

        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new XBView(templateFile);
    }
}
