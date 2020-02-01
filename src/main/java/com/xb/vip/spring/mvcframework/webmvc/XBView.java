package com.xb.vip.spring.mvcframework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义模板借新引擎
 * 核心方法是render()
 */
public class XBView {

    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public XBView(File viewFile) {
        this.viewFile = viewFile;
    }

    public String getContentType(){
        return DEFAULT_CONTENT_TYPE;
    }

    public void render(Map<String,?> model, HttpServletRequest req, HttpServletResponse resp) throws  Exception{

        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");

        try {
            String line = null;
            while (null != (line = ra.readLine())){
                line = new String(line.getBytes("ISO-8859-1"),"utf-8");
                Pattern pattern = Pattern.compile("¥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()){

                    String paramName = matcher.group();
                    paramName = paramName.replaceAll("¥\\{|\\}","");
                    Object paramValue = model.get(paramName);
                    if (null == paramValue){continue;}
                    //要把¥{}中间的这个字符串取出来
                    line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);

                }
                sb.append(line);
            }
        } finally {
            ra.close();
        }

        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(sb.toString());

    }

    //处理特殊字符
    private String makeStringForRegExp(String str) {

        return str.replace("\\","\\\\").replace("*","\\")
                .replace("+","\\+").replace("|","\\|")
                .replace("{","\\}").replace("}","\\}")
                .replace("(","\\(").replace(")","\\)")
                .replace("^","\\^").replace("$","\\$")
                .replace("[","\\[").replace("]","\\]")
                .replace("?","\\?").replace(",","\\,")
                .replace(".","\\.").replace("&","&");
    }
}
