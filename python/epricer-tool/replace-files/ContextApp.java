package com.ibm.services.epricer.framework.utils;

import org.springframework.context.ApplicationContext;

import com.ibm.services.epricer.env.Env;
import com.ibm.services.epricer.framework.log.Console;
import com.ibm.services.epricer.param.SolrQueryBean;
import com.ibm.services.patterns.callback.CallBackRegistry;

import java.io.File;

public class ContextApp {
	private static ContextApp instance;
	private static ApplicationContext applicationContext;
	
	private static Env envConfig;
	private static SolrQueryBean solrQueryBean;
	
	private static CallBackRegistry registry;
//	private long lastCreateEnvTs;
//	private int update_intvl_sec=1000;
	private long lastModifyEnvTime;
	

    protected ContextApp() {
    }
    
    public static ContextApp getInstance() {
        if(instance == null) {
            instance = new ContextApp();
        }
        return instance;
    }
    
    public static Object getBean(String id) {
        return getInstance().getApplicationContext().getBean(id);
    }
    
    public  ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }
    
    public static void setEnvConfig(Env inEnvConfig) {
    	envConfig = inEnvConfig;
    }
    
    public Env getEnvConfig() {
    	/*if ((currTs - lastCreateEnvTs)>update_intvl_sec*1000){
    		Console.debug(ContextApp.class.getName(), "Interval for reading env-config reached, rereading ...");
    		envConfig = Env.getEnv();
    		lastCreateEnvTs=System.currentTimeMillis();
    	}*/

    	synchronized ("abc"){
			String envPath = System.getProperty("env.config.path").replaceAll("\\\\","/");
			long modifyDatetime=new File(envPath).lastModified();
			if (lastModifyEnvTime!=modifyDatetime){
				lastModifyEnvTime=modifyDatetime;
				Console.debug(ContextApp.class.getName(), "Env-config.json was changed, rereading ...");
				envConfig = Env.getEnv();
			}

			return envConfig;
		}

    }

	public SolrQueryBean getSolrQueryBean() {
		return solrQueryBean;
	}

	public static void setSolrQueryBean(SolrQueryBean inSolrQueryBean) {
		solrQueryBean = inSolrQueryBean;
	}
	
	public CallBackRegistry getCallBackRegistry() {
		return registry;
	}

	public static void setCallbackRegistry(CallBackRegistry inRegistry) {
		registry = inRegistry;
	}
}
