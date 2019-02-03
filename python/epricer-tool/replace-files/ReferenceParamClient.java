package com.ibm.services.epricer.param;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.ibm.services.epricer.env.Env;
import com.ibm.services.epricer.env.EnvConstant;
import com.ibm.services.epricer.framework.exceptions.ServiceException;
import com.ibm.services.epricer.framework.log.Constants;
import com.ibm.services.epricer.framework.log.PropertyFileLoader;
import com.ibm.services.epricer.framework.rest.DataBean;
import com.ibm.services.epricer.framework.rest.Helper;
import com.ibm.services.epricer.framework.service.EpricerServiceBase;

public class ReferenceParamClient extends EpricerServiceBase implements ReferenceParamAPI {

	public static HashMap<String, DataBean> solrCache = new HashMap<String, DataBean>();
	private boolean isLocal = true;
	private String[] excludedQueryArr = { "get_bp_redirection_data_by_contextid" };
	private List<String> excludedQueryList = Arrays.asList(excludedQueryArr);

	@Override
	public void write(String contentType, String id, String collection, DataBean data) throws ServiceException {
		
		String solrUrl = getSolrConnectionURL();
		try {
			write(solrUrl, contentType, id, collection, data);
		} catch (Exception ex) {
			throw new ServiceException(this, "");
		}
	}

	@Override
	public DataBean readOne(String collectionName, String docName, String queryId, HashMap<String, String> paramMap) throws ServiceException {
		return read(collectionName, docName, queryId, paramMap, 1);
	}

	@Override
	public DataBean read(String collectionName, String docName, String queryId, HashMap<String, String> paramMap) throws ServiceException {
		return read(collectionName, docName, queryId, paramMap, 2000);
	}

	private DataBean read(String collectionName, String docName, String queryId, HashMap<String, String> paramMap, int count) throws ServiceException {
		DataBean result = new DataBean();
		Env config = getServiceContext();
		String solrClient = config.getStructureByName(EnvConstant.GEN_CONFIG).getString("solr_client_type");
		if (solrClient != null && "H".equals(solrClient)) {
			HttpGet get = null;
			HttpClient client = null;
			SolrQueryBean queryBean = getSolrQueryBean();
			String queryString = queryBean.getQueryString(collectionName, docName, queryId);
			queryString = SolrQueryBean.replaceDynamicParams(queryString, paramMap);
			queryString = queryString.replaceAll(" ", "+");
			String url = config.getStructureByName(EnvConstant.GEN_CONFIG).getString("failover_solr_url");
			StringBuffer queryURL = new StringBuffer(url);
			queryURL.append("/solr/");
			queryURL.append(collectionName);
			queryURL.append("/select:?wt=json");
			queryURL.append("&rows=" + count);
			queryURL.append("&q=");
			queryURL.append(queryString);

			String key = queryURL.toString();
			if (isLocal && !excludedQueryList.contains(queryId) && solrCache.containsKey(key)) {
				getLogger().debug("Read cache for " + key);
				// System.out.println("Read cache for "+key);
				return solrCache.get(key);
			}

			// Console.debug(this,"queryURL" + queryURL);
			try {
				client = new DefaultHttpClient();
				get = new HttpGet(queryURL.toString());
				HttpResponse response = client.execute(get);
				if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
					throw new ServiceException(this, response.getStatusLine().getStatusCode() + "", response.getStatusLine().toString());
				}
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer json = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					json.append(line);
				}
				DataBean queryResult = Helper.parseJSON(json.toString());
				ArrayList<DataBean> docList = queryResult.getStructure("response").getCollection("docs");
				DataBean data;
				if (docList != null) {
					for (DataBean doc : docList) {
						Enumeration<String> cols = doc.getColumnNames();
						data = new DataBean();
						while (cols.hasMoreElements()) {
							String tKey = cols.nextElement();
							String newVal = doc.getString(tKey);
							if (newVal.startsWith("JSCCOLLECTION:")) {
								newVal = SolrHelper.decodeString((newVal.substring("JSCCOLLECTION:".length())));
								// newVal =
								// String.valueOf(Base64.decodeBase64((newVal.substring("JSCCOLLECTION:".length()).getBytes())));
								data.setCollection(tKey, SolrHelper.parseJsonArray(newVal));
							} else if (newVal.startsWith("JSCSTRUCTURE:")) {
								newVal = SolrHelper.decodeString((newVal.substring("JSCSTRUCTURE:".length())));
								// newVal =
								// String.valueOf(Base64.decodeBase64((newVal.substring("JSCSTRUCTURE:".length()).getBytes())));
								data.setStructure(tKey, SolrHelper.parseJSON(newVal));
							} else {
								data.setValue(tKey, newVal);
							}

						}
						result.addToCollection("contents", data);
						result.setValue("status", 1);
					}
				}

			} catch (Exception ex) {
				// System.out.println("can't read from solr.. check exception
				// below");
				ex.printStackTrace();
				if (ex instanceof ServiceException) {
					ex.printStackTrace();
					throw (ServiceException) ex;
				}
			} finally {
				if (client != null) {
					client = null;
				}
				if (get != null) {
					// get.releaseConnection();
				}
			}

