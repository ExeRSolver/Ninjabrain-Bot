package ninjabrainbot.gui.components.layout;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JComponent;

import ninjabrainbot.gui.components.labels.ThemedLabel;
import ninjabrainbot.gui.components.panels.ThemedPanel;
import ninjabrainbot.gui.style.StyleManager;

public class LabeledField extends ThemedPanel {

	public LabeledField(StyleManager styleManager, String label, JComponent component, boolean stretchComponent) {
		super(styleManager);
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = 0;
		gbc.ipadx = 5;
		gbc.weightx = 0;
		add(new ThemedLabel(styleManager, label), gbc);

		if (stretchComponent) {
			gbc.weightx = 1;
			add(component, gbc);
		} else {
			add(component, gbc);
			gbc.weightx = 1;
			gbc.ipadx = 0;
			add(Box.createGlue(), gbc);
		}
	}
}
