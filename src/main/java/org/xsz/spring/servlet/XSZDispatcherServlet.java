package org.xsz.spring.servlet;



import org.xsz.spring.Annotation.XSZAutowired;
import org.xsz.spring.Annotation.XSZController;
import org.xsz.spring.Annotation.XSZService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class XSZDispatcherServlet extends HttpServlet{//javax.servlet

    // 跟web.xml param-name一致
    private static final String LOCATION = "contextConfigLocation";

    // 保存配置信息
    private Properties properties = new Properties();

    //扫到的类名
    private List<String> classNames = new ArrayList<>();

    //IOC容器，保存bean
    private Map<String, Object> beanMap = new HashMap<>();

    public XSZDispatcherServlet(){
        super();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 加载配置文件
        doLoadConfig(config.getInitParameter(LOCATION));
        // 扫描相关类
        doScanner(properties.getProperty("sacnPackage"));
        // 初始化
        try {
            doInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        // 依赖注入
        doAutowired();
    }

    private void doAutowired() {
        for(Map.Entry<String, Object> entry: beanMap.entrySet()){

            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields){
                if(!field.isAnnotationPresent(XSZAutowired.class)) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), beanMap.get(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(classNames.isEmpty()){
            throw new RuntimeException("未扫到类");
        }
        for(String className : classNames){
            Class<?> aClass = Class.forName(className);
            Class<XSZService> clazzService = XSZService.class;
            Class<XSZController> clazzController = XSZController.class;
            if(aClass.isAnnotationPresent(clazzService)){
                Class<?>[] interfaces = aClass.getInterfaces();
                for(Class c : interfaces){
                    beanMap.put(toLowerFirstCase(c.getSimpleName()),aClass.newInstance());
                }

            }else if(aClass.isAnnotationPresent(clazzController)){
                beanMap.put(toLowerFirstCase(aClass.getSimpleName()), aClass.newInstance());
            }else{
                continue;
            }

        }

    }

    private void doScanner(String sacnPackage) {
        URL url =  this.getClass().getClassLoader().getResource("/"+ sacnPackage.replaceAll("\\.", "/"));
        File dirFile = new File(url.getFile());
        for (File file : dirFile.listFiles()){
            if(file.isDirectory()){
                doScanner(sacnPackage+"."+file.getName());
            }else{
                classNames.add(sacnPackage+ "." + file.getName().replaceAll(".class", "").trim());
            }
        }


    }

    private void doLoadConfig(String initParameter) {
        InputStream inputStream = null;
        inputStream = this.getClass().getClassLoader().getResourceAsStream(initParameter);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
                try {
                    if(inputStream != null){inputStream.close();}
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    private String toLowerFirstCase(String param){
        return param
                .substring(0,1)
                .toLowerCase()
                .concat(param.substring(1));
    }
}
