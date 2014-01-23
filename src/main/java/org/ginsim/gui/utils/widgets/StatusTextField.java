package org.ginsim.gui.utils.widgets;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.ginsim.gui.utils.data.GenericPropertyHolder;
import org.ginsim.gui.utils.data.GenericPropertyInfo;
import org.ginsim.gui.utils.data.ObjectPropertyEditorUI;
import org.ginsim.gui.utils.data.TextProperty;


public class StatusTextField extends JTextField implements FocusListener,
	KeyListener, ObjectPropertyEditorUI {
	private static final long	serialVersionUID	= 7188801384907165831L;

	private static final int STATUS_OK = 0;
	private static final int STATUS_KO = 1;
	private static final int STATUS_NEW = 2;
	private static final int STATUS_EMPTY = 3;
	
	private static final Color fg = Color.BLACK;
	private static final Color fg_empty = Color.GRAY;
	private static final Color bg = Color.WHITE;
	private static final Color bg_new = Color.YELLOW;
	private static final Color bg_ko = Color.RED;
	
	String emptyText = null;
	int status;
	boolean highlight;
	boolean hasFocus = false;
	
	TextProperty pinfo = null;
	
	public StatusTextField() {
        this(null, false);
	}
	public StatusTextField(String emptyText, boolean highlight) {
		Dimension d = new Dimension(100,20);
        setPreferredSize(d);
        setMinimumSize(d);
        status = STATUS_OK;
		this.emptyText = emptyText;
		this.highlight = highlight;
		redraw();
		this.addKeyListener(this);
		this.addFocusListener(this);
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			apply();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (pinfo != null) {
				setText(pinfo.getValue());
			} else {
				setText("");
			}
		}
		redraw();
	}

	public void keyTyped(KeyEvent e) {
	}

	public void focusGained(FocusEvent e) {
		hasFocus = true;
		if (status == STATUS_EMPTY) {
			super.setText("");
		}
		redraw();
	}

	public void focusLost(FocusEvent e) {
		hasFocus = false;
		apply();
		redraw();
	}
	
	public String getText() {
		if (status == STATUS_EMPTY) {
			return "";
		}
		return super.getText();
	}
	
	public void setText(String t) {
		super.setText(t);
		redraw();
	}
	
	private void redraw() {
		// first get the status
		if (pinfo != null) {
			String s = getText();
			if (s.equals(pinfo.getValue())) {
				status = STATUS_OK;
			} else if (pinfo.isValidValue(s)) {
				status = STATUS_NEW;
			} else {
				status = STATUS_KO;
			}
		} else {
			if ("".equals(super.getText())) {
				if (emptyText != null) {
					status = STATUS_EMPTY;
				} else {
					status = STATUS_OK;
				}
			} else if (highlight) {
				status = STATUS_NEW;
			}
		}
		switch (status) {
			case STATUS_EMPTY:
				if (hasFocus) {
					setBackground(bg);
					setForeground(fg);
					super.setText("");
				} else {
					setBackground(bg);
					setForeground(fg_empty);
					super.setText(emptyText);
				}
				break;
			case STATUS_KO:
				setBackground(bg_ko);
				setForeground(fg);
				break;
			case STATUS_NEW:
				setBackground(bg_new);
				setForeground(fg);
				break;
			default:
				setBackground(bg);
				setForeground(fg);
				break;
		}
	}

	public void apply() {
		if (pinfo == null) {
			return;
		}
		pinfo.setValue(getText());
		redraw();
	}

	public void refresh(boolean force) {
		if (force) {
			setText(pinfo.getValue());
		}
		redraw();
	}

    public void setEditedProperty(GenericPropertyInfo pinfo, GenericPropertyHolder panel) {
        if (pinfo == null) {
            setProperty(null);
            return;
        }
        setProperty(new GenericTextProperty(pinfo));

        if (panel == null) {
            return;
        }

        int pos = 0;
        if (pinfo.name != null) {
            panel.addField(new JLabel(pinfo.name), pinfo, 0);
            pos++;
        }
        if (pinfo.isEditable) {
            setEditable(true);
        } else {
            setEditable(false);
        }
        panel.addField(this, pinfo, pos);
    }

	public void setProperty(TextProperty pinfo) {
		this.pinfo = pinfo;
        refresh(true);
	}

	@Override
	public void release() {
		apply();
	}
}

class GenericTextProperty implements TextProperty {

    private final GenericPropertyInfo pinfo;

    public GenericTextProperty(GenericPropertyInfo pinfo) {
        this.pinfo = pinfo;
    }

    @Override
    public String getValue() {
        try {
            return pinfo.getStringValue();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void setValue(String value) {
        pinfo.setValue(value);
    }

    @Override
    public boolean isValidValue(String value) {
        return pinfo.isValidValue(value);
    }
}