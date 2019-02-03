package com.ibm.services.epricer.env;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import com.ibm.services.epricer.framework.rest.DataBean;
import com.ibm.services.epricer.framework.rest.Helper;

public class Env extends DataBean implements EnvConstant {
	
	
	private static Env env;
	
	private Env() {
		super();
	}
	
	public static Env getEnv () {
		env=null;
		return createEnv();
		/*if(env==null) {
			return createEnv();
		}
		else {
			return env;
		}*/
	}

	private synchronized static Env createEnv() {
		// TODO Auto-generated method stub
		if(env!=null) {
			return env;
		}
		else {
			// read the environment variables
			String envName = System.getenv("env.name");
			String envPath = System.getenv("env.config.path");
			
			// if not found (for local) read the environment properties
			envName = (envName == null) ? System.getProperty("env.name") : envName;
			envPath = (envPath == null) ? System.getProperty("env.config.path") : envPath;

			System.out.println("envName = "+envName);
			System.out.println("envPath = "+envPath);
			env = buildEnv(envName,envPath);			
			return env;
		}
	}

	private static Env buildEnv(String envName, String envPath) {
		
		Env env = new Env();
		// TODO Auto-generated method stub
		String envJson = Helper.readAllFromFile(envPath);
		DataBean envConfigs = Helper.parseJSON(envJson);
		DataBean envDataBean = envConfigs.getStructure(envName);
		//build the common config
		DataBean generalConfig = new DataBean();
		Enumeration<String> colNames = envDataBean.getColumnNames();
		while(colNames.hasMoreElements()) {
			String col = colNames.nextElement();
			generalConfig.setValue(col, envDataBean.getString(col));
		}
		env.setStructure(GEN_CONFIG, generalConfig);
		
		//next build the collections
		HashMap<String, ArrayList<DataBean>> configCollectionMap = envDataBean.getCollections();
		
		Iterator<String> keys = configCollectionMap.keySet().iterator();
		while(keys.hasNext()) {
			String keyName = keys.next();
			ArrayList<DataBean> listVal = configCollectionMap.get(keyName);
			env.setCollection(keyName, listVal);
		}
		
		HashMap<String, DataBean>  structMap = envDataBean.getStructures();
		Iterator<String> structKeys = structMap.keySet().iterator();
		while(structKeys.hasNext()) {
			String key = structKeys.next();
			env.setStructure(key, structMap.get(key));
		}
		
		return env;
	}
	
	public void getLogConfigByModule(String module) {
		module = module!=null? module : "";
		LogEnv logEnv =(LogEnv) env.getObject("logenv_"+module);
		if (logEnv==null) {
			loadLogConfigByModule();
			logEnv =(LogEnv) env.getObject("logenv_"+module);
		}
		
	}
	
	private void loadLogConfigByModule() {
		
		ArrayList<DataBean> logs = env.getCollection(LOG);
		for(DataBean log : logs) {
				LogEnv logEnv = LogEnv.getLogEnv();
				logEnv.setJmslogappender(log.getBoolean("jmslogappender"));
				logEnv.setFilelogappender(log.getBoolean("filelogappender"));
				logEnv.setJmsLog4jConfig(log.getString("jmslog4jconfig"));
				logEnv.setFileLog4jConfig(log.getString("filelog4jconfig"));
				logEnv.setTargetJmsConnectionJndi(log.getString("targetjmsconnectionjndi"));
				logEnv.setTargetMq(log.getString("targetmq"));
				logEnv.setModule(log.getString("module"));
				env.setObject("logenv_"+log.getString("module"), logEnv);
			
		}
		//return null;
	}
	
	public DataBean getMQConfigByInterfaceName(String interfaceName) {
		interfaceName = interfaceName!=null ? interfaceName : "";
		ArrayList<DataBean> mqList = env.getCollection(MQ);
		for(DataBean mqConfig : mqList) {
			if(interfaceName.equalsIgnoreCase(mqConfig.getString(INTERFACE_NAME))) {
				return mqConfig;
			}
		}
		
		return null;
	}
	
	public DataBean getWSConsumesConfigByInterface(String interfaceName) {
		ArrayList<DataBean> wsList = env.getCollection(WS_CONSUMES);
		for(DataBean wsConfig : wsList) {
			if(interfaceName.equalsIgnoreCase(wsConfig.getString(INTERFACE_NAME))) {
				return wsConfig;
			}
		}
		return null;		
	}
	
	public DataBean getWSProviderConfigByWSNameAndClientID(String webserviceName, String clientId) {
		ArrayList<DataBean> wsList = env.getCollection(WS_PROVIDES);
		for (DataBean wsConfig : wsList) {
			if(webserviceName.equalsIgnoreCase(wsConfig.getString(WS_NAME))) {
				ArrayList<DataBean> clients = wsConfig.getCollection(WS_CLIENTS);
				for(DataBean client : clients) {
					if(clientId.equalsIgnoreCase(client.getString(WS_CLIENT_ID))) {
						return client;
					}
				}
			}
		}
		return null;
	}
	
	public DataBean getStructureByName(String name) {
		return env.getStructure(name);
	}
	
	public String getValueByName(DataBean dataBean, String name) {
		return dataBean.getString(name);
	}

}
