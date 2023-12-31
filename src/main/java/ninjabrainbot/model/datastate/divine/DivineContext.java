package ninjabrainbot.model.datastate.divine;

import ninjabrainbot.event.DisposeHandler;
import ninjabrainbot.event.IDisposable;
import ninjabrainbot.model.datastate.blind.BlindPosition;
import ninjabrainbot.model.datastate.statistics.DiscretizedDensity;
import ninjabrainbot.model.datastate.stronghold.Ring;
import ninjabrainbot.model.domainmodel.DataComponent;
import ninjabrainbot.model.domainmodel.IDataComponent;
import ninjabrainbot.model.domainmodel.IDomainModel;
import ninjabrainbot.model.domainmodel.ListComponent;
import ninjabrainbot.util.Coords;
import ninjabrainbot.util.Logger;

public class DivineContext implements IDivineContext, IDisposable {

	private final DiscretizedDensity discretizedAngularDensity;
	private final DivineMonteCarloSimulator simulator;

	private final ListComponent<IDivinable> divineObjects;

	private final DataComponent<Boolean> measuringPortalOrientation;

	private final DisposeHandler disposeHandler = new DisposeHandler();

	public DivineContext(IDomainModel domainModel) {
        divineObjects = new ListComponent<>(domainModel, 10);
		measuringPortalOrientation = new DataComponent<>(domainModel, false);
        discretizedAngularDensity = new DiscretizedDensity(0, 2.0 * Math.PI);
		simulator = new DivineMonteCarloSimulator(divineObjects);

		disposeHandler.add(divineObjects.subscribeInternal(this::onChanged));
	}

	@Override
	public double relativeDensity() {
		Fossil fossil = (Fossil) getFirstDivineObjectOfType(DivineType.FOSSIL);
		return fossil == null ? 1.0 : (16.0 / 3.0);
	}

	@Override
	public void addDivineObject(IDivinable newDivine) {
		for (IDivinable oldDivine : divineObjects) {
			if (newDivine.equals(oldDivine))
				return;
			if (newDivine.divineType() == oldDivine.divineType() && !oldDivine.divineType().allowMultiple()) {
				divineObjects.replace(oldDivine, newDivine);
				return;
			}
		}
		divineObjects.add(newDivine);
	}

	@Override
	public void removeDivineObject(IDivinable divineObject) {
		divineObjects.remove(divineObject);
	}

	@Override
	public ListComponent<IDivinable> getDivineObjects() {
		return divineObjects;
	}

	@Override
	public IDivinable getFirstDivineObjectOfType(DivineType type) {
		for (IDivinable divineObject : divineObjects) {
			if (divineObject.divineType() == type)
				return divineObject;
		}
		return null;
	}

	@Override
	public IDataComponent<Boolean> measuringPortalOrientation() {
		return measuringPortalOrientation;
	}

	@Override
	public boolean hasDivine() {
		return divineObjects.size() > 0;
	}

	public double getDensityAtAngleBeforeSnapping(double phi) {
		while (phi < 0)
			phi += 2.0 * Math.PI;
		return discretizedAngularDensity.getDensity(phi);
	}

	/**
	 * Returns the closest of the three divine coords that are a distance r from
	 * (0,0)
	 */
	public BlindPosition getClosestCoords(double x, double z, double r) {
		Fossil fossil = (Fossil) getFirstDivineObjectOfType(DivineType.FOSSIL);
		if (fossil == null) {
			double multiplier = r / Coords.dist(x, z, 0, 0);
			return new BlindPosition(x * multiplier, z * multiplier);
		}
		int n = Ring.get(0).numStrongholds;
		double minDist2 = Double.MAX_VALUE;
		int angleIndex = -4 + fossil.x;
		if (angleIndex < 0) {
			angleIndex += 16;
		}
		double phi = 2.0 * Math.PI * ((angleIndex + 0.5) / 16.0);
		double optX = 0;
		double optZ = 0;
		for (int i = 0; i < n; i++) {
			double phi_i = phi + i * 2.0 * Math.PI / n;
			double x2 = Coords.getX(r, phi_i);
			double z2 = Coords.getZ(r, phi_i);
			double d2 = Coords.dist2(x, z, x2, z2);
			if (d2 < minDist2) {
				minDist2 = d2;
				optX = x2;
				optZ = z2;
			}
		}
		return new BlindPosition(optX, optZ);
	}

	private void onChanged() {
		discretizedAngularDensity.reset(256 * 3);
		if (hasDivine()) {
			long t0 = System.currentTimeMillis();

			simulator.reset();
			System.out.println("Updating " + divineObjects.size() + " divine objects");
			for (int i = 0; i < 100000 && simulator.shouldContinue(); i++) {
				double phi = simulator.nextAngle();
				addDensityThreeStrongholds(phi, 1);
			}
			Logger.log("Time to update divine angular density: " + (System.currentTimeMillis() - t0) / 1000f + " seconds.");
		}
		discretizedAngularDensity.normalize();
	}

	private void addDensityThreeStrongholds(double phi, double density) {
		for (int i = 0; i < 3; i++) {
			if (phi > 2 * Math.PI)
				phi -= 2 * Math.PI;
			discretizedAngularDensity.addDensity(phi, 1.0);
			phi += 2.0 / 3.0 * Math.PI;
		}
	}

	@Override
	public void dispose() {
		disposeHandler.dispose();
	}
}
