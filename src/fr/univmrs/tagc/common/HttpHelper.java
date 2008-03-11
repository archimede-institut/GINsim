package fr.univmrs.tagc.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpHelper implements OpenHelper {

	static Map m_proto = new HashMap();
	public static final String DOIBASE = "http://dx.doi.org/";
	
	public boolean open(String proto, String value) {
		return Tools.openURI(getLink(proto, value));
	}
	public void add(String proto, String value) {
	}

	public static void setup() {
		m_proto.put("http", null);
		m_proto.put("wp", "http://en.wikipedia.org/wiki/");

		m_proto.put("doi", DOIBASE);
		m_proto.put("pubmed", "http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=retrieve&db=pubmed&dopt=AbstractPlus&list_uids=");

		m_proto.put("hugo", "http://www.genenames.org/data/hgnc_data.php?hgnc_id=");
		m_proto.put("entrez", "http://www.ncbi.nlm.nih.gov/sites/entrez?Db=gene&Cmd=ShowDetailView&ordinalpos=1&TermToSearch=");
		
		HttpHelper h = new HttpHelper();
		Iterator it = m_proto.keySet().iterator();
		while (it.hasNext()) {
			Tools.addHelperClass((String)it.next(), h);
		}
	}
	public String getLink(String proto, String value) {
		return m_proto.get(proto)+value;
	}
}
