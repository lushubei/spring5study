package com.xb.vip.spring.mvcframework.webmvc;

import com.xb.vip.spring.mvcframework.annotation.XBRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 原生Spring的HandlerAdapter主要完成请求传递到服务端的参数列表与Method实参列表的对应关系，
 * 完成参数值的类型转换工作.
 * 核心方法是handle()
 */
//专人干专事
public class XBHandlerAdapter {

    public boolean supports(Object handler) {
        return (handler instanceof XBHandlerMapping);
    }

    public XBModeAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {

        XBHandlerMapping handlerMapping = (XBHandlerMapping) handler;

        //每个方法有一个参数列表，这里保存的是形参列表
        Map<String, Integer> paramMapping = new HashMap<String, Integer>();

        //这里只是给出命名参数
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof XBRequestParam) {
                    String paramName = ((XBRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramMapping.put(paramName, i);
                    }
                }

            }
        }

        //根据用户请求的参数信息，跟Method中的参数信息进行动态匹配
        //resp穿金路的目的只有一个：将其赋值给方法参数，仅此而已

        //只有当用户传过来的ModelAndView为空的时候，才会新建一个默认的

        //1. 要准备好这个方法的形参列表
        //方法重载时形参的决定因素：参数的个数、参数的类型、参数的顺序、方法的名字
        //只处理Request和Response
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type == HttpServletRequest.class ||
                    type == HttpServletResponse.class) {
                paramMapping.put(type.getName(), i);
            }
        }

        //2. 得到自定义命名参数所在的位置
        //用户通过URL传过来的参数列表
        Map<String, String[]> reqParameterMap = req.getParameterMap();

        //3. 构造实参列表
        Object[] paramValues = new Object[paramTypes.length];
        for (Map.Entry<String, String[]> param : reqParameterMap.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", "");

            if (!paramMapping.containsKey(param.getKey())) {
                continue;
            }

            int index = paramMapping.get(param.getKey());

            //因为页面传过来的值都是String类型的，而在方法中定义的类型是千变万化的
            //所以要针对我们传过来的参数进行类型转换
            paramValues[index] = caseStringValue(value, paramTypes[index]);
        }

        if (paramMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (paramMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        //4. 从handler中取出Controller、Method,然后利用反射机制进行调用
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);

        if (result == null) {
            return null;
        }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == XBModeAndView.class;
        if (isModelAndView) {
            return (XBModeAndView) result;
        } else {
            return null;
        }
    }

    private Object caseStringValue(String value, Class<?> clazz) {

        if (clazz == String.class) {
            return value;
        } else if (clazz == Integer.class) {
            return Integer.valueOf(value);
        } else if (clazz == int.class) {
            return Integer.valueOf(value).intValue();
        } else {
            return null;
        }
    }
}
