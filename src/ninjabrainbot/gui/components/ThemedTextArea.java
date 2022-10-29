package ninjabrainbot.gui.components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextArea;

import ninjabrainbot.gui.style.ConfigurableColor;
import ninjabrainbot.gui.style.SizePreference;
import ninjabrainbot.gui.style.StyleManager;

public class ThemedTextArea extends JTextArea implements ThemedComponent {

	private static final long serialVersionUID = -1769219771406000716L;
	public boolean bold;

	private ConfigurableColor bgCol;
	private ConfigurableColor fgCol;

	public ThemedTextArea(StyleManager styleManager) {
		this(styleManager, "");
	}

	public ThemedTextArea(StyleManager styleManager, String text) {
		this(styleManager, text, false);
	}

	public ThemedTextArea(StyleManager styleManager, String text, boolean bold) {
		super(text);
		styleManager.registerThemedComponent(this);
		this.bold = bold;
		setEditable(false);
		setLineWrap(false);

		bgCol = styleManager.currentTheme.COLOR_STRONG;
		fgCol = styleManager.currentTheme.TEXT_COLOR_STRONG;
	}

	public void updateSize(StyleManager styleManager) {
		setFont(styleManager.fontSize(getTextSize(styleManager.size), !bold));
	}

	public void updateColors() {
		Color bg = getBackgroundColor();
		if (bg != null)
			setBackground(bg);
		Color fg = getForegroundColor();
		if (fg != null)
			setForeground(fg);
	}

	public int getTextSize(SizePreference p) {
		return p.TEXT_SIZE_SMALL;
	}

	public void setBackgroundColor(ConfigurableColor color) {
		bgCol = color;
	}

	public void setForegroundColor(ConfigurableColor color) {
		fgCol = color;
	}

	protected Color getBackgroundColor() {
		return bgCol.color();
	}

	protected Color getForegroundColor() {
		return fgCol.color();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(0, super.getPreferredSize().height);
	}

}
