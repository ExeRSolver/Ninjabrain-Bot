package ninjabrainbot.gui.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import ninjabrainbot.gui.buttons.FlatButton;
import ninjabrainbot.gui.components.panels.ThemedPanel;
import ninjabrainbot.gui.style.StyleManager;
import ninjabrainbot.gui.style.theme.Theme;
import ninjabrainbot.gui.style.theme.WrappedColor;
import ninjabrainbot.io.preferences.NinjabrainBotPreferences;

public class ThemePanel extends ThemedPanel {

	final WrappedColor borderCol;

	public ThemePanel(StyleManager styleManager, NinjabrainBotPreferences preferences, Theme theme) {
		super(styleManager);
		setLayout(new GridLayout(0, 1));
		borderCol = styleManager.currentTheme.COLOR_DIVIDER_DARK;

		FlatButton nameLabel = new LeftAlignedButton(styleManager, theme.toString());
		nameLabel.setForegroundColor(theme.TEXT_COLOR_TITLE);
		nameLabel.setHoverColor(theme.COLOR_STRONGEST);
		nameLabel.setBackgroundColor(theme.COLOR_STRONGEST);
		nameLabel.setBorder(new EmptyBorder(5, 8, 5, 5));

		ThemedPanel colorPreviewPanels = new ThemedPanel(styleManager);
		colorPreviewPanels.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.weightx = 1;
		colorPreviewPanels.add(new ColorDisplayPanel(styleManager, theme.COLOR_NEUTRAL), gbc);
		colorPreviewPanels.add(new ColorDisplayPanel(styleManager, theme.COLOR_SLIGHTLY_WEAK), gbc);
		colorPreviewPanels.add(new ColorDisplayPanel(styleManager, theme.COLOR_STRONG), gbc);
		colorPreviewPanels.add(new ColorDisplayPanel(styleManager, theme.COLOR_DIVIDER), gbc);
		colorPreviewPanels.add(new ColorDisplayPanel(styleManager, theme.COLOR_DIVIDER_DARK), gbc);
		colorPreviewPanels.setBackgroundColor(theme.COLOR_DIVIDER_DARK);

		add(nameLabel);
		add(colorPreviewPanels);

		nameLabel.addActionListener(__ -> preferences.theme.set(theme.UID));
		theme.whenNameChanged().subscribeEDT(nameLabel::setText);
		theme.whenModified().subscribeEDT(__ -> styleManager.init());
	}

	@Override
	public void updateColors() {
		super.updateColors();
		setBorder(new LineBorder(borderCol.color(), 1));
	}

}

class LeftAlignedButton extends FlatButton {

	LeftAlignedButton(StyleManager styleManager, String name) {
		super(styleManager, name);
		setHorizontalAlignment(SwingConstants.LEFT);
	}

}