package ninjabrainbot.model.input;

import ninjabrainbot.event.ISubscribable;
import ninjabrainbot.model.datastate.common.BlockPosition;
import ninjabrainbot.model.datastate.divine.Fossil;

public interface IBlockPositionInputSource {

	/**
	 * Notifies subscribers whenever new block coordinates have been inputted, e.g. as a result of a F3+I command.
	 */
	ISubscribable<BlockPosition> whenNewBlockPositionInputted();

}
