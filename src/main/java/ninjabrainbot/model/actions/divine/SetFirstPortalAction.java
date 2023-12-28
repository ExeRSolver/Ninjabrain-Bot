package ninjabrainbot.model.actions.divine;

import ninjabrainbot.model.actions.IAction;
import ninjabrainbot.model.datastate.divine.FirstPortal;
import ninjabrainbot.model.datastate.divine.IDivineContext;

public class SetFirstPortalAction implements IAction {

	private final IDivineContext divineContext;
	private final FirstPortal firstPortal;

	public SetFirstPortalAction(IDivineContext divineContext, double horizontalAngle) {
		this.divineContext = divineContext;

		horizontalAngle %= 360;
		if (horizontalAngle < 0)
			horizontalAngle += 360;

		int orientation;
		if (horizontalAngle >= 225 && horizontalAngle < 315)
			orientation = 0;
		else if (horizontalAngle >= 135 && horizontalAngle < 225)
			orientation = 1;
		else if (horizontalAngle >= 45 && horizontalAngle < 135)
			orientation = 2;
		else
			orientation = 3;

		this.firstPortal = new FirstPortal(orientation);
	}

	@Override
	public void execute() {
		divineContext.measuringPortalOrientation().set(false);
		divineContext.firstPortal().set(firstPortal);
	}

}
