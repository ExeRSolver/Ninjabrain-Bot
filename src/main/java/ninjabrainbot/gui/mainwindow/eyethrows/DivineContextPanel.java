package ninjabrainbot.gui.mainwindow.eyethrows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import ninjabrainbot.event.IReadOnlyList;
import ninjabrainbot.model.datastate.divine.*;
import ninjabrainbot.model.input.IButtonInputHandler;
import ninjabrainbot.event.IDisposable;
import ninjabrainbot.event.Subscription;
import ninjabrainbot.gui.buttons.FlatButton;
import ninjabrainbot.gui.components.panels.ThemedPanel;
import ninjabrainbot.gui.style.SizePreference;
import ninjabrainbot.gui.style.StyleManager;
import ninjabrainbot.gui.style.theme.WrappedColor;
import ninjabrainbot.util.I18n;

/**
 * JComponent for showing a Throw.
 */
public class DivineContextPanel extends ThemedPanel implements IDisposable {

	private final int index;
	private IDivinable divine;
	private final JLabel label;
	private final FlatButton removeButton;

	final Subscription divineSubscription;
	final Runnable whenVisibilityChanged;

	private final WrappedColor borderCol;

	public DivineContextPanel(StyleManager styleManager, IDivineContext divineContext, IButtonInputHandler buttonInputHandler, int index, Runnable whenVisibilityChanged) {
		super(styleManager);
		this.index = index;
		this.whenVisibilityChanged = whenVisibilityChanged;

		setOpaque(true);
		label = new JLabel((String) null, SwingConstants.CENTER);
		removeButton = new FlatButton(styleManager, "-");
		removeButton.setBackgroundColor(styleManager.currentTheme.COLOR_DIVIDER);
		removeButton.setForegroundColor(styleManager.currentTheme.TEXT_COLOR_SLIGHTLY_STRONG);
		removeButton.setHoverColor(styleManager.currentTheme.COLOR_EXIT_BUTTON_HOVER);
		removeButton.setBorder(null);
		add(removeButton);
		add(label);
		setLayout(null);
		updateVisibility();

		updateDivine(divineContext.getDivineObjects());
		removeButton.addActionListener(__ -> buttonInputHandler.onRemoveDivineButtonPressed(this.divine));
		divineSubscription = divineContext.getDivineObjects().subscribeEDT(this::updateDivine);

		borderCol = styleManager.currentTheme.COLOR_DIVIDER_DARK;
		setBackgroundColor(styleManager.currentTheme.COLOR_DIVIDER);
		setForegroundColor(styleManager.currentTheme.TEXT_COLOR_SLIGHTLY_STRONG);
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (label != null)
			label.setFont(font);
		if (removeButton != null)
			removeButton.setFont(font);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		int w = width - height;
		if (this.label != null)
			this.label.setBounds(0, 0, w, height);
		if (this.removeButton != null)
			this.removeButton.setBounds(w, 0, height, height - 1);
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (label != null)
			label.setForeground(fg);
	}

	@Override
	public void updateColors() {
		setBorder(new MatteBorder(0, 0, 1, 0, borderCol.color()));
		super.updateColors();
	}

	@Override
	public void updateSize(StyleManager styleManager) {
		super.updateSize(styleManager);
		setPreferredSize(new Dimension(styleManager.size.WIDTH, styleManager.size.TEXT_SIZE_SMALL + styleManager.size.PADDING_THIN * 2));
	}

	void updateVisibility() {
		boolean newVisibility = (divine != null);
		if (newVisibility != isVisible()) {
			setVisible(newVisibility);
			if (whenVisibilityChanged != null)
				whenVisibilityChanged.run();
		}
	}

	@Override
	public int getTextSize(SizePreference p) {
		return p.TEXT_SIZE_SMALL;
	}

	@Override
	public void dispose() {
		divineSubscription.dispose();
	}

	private void updateDivine(IReadOnlyList<IDivinable> divineObjects) {
		setDivine(index < divineObjects.size() ? divineObjects.get(index) : null);
	}

	private void setDivine(IDivinable divine) {
		if (this.divine == divine)
			return;

		this.divine = divine;
		if (divine == null) {
			label.setText(null);
			removeButton.setVisible(false);
		} else {
			switch (divine.divineType()) {
				case FOSSIL:
					label.setText((I18n.get("divine") + I18n.get("fossil_number", ((Fossil) divine).x)));
					break;
				case BURIED_TREASURE:
					label.setText((I18n.get("divine") + I18n.get("bt_coords", ((BuriedTreasure) divine).x, ((BuriedTreasure) divine).z)));
					break;
				case FIRST_PORTAL:
					label.setText((I18n.get("divine") + I18n.get("first_portal", ((FirstPortal) divine).orientation())));
					break;
			}
			removeButton.setVisible(true);
		}
		updateVisibility();
	}

}
