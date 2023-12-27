package ninjabrainbot.model.input;

import ninjabrainbot.event.ISubscribable;
import ninjabrainbot.model.datastate.divine.BuriedTreasure;

public interface IBuriedTreasureInputSource {

	/**
	 * Notifies subscribers whenever new fossil coordinates have been inputted, e.g. as a result of a F3+I command.
	 */
	ISubscribable<BuriedTreasure> whenNewBuriedTreasureInputted();

}
