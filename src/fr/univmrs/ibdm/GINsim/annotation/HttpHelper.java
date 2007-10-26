package fr.univmrs.ibdm.GINsim.annotation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import fr.univmrs.ibdm.GINsim.global.Tools;

public class HttpHelper implements AnnotationHelper {

	static Map m_proto = new HashMap();
	
	static void addPattern(String key, String surl) {
		m_proto.put(key, Pattern.compile(surl));
	}
	
	public void update(AnnotationLink l) {
		// for later usage...
		if (l.proto.equals("http")) {
			l.helper = null;
		}
	}

	public void open(AnnotationLink l) {
		Tools.webBrowse(m_proto.get(l.proto)+l.value);
	}

	public static void setup() {
		m_proto.put("google", "http://www.google.fr/search?q=");
		m_proto.put("doi", "http://dx.doi.org/");
		m_proto.put("entrez", "http://www.ncbi.nlm.nih.gov/sites/entrez?Db=gene&Cmd=ShowDetailView&ordinalpos=1&TermToSearch=");
		
		HttpHelper h = new HttpHelper();
		Iterator it = m_proto.keySet().iterator();
		while (it.hasNext()) {
			AnnotationLink.addHelperClass((String)it.next(), h);
		}
	}
}
