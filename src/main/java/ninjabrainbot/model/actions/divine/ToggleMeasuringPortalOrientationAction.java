package ninjabrainbot.model.actions.divine;

import ninjabrainbot.model.actions.IAction;
import ninjabrainbot.model.datastate.IDataState;
import ninjabrainbot.model.domainmodel.IDataComponent;

public class ToggleMeasuringPortalOrientationAction implements IAction {

	private final IDataState dataState;

	public ToggleMeasuringPortalOrientationAction(IDataState dataState) {
		this.dataState = dataState;
	}

	@Override
	public void execute() {
		if (dataState.locked().get())
			return;

		IDataComponent<Boolean> measuringPortalOrientation = dataState.getDivineContext().measuringPortalOrientation();
		measuringPortalOrientation.set(!measuringPortalOrientation.get());
	}
}
