package ninjabrainbot.model.datastate.highprecision;

import ninjabrainbot.model.domainmodel.DataComponent;
import ninjabrainbot.model.domainmodel.IDataComponent;
import ninjabrainbot.model.domainmodel.IDomainModel;

public class BoatDataState implements IBoatDataState {

	private final DataComponent<Boolean> enteringBoat;
	private final DataComponent<Float> boatAngle;
	private final DataComponent<BoatState> boatState;

	public BoatDataState(IDomainModel domainModel) {
		enteringBoat = new DataComponent<>(domainModel, false);
		boatAngle = new DataComponent<>(domainModel);
		boatState = new DataComponent<>(domainModel, BoatState.NONE);
	}

	@Override
	public IDataComponent<Boolean> enteringBoat() {
		return enteringBoat;
	}

	@Override
	public IDataComponent<Float> boatAngle() {
		return boatAngle;
	}

	@Override
	public IDataComponent<BoatState> boatState() {
		return boatState;
	}

}
