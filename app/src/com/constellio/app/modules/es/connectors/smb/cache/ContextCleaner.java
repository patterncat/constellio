package com.constellio.app.modules.es.connectors.smb.cache;

import com.constellio.app.modules.es.connectors.smb.service.SmbModificationIndicator;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContextCleaner {

	private static Map<String, SmbModificationIndicator> MAP = new HashMap<>();
	private static HttpSolrServer SERVER;

	public static void main(String[] args) throws Exception {

		String serverUrl = "http://localhost:8983/solr/records";
		String fetcherUrls =  "/tmp/cacheReader/fetchedUrls.txt";

		SmbConnectorContext context = read(new File(fetcherUrls));
		System.out.println("Loaded cache size : " + context.recordUrls.size());

		SERVER = new HttpSolrServer(serverUrl);
		load();
		System.out.println("Loaded database size : " + MAP.size());

		for (Map.Entry<String, SmbModificationIndicator> databaseEntry : MAP.entrySet()) {
			String url = databaseEntry.getKey();
			SmbModificationIndicator databaseIndicator = databaseEntry.getValue();
			SmbModificationIndicator contextIndicator = context.recordUrls.get(databaseEntry.getKey());
			boolean folder = StringUtils.endsWith(url, "/");
			if (folder && contextIndicator != null && contextIndicator.getLastModified() != databaseIndicator.getLastModified()) {
				context.recordUrls.remove(url);
			} else if (!folder && contextIndicator != null && !contextIndicator.equals(databaseIndicator)) {
				context.recordUrls.remove(url);
			}
		}

		System.out.println("New cleaned cache size : " + context.recordUrls.size());
		File savedCache = new File("/tmp/cacheReader/fetchedUrls_fixed.txt");
		save(context, savedCache);

		System.out.println("Saved to  : " + savedCache);
	}

	
	private static void load() throws Exception {
		int start = 0;
		boolean foundResult;
		do {
			foundResult = false;
			SolrDocumentList results = query(start);
			for (SolrDocument solrDocument : results) {
				foundResult = true;
				start++;

				String permissionHash = StringUtils.defaultIfEmpty((String) solrDocument.get("permissionsHash_s"), "");
				Double size = (Double) ObjectUtils.defaultIfNull((Double) solrDocument.get("size_d"), -3D);
				Date lastModified = (Date) ObjectUtils.defaultIfNull((Date) solrDocument.get("lastModified_dt"), new Date());

				//Complicated date management...
				LocalDateTime correctedLastModified = new LocalDateTime(lastModified).minusMillis(DateTimeZone.getDefault().getOffset(lastModified.getTime()));
				long millis = correctedLastModified.toDate().getTime();

				SmbModificationIndicator databaseIndicator = new SmbModificationIndicator(permissionHash, size, millis);
				MAP.put(solrDocument.get("url_s").toString(), databaseIndicator);
			}
			System.out.println("Loading batch from database : " + MAP.size());
		} while (foundResult);
	}

	private static SolrDocumentList query(int start) throws Exception {
		SolrQuery query = new SolrQuery();
		query.set("start", start);
		query.set("q", "*:*");
		query.set("sort", "url_s ASC");
		query.set("rows", 10000);
		query.set("wt", "json");
		query.set("fl", "url_s permissionsHash_s size_d lastModified_dt");
		query.add("fq", "url_s:smb*");

		QueryResponse response = SERVER.query(query);
		SolrDocumentList results = response.getResults();
		return results;
	}

	private static void save(SmbConnectorContext context, File file) throws IOException {
		try (ObjectOutputStream oos  = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			oos.writeObject(context);
		}
	}

	private static SmbConnectorContext read(File file)  throws Exception {
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			return (SmbConnectorContext) ois.readObject();
		}
	}
}
