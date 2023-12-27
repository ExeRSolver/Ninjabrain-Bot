package ninjabrainbot.gui.options.sections;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ninjabrainbot.gui.components.layout.StackPanel;
import ninjabrainbot.gui.components.preferences.CheckboxPanel;
import ninjabrainbot.gui.components.preferences.RadioButtonPanel;
import ninjabrainbot.gui.frames.OptionsFrame;
import ninjabrainbot.gui.style.StyleManager;
import ninjabrainbot.io.mcinstance.IActiveInstanceProvider;
import ninjabrainbot.io.preferences.NinjabrainBotPreferences;
import ninjabrainbot.util.I18n;

public class BasicOptionsPanel extends JPanel {

	public BasicOptionsPanel(StyleManager styleManager, NinjabrainBotPreferences preferences, IActiveInstanceProvider activeInstanceProvider) {
		setOpaque(false);
		setLayout(new GridLayout(1, 2, 2 * OptionsFrame.PADDING, 0));
		setBorder(new EmptyBorder(2 * OptionsFrame.PADDING, 2 * OptionsFrame.PADDING, 2 * OptionsFrame.PADDING, 2 * OptionsFrame.PADDING));
		JPanel column1 = new StackPanel(3 * OptionsFrame.PADDING);
		JPanel column2 = new StackPanel(2 * OptionsFrame.PADDING);
		column1.setOpaque(false);
		column2.setOpaque(false);
		add(column1);
		add(column2);

		// Column 1
		column1.add(new CheckboxPanel(styleManager, I18n.get("settings.show_nether_coordinates"), preferences.showNetherCoords));
		column1.add(new CheckboxPanel(styleManager, I18n.get("settings.auto_reset"), preferences.autoReset));
		if (activeInstanceProvider.supportsReadingActiveMinecraftWorld())
			column1.add(new CheckboxPanel(styleManager, I18n.get("settings.auto_reset_when_world_is_reset"), preferences.autoResetWhenChangingInstance));
		column1.add(new CheckboxPanel(styleManager, I18n.get("settings.always_on_top"), preferences.alwaysOnTop));
		column1.add(new CheckboxPanel(styleManager, I18n.get("settings.translucent_window"), preferences.translucent));
		column1.add(new CheckboxPanel(styleManager, I18n.get("settings.notify_when_a_new_version_is_available"), preferences.checkForUpdates));

		// Column 2
		column2.add(new RadioButtonPanel(styleManager, I18n.get("settings.display_stronghold_location_using"), preferences.strongholdDisplayType));
		column2.add(new RadioButtonPanel(styleManager, I18n.get("settings.view_type"), preferences.view));
		column2.add(new RadioButtonPanel(styleManager, I18n.get("settings.window_size"), preferences.size));
		column2.add(new RadioButtonPanel(styleManager, I18n.get("settings.mc_version"), preferences.mcVersion));
	}

}
