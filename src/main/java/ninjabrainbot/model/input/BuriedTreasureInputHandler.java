package ninjabrainbot.model.input;

import ninjabrainbot.event.DisposeHandler;
import ninjabrainbot.event.IDisposable;
import ninjabrainbot.model.actions.IActionExecutor;
import ninjabrainbot.model.actions.common.SetBuriedTreasureAction;
import ninjabrainbot.model.datastate.IDataState;
import ninjabrainbot.model.datastate.divine.BuriedTreasure;

/**
 * Listens to the stream of buried treasures and decides if/how the buried treasures should be inputted into the data state.
 */
public class BuriedTreasureInputHandler implements IDisposable {

	private final IDataState dataState;
	private final IActionExecutor actionExecutor;

	final DisposeHandler disposeHandler = new DisposeHandler();

	public BuriedTreasureInputHandler(IBuriedTreasureInputSource buriedTreasureInputSource, IDataState dataState, IActionExecutor actionExecutor) {
		this.dataState = dataState;
		this.actionExecutor = actionExecutor;
		disposeHandler.add(buriedTreasureInputSource.whenNewBuriedTreasureInputted().subscribe(this::onNewBuriedTreasure));
	}

	private void onNewBuriedTreasure(BuriedTreasure buriedTreasure) {
		if (dataState.locked().get())
			return;

		actionExecutor.executeImmediately(new SetBuriedTreasureAction(dataState.getDivineContext(), buriedTreasure));
	}

	@Override
	public void dispose() {
		disposeHandler.dispose();
	}
}
