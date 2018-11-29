package org.st.framework.gb28181.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.st.framework.gb28181.listener.annotation.MethodTarget;
import org.st.framework.gb28181.listener.handlers.request.RequestMethodHandlerAdapter;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static Map<String,RequestMethodHandlerAdapter> map = new HashMap<String,RequestMethodHandlerAdapter>();
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, Object> annotations = applicationContext.getBeansWithAnnotation(MethodTarget.class);
        /**
         * 初始化所有事件处理类
         */
        for (Map.Entry<String, Object> entry : annotations.entrySet()) {
            Annotation annotation = AnnotationUtils.findAnnotation(entry.getValue().getClass(),MethodTarget.class);
            Object name = AnnotationUtils.getValue(annotation,"name");
            Object cSeq = AnnotationUtils.getValue(annotation,"cSeq");
            String key = name +"_"+cSeq;
            map.put((String) name, ((RequestMethodHandlerAdapter) entry.getValue()));
        }
    }

    public static <T> T getBean(Class<T> clazz){
        try {
            return applicationContext.getBean(clazz);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RequestMethodHandlerAdapter methodsAdapter(String methodName, int cSeq){
        String key = methodName+"_"+cSeq;
        return map.get(methodName);
    }
}
