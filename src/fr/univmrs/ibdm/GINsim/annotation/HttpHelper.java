package fr.univmrs.ibdm.GINsim.annotation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.tagc.global.Tools;

public class HttpHelper implements AnnotationHelper {

	static Map m_proto = new HashMap();
	public static final String DOIBASE = "http://dx.doi.org/";
	
	public void update(AnnotationLink l, GsGraph graph) {
		// for later usage...
		if (l.proto.equals("http") || l.proto.equals("https") || l.proto.equals("ftp")) {
			l.value = l.proto+l.value;
			l.proto = null;
		}
	}

	public void open(AnnotationLink l) {
		Tools.webBrowse(getLink(l));
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
			AnnotationLink.addHelperClass((String)it.next(), h);
		}
	}

	public String getLink(AnnotationLink l) {
		if (l.proto == null) {
			return l.value;
		}
		return m_proto.get(l.proto)+l.value;
	}
}
