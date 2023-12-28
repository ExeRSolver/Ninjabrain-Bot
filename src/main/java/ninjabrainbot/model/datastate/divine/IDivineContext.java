package ninjabrainbot.model.datastate.divine;

import ninjabrainbot.model.datastate.blind.BlindPosition;
import ninjabrainbot.model.domainmodel.IDataComponent;
import ninjabrainbot.model.domainmodel.IListComponent;

public interface IDivineContext {

	Fossil getFossil();

	IDataComponent<Fossil> fossil();

	IListComponent<BuriedTreasure> buriedTreasures();

	IDataComponent<FirstPortal> firstPortal();

	boolean hasDivine();

	double relativeDensity();

	double getDensityAtAngleBeforeSnapping(double phi);

	/**
	 * Returns the closest of the three divine coords that are a distance r from
	 * (0,0)
	 */
	BlindPosition getClosestCoords(double x, double z, double r);

}
