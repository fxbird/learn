package com.didispace.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RefreshScope
@RestController

public class TestController{
    @Value("${from}")
    private String from;

    @Autowired
    private Environment env;

    @RequestMapping("/from")
    public String from(){
        return this.from;
    }

    @RequestMapping(value = "/getEnv", method = RequestMethod.GET)
    public String getEnv(HttpServletRequest request){
        return env.getProperty(request.getParameter("name"));
    }
}