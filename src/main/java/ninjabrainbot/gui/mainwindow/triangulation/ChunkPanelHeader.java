package ninjabrainbot.gui.mainwindow.triangulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import ninjabrainbot.event.IDisposable;
import ninjabrainbot.event.Subscription;
import ninjabrainbot.gui.components.panels.ThemedPanel;
import ninjabrainbot.gui.style.SizePreference;
import ninjabrainbot.gui.style.StyleManager;
import ninjabrainbot.gui.style.theme.ColumnLayout;
import ninjabrainbot.gui.style.theme.WrappedColor;
import ninjabrainbot.io.preferences.NinjabrainBotPreferences;
import ninjabrainbot.io.preferences.enums.StrongholdDisplayType;
import ninjabrainbot.util.I18n;

public class ChunkPanelHeader extends ThemedPanel implements IDisposable {

	private final JLabel location;
	private final JLabel angle;
	private final JLabel[] labels;

	final StyleManager styleManager;

	final Subscription strongholdDisplayTypeChangedSubscription;

	private final WrappedColor borderCol;

	public ChunkPanelHeader(StyleManager styleManager, NinjabrainBotPreferences preferences) {
		super(styleManager, true);
		this.styleManager = styleManager;
		setOpaque(true);
		location = new JLabel("", SwingConstants.CENTER);
		JLabel certainty = new JLabel(I18n.get("certainty_2"), SwingConstants.CENTER);
		JLabel distance = new JLabel(I18n.get("dist"), SwingConstants.CENTER);
		JLabel nether = new JLabel(I18n.get("nether"), SwingConstants.CENTER);
		angle = new JLabel(I18n.get("angle"), SwingConstants.CENTER);
		labels = new JLabel[] { location, certainty, distance, nether, angle };
		ColumnLayout layout = new ColumnLayout(0);
		layout.setRelativeWidth(location, 2f);
		layout.setRelativeWidth(nether, 1.8f);
		layout.setRelativeWidth(angle, 2.5f);
		setLayout(layout);
		add(location);
		add(certainty);
		add(distance);
		add(nether);
		setAngleUpdatesEnabled(preferences.showAngleUpdates.get());
		updateHeaderText(preferences.strongholdDisplayType.get());
		strongholdDisplayTypeChangedSubscription = preferences.strongholdDisplayType.whenModified().subscribeEDT(this::updateHeaderText);

		borderCol = styleManager.currentTheme.COLOR_DIVIDER_DARK;
		setBackgroundColor(styleManager.currentTheme.COLOR_HEADER);
		setForegroundColor(styleManager.currentTheme.TEXT_COLOR_HEADER);
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (labels != null) {
			for (JLabel l : labels) {
				l.setFont(font);
			}
		}
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (labels != null) {
			for (JLabel l : labels) {
				if (l != null)
					l.setForeground(fg);
			}
		}
	}

	public void updateHeaderText(StrongholdDisplayType sdt) {
		location.setText(sdt == StrongholdDisplayType.CHUNK ? I18n.get("chunk") : I18n.get("location"));
	}

	public void setAngleUpdatesEnabled(boolean b) {
		if (b) {
			add(angle);
		} else {
			remove(angle);
		}
	}

	@Override
	public void updateColors() {
		setBorder(new MatteBorder(0, 0, 2, 0, borderCol.color()));
		super.updateColors();
	}

	@Override
	public void updateSize(StyleManager styleManager) {
		super.updateSize(styleManager);
		setPreferredSize(new Dimension(styleManager.size.WIDTH, styleManager.size.TEXT_SIZE_MEDIUM + styleManager.size.PADDING_THIN * 2));
	}

	@Override
	public int getTextSize(SizePreference p) {
		return p.TEXT_SIZE_MEDIUM;
	}

	@Override
	public void dispose() {
		strongholdDisplayTypeChangedSubscription.dispose();
	}

}
