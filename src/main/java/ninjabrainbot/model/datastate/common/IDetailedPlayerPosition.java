package ninjabrainbot.model.datastate.common;

public interface IDetailedPlayerPosition extends IPlayerPosition {

	double yInPlayerDimension();

	/**
	 * In degrees.
	 */
	double verticalAngle();

	boolean lookingBelowHorizon();

}