			solrCache.put(key, result);

			System.out.println("Successfully read data from solr for query id : " + queryId);
			return result;
		}

		CloudSolrClient solrServer = null;
		String solrUrl = getSolrConnectionURL();
		try {
			SolrQueryBean queryBean = getSolrQueryBean();
			String queryString = queryBean.getQueryString(collectionName, docName, queryId);
			queryString = SolrQueryBean.replaceDynamicParams(queryString, paramMap);
			// System.out.println("Trying to create the client with ssl off");
			// CloseableHttpClient client =
			// HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
			// HttpClient client = HttpClientBuilder.create().build();
			solrServer = new CloudSolrClient(solrUrl);
			// System.out.println("Client created successfully");
			// solrServer = new CloudSolrClient(solrUrl);
			solrServer.setDefaultCollection(collectionName);
			ModifiableSolrParams qparams = new ModifiableSolrParams();
			qparams.set("rows", queryBean.getResultSize());
			qparams.set("q", queryString);
			QueryResponse response = solrServer.query(qparams);

			SolrDocumentList docList = response.getResults();
			int resultSize = docList.size();
			long totalCount = docList.getNumFound();
			// TODO: Not a valid use case in our case... but for a safety will
			// update the code accordingly to fetch the rest of the rows and
			// append it into the result Databean
			if (totalCount > resultSize) {
				// we need to fetch rest
			}

			result.setValue("count", resultSize);

			SolrDocument tEntry;
			DataBean dataBean;
			for (int i = 0; i < resultSize; i++) {

				tEntry = docList.get(i);
				Collection<String> cols = tEntry.getFieldNames();

				dataBean = new DataBean();
				for (String tKey : cols) {
					if (tEntry.getFieldValue(tKey) != null) {
						String newVal = String.valueOf(tEntry.getFieldValue(tKey)).trim();
						if (newVal.startsWith("JSCCOLLECTION:")) {
							newVal = SolrHelper.decodeString((newVal.substring("JSCCOLLECTION:".length())));
							// newVal =
							// String.valueOf(Base64.decodeBase64((newVal.substring("JSCCOLLECTION:".length()).getBytes())));
							dataBean.setCollection(tKey, SolrHelper.parseJsonArray(newVal));
						} else if (newVal.startsWith("JSCSTRUCTURE:")) {
							newVal = SolrHelper.decodeString((newVal.substring("JSCSTRUCTURE:".length())));
							// newVal =
							// String.valueOf(Base64.decodeBase64((newVal.substring("JSCSTRUCTURE:".length()).getBytes())));
							dataBean.setStructure(tKey, SolrHelper.parseJSON(newVal));
						} else {
							dataBean.setValue(tKey, newVal);
						}

					}
				}
				result.addToCollection("contents", dataBean);
				result.setValue("status", 1);
			}
		} catch (Exception ex) {
			result.setValue("status", 0);
			result.setValue("errormsg", ex.getMessage());
		}
		return result;
	}

	@Override
	public void delete(String collectionName, String docName, String queryId, HashMap<String, String> paramMap) throws ServiceException {
		
		Env config = getServiceContext();
		String solrClient = config.getStructureByName(EnvConstant.GEN_CONFIG).getString("solr_client_type");
		if (solrClient != null && "H".equals(solrClient)) {
			String url = config.getStructureByName(EnvConstant.GEN_CONFIG).getString("failover_solr_url");
			SolrQueryBean queryBean = getSolrQueryBean();
			String queryString = queryBean.getQueryString(collectionName, docName, queryId);
			queryString = SolrQueryBean.replaceDynamicParams(queryString, paramMap);
			StringBuffer queryURL = new StringBuffer(url);
			queryURL.append("/solr/");
			queryURL.append(collectionName);
			queryURL.append("/update");
			HttpPost post = null;
			HttpClient client = null;
			try {
				post = new HttpPost(queryURL.toString());
				post.setHeader("Content-Type", "application/json");
				StringBuffer postBody = new StringBuffer("{\"delete\":{");
				postBody.append("\"commitWithin\": 1000");
				postBody.append(",\"query\":");
				postBody.append("\"");
				postBody.append(queryString);
				postBody.append("\"");
				postBody.append("");
				postBody.append(" }}");
				String finalBody = postBody.toString();
				post.setEntity(new ByteArrayEntity(finalBody.getBytes("UTF-8")));
				// System.out.println(finalBody);
				client = new DefaultHttpClient();
				HttpResponse response = client.execute(post);
				// System.out.println("HTTP Status code for delete is =
				// "+response.getStatusLine());
				// System.out.println("Successfully deleted data from solr");
				return;
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (client != null) {
					client = null;
				}
				if (post != null) {
					// post.releaseConnection();
				}
			}
		}

		CloudSolrClient solrServer = null;
		String url = getSolrConnectionURL();
		try {

			// System.out.println("Trying to create the client with ssl off");
			// CloseableHttpClient client =
			// HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
			// HttpClient client = HttpClientBuilder.create().build();
			solrServer = new CloudSolrClient(url);
			// System.out.println("Client created successfully");
			SolrQueryBean queryBean = getSolrQueryBean();
			String queryString = queryBean.getQueryString(collectionName, docName, queryId);
			queryString = SolrQueryBean.replaceDynamicParams(queryString, paramMap);
			solrServer.setDefaultCollection(collectionName);
			solrServer.deleteByQuery(collectionName, queryString);
			solrServer.commit();
		} catch (Exception ex) {
			throw new ServiceException(this, ex.getMessage(), ex);
		} finally {
			if (solrServer != null) {
				solrServer.shutdown();
			}
		}
	}

	private String getSolrConnectionURL() {
		Env config = getServiceContext();
		String solrConnectionUrl = config.getStructureByName(EnvConstant.GEN_CONFIG).getString(EnvConstant.SOLR_URL);
		return solrConnectionUrl;
	}

	@Override
	public void write(String connectionUrl, String contentType, String id, String collection, DataBean data) throws ServiceException {
		Env config = getServiceContext();
		String solrClient = config.getStructureByName(EnvConstant.GEN_CONFIG).getString("solr_client_type");
		if (solrClient != null && "H".equals(solrClient)) {
			writeTemp(contentType, id, collection, data);
		}
		else {
			CloudSolrClient solrServer = null;
			try {
				try {
					// System.out.println("Trying to create the client with ssl
					// off");
					// HttpClient client = new DefaultHttpClient();
					// HttpClient client = HttpClientBuilder.create().build();
					solrServer = new CloudSolrClient(connectionUrl);
					// System.out.println("Client created successfully");
				} catch (Exception ex) {
					// System.out.println("Printing the error on IBM JRE -----
					// "+ex.getMessage());
					// HttpClient client = HttpClientBuilder.create().build();
					// solrServer = new CloudSolrClient(connectionUrl, client);
					// System.out.println("Connection is obtained");
					ex.printStackTrace();
					throw ex;
				}
				solrServer.setDefaultCollection(collection);
				// solrServer.deleteByQuery(collection,
				// "contenttype:"+contentType);

				if (data.getCollection("contents") != null) {
					ArrayList<DataBean> itemList = data.getCollection("contents");
					if (itemList != null) {
						Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
						long recordCounter = 0;
						for (int i = 0, size = itemList.size(); i < size; i++) {
							DataBean tEntry = itemList.get(i);
							SolrInputDocument doc = new SolrInputDocument();
							doc.setField("contenttype", contentType);
							if (id != null && id.trim().length() > 0) {
								doc.setField("id", id);
							} else {
								doc.setField("id", hashIDWithPrefix(contentType + recordCounter));
							}

							docs.add(doc);
							recordCounter++;

							Enumeration cols = tEntry.getColumnNames();

							if (cols != null) {
								String tStr;

								while (cols.hasMoreElements()) {
									tStr = cols.nextElement().toString();

									doc.setField(tStr, tEntry.getString(tStr).trim());
								}
							}

							if (tEntry.getCollections() != null) {
								HashMap tMap = tEntry.getCollections();
								Iterator mList = tMap.keySet().iterator();

								while (mList.hasNext()) {
									String mName = mList.next().toString();
									String colStr = SolrHelper.toJsonArray(tEntry.getCollection(mName));
									String encodedColStr = SolrHelper.encodeString(colStr.trim());

									doc.setField(mName, "JSCCOLLECTION:".concat(encodedColStr));
									// (Base64.encodeBase64String(toJsonArray(tEntry.getCollection(mName)).getBytes()));
								}
							}

							if (tEntry.getStructures() != null) {
								HashMap tMap = tEntry.getStructures();
								Iterator mList = tMap.keySet().iterator();

								while (mList.hasNext()) {
									String mName = mList.next().toString();
									String strucStr = SolrHelper.toJson(tEntry.getStructure(mName));
									String encodedStrucStr = SolrHelper.encodeString(strucStr.trim());
									doc.setField(mName, "JSCSTRUCTURE:".concat(encodedStrucStr));
								}
							}
						}

						solrServer.add(docs);

					}

				}
				solrServer.commit();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ServiceException(this, ex.getMessage(), ex);
			} finally {
				if (solrServer != null) {
					// solrServer.close();
					solrServer.shutdown();
				}
			}

		}
	}

	private void writeTemp(String contentType, String globalid, String collection, DataBean data) throws ServiceException {
		Env config = getServiceContext();
		String url = config.getStructureByName(EnvConstant.GEN_CONFIG).getString("failover_solr_url");

		StringBuffer queryURL = new StringBuffer(url);
		queryURL.append("/solr/");
		queryURL.append(collection);
		queryURL.append("/update");

		HttpPost post = null;
		HttpClient client = null;

		if (data.getCollection("contents") != null) {
			ArrayList<DataBean> itemList = data.getCollection("contents");
			if (itemList != null) {
				long recordCounter = 0;
				for (DataBean content : itemList) {
					String id = globalid;
					if (content.getString("id") != null && content.getString("id") != "") {
						id = content.getString("id");
					}

					DataBean doc = new DataBean();
					doc.setValue("contenttype", contentType);
					id = id != null ? id : hashIDWithPrefix(contentType + recordCounter);
					doc.setValue("id", id);

					Enumeration cols = content.getColumnNames();

					if (cols != null) {
						String tStr;

						while (cols.hasMoreElements()) {
							tStr = cols.nextElement().toString();

							doc.setValue(tStr, content.getString(tStr).trim());
						}
					}

					if (content.getCollections() != null) {
						HashMap tMap = content.getCollections();
						Iterator mList = tMap.keySet().iterator();

						while (mList.hasNext()) {
							String mName = mList.next().toString();
							String colStr = SolrHelper.toJsonArray(content.getCollection(mName));
							String encodedColStr = null;
							try {
								encodedColStr = SolrHelper.encodeString(colStr.trim());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							doc.setValue(mName, "JSCCOLLECTION:".concat(encodedColStr));
						}
					}

					if (content.getStructures() != null) {
						HashMap tMap = content.getStructures();
						Iterator mList = tMap.keySet().iterator();

						while (mList.hasNext()) {
							String mName = mList.next().toString();
							String strucStr = SolrHelper.toJson(content.getStructure(mName));
							String encodedStrucStr = null;
							try {
								encodedStrucStr = SolrHelper.encodeString(strucStr.trim());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							doc.setValue(mName, "JSCSTRUCTURE:".concat(encodedStrucStr));
						}
					}

					try {
						post = new HttpPost(queryURL.toString());
						post.setHeader("Content-Type", "application/json");
						StringBuffer postBody = new StringBuffer("{\"add\":{");
						postBody.append("\"overwrite\": true,\"commitWithin\": 1000,\"doc\": ");
						postBody.append(Helper.toJson(doc));
						postBody.append(" }}");
						String finalBody = postBody.toString();
						post.setEntity(new ByteArrayEntity(finalBody.getBytes("UTF-8")));
						client = new DefaultHttpClient();
						HttpResponse response = client.execute(post);
						// System.out.println("HTTP Status Code is =
						// "+response.getStatusLine());
					} catch (Exception ex) {
						// System.out.println("Can't write to solr.. check
						// exception below");
						ex.printStackTrace();
					} finally {
						if (client != null) {
							client = null;
						}
						if (post != null) {
							// post.releaseConnection();
						}
					}
					recordCounter++;
				}
			}
		}

	}

	public String hashIDWithPrefix(String prefix) {
		long hash = 0;
		String value = String.valueOf(Calendar.getInstance().getTime().getTime());

		for (int i = 0; i < value.length(); i++) {
			hash = 32 * hash + value.charAt(i);
		}

		return (prefix + String.valueOf(hash));
	}

	public String getTimestamp() {
		return (getTimestamp(java.util.Calendar.getInstance().getTime()));
	}

	public static String getTimestamp(java.util.Date date) {
		String result = "";

		java.text.SimpleDateFormat out = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		result = out.format(date);

		return (result);

	}

	@Override
	public DataBean readSelectedFields(String collectionName, String docName, String queryId, HashMap<String, String> paramMap, String selectedFields) throws ServiceException {
		selectedFields = selectedFields != null && selectedFields.length() > 0 ? selectedFields : "";
		DataBean result = new DataBean();
		Env config = getServiceContext();
		String solrClient = config.getStructureByName(EnvConstant.GEN_CONFIG).getString("solr_client_type");
		if (solrClient != null && "H".equals(solrClient)) {
			HttpGet get = null;
			HttpClient client = null;
			SolrQueryBean queryBean = getSolrQueryBean();
			String queryString = queryBean.getQueryString(collectionName, docName, queryId);
			queryString = SolrQueryBean.replaceDynamicParams(queryString, paramMap);
			queryString = queryString.replaceAll(" ", "+");
			String url = config.getStructureByName(EnvConstant.GEN_CONFIG).getString("failover_solr_url");
			StringBuffer queryURL = new StringBuffer(url);
			queryURL.append("/solr/");
			queryURL.append(collectionName);
			queryURL.append("/select:?wt=json");
			queryURL.append("&rows=1000");
			if (selectedFields.length() > 0) {
				queryURL.append("&fl=" + selectedFields);
			}
			queryURL.append("&q=");
			queryURL.append(queryString);
			// Console.debug(this,"queryURL" + queryURL);

			String key = queryURL.toString();
			if (isLocal && !excludedQueryList.contains(key) && solrCache.containsKey(key)) {
				getLogger().debug("Read cache for " + key);
				return solrCache.get(key);
			}

			try {
				client = new DefaultHttpClient();
				get = new HttpGet(queryURL.toString());
				HttpResponse response = client.execute(get);
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer json = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					json.append(line);
				}
				DataBean queryResult = Helper.parseJSON(json.toString());
				ArrayList<DataBean> docList = queryResult.getStructure("response").getCollection("docs");
				DataBean data;
				if (docList != null) {
					for (DataBean doc : docList) {
						Enumeration<String> cols = doc.getColumnNames();
						data = new DataBean();
						while (cols.hasMoreElements()) {
							String tKey = cols.nextElement();
							String newVal = doc.getString(tKey);
							if (newVal.startsWith("JSCCOLLECTION:")) {
								newVal = SolrHelper.decodeString((newVal.substring("JSCCOLLECTION:".length())));
								// newVal =
								// String.valueOf(Base64.decodeBase64((newVal.substring("JSCCOLLECTION:".length()).getBytes())));
								data.setCollection(tKey, SolrHelper.parseJsonArray(newVal));
							} else if (newVal.startsWith("JSCSTRUCTURE:")) {
								newVal = SolrHelper.decodeString((newVal.substring("JSCSTRUCTURE:".length())));
								// newVal =
								// String.valueOf(Base64.decodeBase64((newVal.substring("JSCSTRUCTURE:".length()).getBytes())));
								data.setStructure(tKey, SolrHelper.parseJSON(newVal));
							} else {
								data.setValue(tKey, newVal);
							}

						}
						result.addToCollection("contents", data);
						result.setValue("status", 1);
					}
				}

			} catch (Exception ex) {
				// System.out.println("can't read from solr.. check exception
				// below");
				ex.printStackTrace();
			} finally {
				if (client != null) {
					client = null;
				}
				if (get != null) {
					// get.releaseConnection();
				}
			}

			solrCache.put(key, result);
			System.out.println("Successfully read data from solr for query id : " + queryId);
			return result;
		}

		CloudSolrClient solrServer = null;
		String solrUrl = getSolrConnectionURL();
		try {
			SolrQueryBean queryBean = getSolrQueryBean();
			String queryString = queryBean.getQueryString(collectionName, docName, queryId);
			queryString = SolrQueryBean.replaceDynamicParams(queryString, paramMap);
			// System.out.println("Trying to create the client with ssl off");
			// CloseableHttpClient client =
			// HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
			// HttpClient client = HttpClientBuilder.create().build();
			solrServer = new CloudSolrClient(solrUrl);
			// System.out.println("Client created successfully");
			// solrServer = new CloudSolrClient(solrUrl);
			solrServer.setDefaultCollection(collectionName);
			ModifiableSolrParams qparams = new ModifiableSolrParams();
			qparams.set("rows", queryBean.getResultSize());
			qparams.set("q", queryString);
			if (selectedFields.length() > 0) {
				qparams.set("fl", selectedFields);
			}
			QueryResponse response = solrServer.query(qparams);

			SolrDocumentList docList = response.getResults();
			int resultSize = docList.size();
			long totalCount = docList.getNumFound();
			// TODO: Not a valid use case in our case... but for a safety will
			// update the code accordingly to fetch the rest of the rows and
			// append it into the result Databean
			if (totalCount > resultSize) {
				// we need to fetch rest
			}

			result.setValue("count", resultSize);

			SolrDocument tEntry;
			DataBean dataBean;
			for (int i = 0; i < resultSize; i++) {

				tEntry = docList.get(i);
				Collection<String> cols = tEntry.getFieldNames();

				dataBean = new DataBean();
				for (String tKey : cols) {
					if (tEntry.getFieldValue(tKey) != null) {
						String newVal = String.valueOf(tEntry.getFieldValue(tKey)).trim();
						if (newVal.startsWith("JSCCOLLECTION:")) {
							newVal = SolrHelper.decodeString((newVal.substring("JSCCOLLECTION:".length())));
							// newVal =
							// String.valueOf(Base64.decodeBase64((newVal.substring("JSCCOLLECTION:".length()).getBytes())));
							dataBean.setCollection(tKey, SolrHelper.parseJsonArray(newVal));
						} else if (newVal.startsWith("JSCSTRUCTURE:")) {
							newVal = SolrHelper.decodeString((newVal.substring("JSCSTRUCTURE:".length())));
							// newVal =
							// String.valueOf(Base64.decodeBase64((newVal.substring("JSCSTRUCTURE:".length()).getBytes())));
							dataBean.setStructure(tKey, SolrHelper.parseJSON(newVal));
						} else {
							dataBean.setValue(tKey, newVal);
						}

					}
				}
				result.addToCollection("contents", dataBean);
				result.setValue("status", 1);
			}
		} catch (Exception ex) {
			result.setValue("status", 0);
			result.setValue("errormsg", ex.getMessage());
		}
		return result;
	}

	private Logger getLogger() {
		PropertyConfigurator.configure(PropertyFileLoader.loadLogProperty(Constants.LOG4J_PROPFILE, null, null));
		Logger logger = Logger.getLogger(this.getClass());

		return logger;
	}

}
