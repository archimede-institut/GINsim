package fr.univmrs.tagc.GINsim.gui.tbclient;

import java.util.Vector;

import javax.swing.JProgressBar;

import tbrowser.data.module.TBModules;
import java.util.Enumeration;
import java.util.Hashtable;

public class GsTBSendThread extends Thread {
  private JProgressBar progressBar = null;
  private boolean toKill = false;
  private GsTBClientPanel clientPanel;

  public GsTBSendThread() {
    super();
  }
  public GsTBSendThread(JProgressBar pb, GsTBClientPanel cp) {
    super();
    progressBar = pb;
    clientPanel = cp;
  }
  public void kill() {
    toKill = true;
  }
  public void run() {
  		Hashtable ez = clientPanel.getGeneTreeModel().getSelectedEntrezIDs();
  		String node, s, exp = "";
  		Vector v;
  		for (Enumeration enu = ez.keys(); enu.hasMoreElements(); ) {
  			node = (String)enu.nextElement();
  			v = (Vector)ez.get(node);
  			s = makeOrExpression(v);
  			if (!s.equals("")) {
      	  if (s.indexOf("|") != -1) s = "(" + s + ")";
          if (exp.equals(""))
              exp = s;
          else
              exp += " & " + s;
        }
  		}
			TBModules modules = null;
      
      /*Vector genes = new Vector();
      Vector symbol = clientPanel.getSelectedGenes();
      Hashtable h = new Hashtable();
      for (int i = 0; i < symbol.size(); i++) {
        v = (Vector)clientPanel.getClient().getGeneInfos(symbol.elementAt(i).toString());
        genes.addAll(v);
        h.put(symbol.elementAt(i), v);
      }
      exp = "";
      if (genes.size() > 0) {
          for (int i = 0; i < symbol.size(); i++) {
              s = makeOrExpression(symbol.elementAt(i).toString(), genes);
              if (!s.equals("")) {
            	  if (s.indexOf("|") != -1) s = "(" + s + ")";
                if (exp.equals(""))
                    exp = s;
                else
                    exp += " & " + s;
            }
          }
      }*/
      clientPanel.setQuery(exp);
      modules = (TBModules)clientPanel.getClient().getModulesFromEntrezIDsExpression(exp, true, true);
      if (!toKill) {
          clientPanel.clearResults();
          clientPanel.setModuleList(modules);
      }
      if (toKill) progressBar.setValue(0);
      clientPanel.resetSendButton();
  }

  private String makeOrExpression(Vector v) {
  	String exp = "";
  	for (int i = 0; i < v.size(); i++)
      if (exp.equals(""))
        exp = v.elementAt(i).toString();
      else
        exp += " | " + v.elementAt(i).toString();
    return exp;
  }
/*  private String makeOrExpression(String s, Vector g) {
      String gene, exp = "";
      String[] infos;
      Vector v = new Vector();

      for (Enumeration enu = g.elements(); enu.hasMoreElements(); ) {
          gene = enu.nextElement().toString();
          infos = gene.split("\t");
          if (infos[0].equalsIgnoreCase(s) || infos[1].equalsIgnoreCase(s))
            if (!v.contains(infos[0].toLowerCase()))
              v.addElement(infos[0].toLowerCase());
      }
      for (int i = 0; i < v.size(); i++)
        if (exp.equals(""))
          exp = v.elementAt(i).toString();
        else
          exp += " | " + v.elementAt(i).toString();
      return exp;
  }*/
}
