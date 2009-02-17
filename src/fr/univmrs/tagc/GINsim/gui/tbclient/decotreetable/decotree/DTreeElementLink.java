package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;

public class DTreeElementLink extends DTreeElementButton implements ActionListener {
	private static final String WINDOWS_ID = "Windows";
	private static final String WINDOWS_PATH = "rundll32";
	private static final String WINDOWS_FLAG = "url.dll,FileProtocolHandler";
	private static final String UNIX_NETSCAPE_PATH = "netscape";
	private static final String UNIX_NETSCAPE_FLAG = "-remote openURL";
	private static final String UNIX_FIREFOX_PATH = "firefox";
	private static final String UNIX_FIREFOX_FLAG = "-new-tab";
	private URL url;

	public DTreeElementLink(AbstractDTreeElement e, URL u, ImageIcon ic, String title, boolean inTable) {
		super(e, ic, title, null, inTable, null);
		super.setListener(this);
		url = u;
	}
	public void actionPerformed(ActionEvent actionEvent) {
		String cmd;
		if (isWindowsPlatform())
			cmd = WINDOWS_PATH + " " + WINDOWS_FLAG + " " + url.toString();
		else
			cmd = UNIX_NETSCAPE_PATH + " " + UNIX_NETSCAPE_FLAG + "(" + url.toString() + ")";
		try {
			Runtime.getRuntime().exec(cmd);
		}
		catch (IOException e) {
			if (cmd.startsWith(UNIX_NETSCAPE_PATH))
				try {
					cmd = UNIX_NETSCAPE_PATH + " " + url.toString();
					Runtime.getRuntime().exec(cmd);
				}
				catch (Exception e2) {
					try {
						cmd = UNIX_FIREFOX_PATH + " " + UNIX_FIREFOX_FLAG + " " + url.toString();
						Runtime.getRuntime().exec(cmd);
					}
					catch (Exception e3) {
						e3.printStackTrace();
					}
				}
		}
	}
	private boolean isWindowsPlatform() {
		String os = System.getProperty("os.name");
		return ((os != null) && os.startsWith(WINDOWS_ID));
	}
}
