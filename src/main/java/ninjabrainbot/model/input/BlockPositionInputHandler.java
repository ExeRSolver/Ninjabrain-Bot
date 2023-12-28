package ninjabrainbot.model.input;

import ninjabrainbot.event.DisposeHandler;
import ninjabrainbot.event.IDisposable;
import ninjabrainbot.model.actions.IAction;
import ninjabrainbot.model.actions.IActionExecutor;
import ninjabrainbot.model.actions.divine.SetBuriedTreasureAction;
import ninjabrainbot.model.actions.divine.SetFossilAction;
import ninjabrainbot.model.datastate.IDataState;
import ninjabrainbot.model.datastate.common.BlockPosition;
import ninjabrainbot.model.datastate.divine.BuriedTreasure;
import ninjabrainbot.model.datastate.divine.Fossil;

/**
 * Listens to a stream of block position inputs and decides if/how the inputs should affect the data state.
 */
public class BlockPositionInputHandler implements IDisposable {

	private final IDataState dataState;
	private final IActionExecutor actionExecutor;

	final DisposeHandler disposeHandler = new DisposeHandler();

	public BlockPositionInputHandler(IBlockPositionInputSource blockPositionInputSource, IDataState dataState, IActionExecutor actionExecutor) {
		this.dataState = dataState;
		this.actionExecutor = actionExecutor;
		disposeHandler.add(blockPositionInputSource.whenNewBlockPositionInputted().subscribe(this::onNewBlockPositionInputted));
	}

	private void onNewBlockPositionInputted(BlockPosition blockPosition) {
		IAction actionForNewThrow = getActionForInputtedBlockPosition(blockPosition);
		if (actionForNewThrow != null)
			actionExecutor.executeImmediately(actionForNewThrow);
	}

	private IAction getActionForInputtedBlockPosition(BlockPosition blockPosition) {
		if (dataState.locked().get())
			return null;

		final int x = blockPosition.getX();
		final int z = blockPosition.getZ();

		if (Math.floorMod(x, 16) == 9 && Math.floorMod(z, 16) == 9)
			return new SetBuriedTreasureAction(dataState.getDivineContext(), new BuriedTreasure(Math.floorDiv(x, 16), Math.floorDiv(z, 16)));

		if (x >= 0 && x < 16 && z >= 0 && z < 16)
			return new SetFossilAction(dataState.getDivineContext(), new Fossil(x));

		return null;
	}

	@Override
	public void dispose() {
		disposeHandler.dispose();
	}
}
