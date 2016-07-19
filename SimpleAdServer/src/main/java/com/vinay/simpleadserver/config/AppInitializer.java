/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinay.simpleadserver.config;

import com.vinay.simpleadserver.cache.AdServerInMemoryCache;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Class to initialize the web application
 *
 * @author vchaitankar
 */
public class AppInitializer implements WebApplicationInitializer {

    /**
     * This method is used to initialize the web application. Dispatcher servlet
     * and InMemory Cache are defined and initialized.
     *
     * @param servletContext ServletContext for the web application
     * @throws ServletException
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext context = getContext();
        servletContext.addListener(new ContextLoaderListener(context));
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        AdServerInMemoryCache<String, String> cache = new AdServerInMemoryCache<>(600, 1000);
        servletContext.setAttribute("cache", cache);
    }

    /**
     *
     * @return This method returns the web application context
     */
    private AnnotationConfigWebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("com.vinay.simpleadserver.config");
        return context;
    }

}
