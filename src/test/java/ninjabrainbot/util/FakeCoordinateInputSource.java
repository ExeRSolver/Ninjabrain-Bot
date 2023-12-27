package ninjabrainbot.util;

import ninjabrainbot.model.datastate.common.BlockPosition;
import ninjabrainbot.model.datastate.common.IDetailedPlayerPosition;
import ninjabrainbot.model.datastate.common.IPlayerPosition;
import ninjabrainbot.model.datastate.common.IPlayerPositionInputSource;
import ninjabrainbot.model.input.IBlockPositionInputSource;
import ninjabrainbot.event.ISubscribable;
import ninjabrainbot.event.ObservableProperty;

public class FakeCoordinateInputSource implements IPlayerPositionInputSource, IBlockPositionInputSource {

	public final ObservableProperty<IDetailedPlayerPosition> whenNewDetailedPlayerPositionInputted;
	public final ObservableProperty<IPlayerPosition> whenNewLimitedPlayerPositionInputted;
	public final ObservableProperty<BlockPosition> whenNewBlockPositionInputted;

	public FakeCoordinateInputSource() {
		whenNewDetailedPlayerPositionInputted = new ObservableProperty<>();
		whenNewLimitedPlayerPositionInputted = new ObservableProperty<>();
		whenNewBlockPositionInputted = new ObservableProperty<>();
	}

	public ISubscribable<IDetailedPlayerPosition> whenNewDetailedPlayerPositionInputted() {
		return whenNewDetailedPlayerPositionInputted;
	}

	public ISubscribable<IPlayerPosition> whenNewLimitedPlayerPositionInputted() {
		return whenNewLimitedPlayerPositionInputted;
	}

	public ISubscribable<BlockPosition> whenNewBlockPositionInputted() {
		return whenNewBlockPositionInputted;
	}
}
