package ninjabrainbot.model.datastate.endereye;

import ninjabrainbot.event.DisposeHandler;
import ninjabrainbot.event.IDisposable;
import ninjabrainbot.event.ISubscribable;
import ninjabrainbot.event.ObservableField;
import ninjabrainbot.io.IClipboardProvider;
import ninjabrainbot.model.datastate.common.*;
import ninjabrainbot.model.input.F3IBlockData;
import ninjabrainbot.model.input.IBlockPositionInputSource;

/**
 * Listens changes of the clipboard in the ClipboardProvider and parses any compatible clipboard strings
 * into player positions and fossils, exposed through the streams whenNewPlayerPositionInputted(), and whenNewFossilInputted().
 */
public class CoordinateInputSource implements IPlayerPositionInputSource, IBlockPositionInputSource, IDisposable {

	private final ObservableField<IDetailedPlayerPosition> whenNewDetailedPlayerPositionInputted;
	private final ObservableField<IPlayerPosition> whenNewLimitedPlayerPositionInputted;
	private final ObservableField<BlockPosition> whenNewBlockPositionInputted;

	private final DisposeHandler disposeHandler = new DisposeHandler();

	public CoordinateInputSource(IClipboardProvider clipboardProvider) {
        whenNewDetailedPlayerPositionInputted = new ObservableField<>(null, true);
		whenNewLimitedPlayerPositionInputted = new ObservableField<>(null, true);
		whenNewBlockPositionInputted = new ObservableField<>(null, true);

		disposeHandler.add(clipboardProvider.clipboardText().subscribe(this::parseClipboard));
	}

	private void parseClipboard(String clipboard) {
		if (clipboard == null)
			return;

		F3CData f3cData = F3CData.tryParseF3CString(clipboard);
		if (f3cData != null) {
			whenNewDetailedPlayerPositionInputted.setAndAlwaysNotifySubscribers(new DetailedPlayerPosition(f3cData.x, f3cData.y, f3cData.z, f3cData.horizontalAngle, f3cData.verticalAngle, f3cData.nether));
			return;
		}

		InputData1_12 data1_12 = InputData1_12.parseInputString(clipboard);
		if (data1_12 != null) {
			whenNewLimitedPlayerPositionInputted.setAndAlwaysNotifySubscribers(new LimitedPlayerPosition(data1_12.x, data1_12.z, data1_12.horizontalAngle));
			return;
		}

		F3IBlockData f3IBlockData = F3IBlockData.tryParseF3IString(clipboard);
		if (f3IBlockData != null) {
			whenNewBlockPositionInputted.setAndAlwaysNotifySubscribers(new BlockPosition(f3IBlockData.x, f3IBlockData.y, f3IBlockData.z));
		}
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

	@Override
	public void dispose() {
		disposeHandler.dispose();
	}
}
