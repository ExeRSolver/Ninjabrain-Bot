package ninjabrainbot.model.datastate.divine;

import ninjabrainbot.event.DisposeHandler;
import ninjabrainbot.event.IDisposable;
import ninjabrainbot.model.datastate.blind.BlindPosition;
import ninjabrainbot.model.datastate.statistics.DiscretizedDensity;
import ninjabrainbot.model.datastate.stronghold.Ring;
import ninjabrainbot.model.domainmodel.DataComponent;
import ninjabrainbot.model.domainmodel.IDataComponent;
import ninjabrainbot.model.domainmodel.IDomainModel;
import ninjabrainbot.util.Coords;

public class DivineContext implements IDivineContext, IDisposable {

	private final DiscretizedDensity discretizedAngularDensity;
	private final DivineMonteCarloSimulator simulator;

	public final DataComponent<Fossil> fossil;
	private final DataComponent<FirstPortal> firstPortal;
	private final DataComponent<BuriedTreasure> buriedTreasure;

	private final DisposeHandler disposeHandler = new DisposeHandler();

	public DivineContext(IDomainModel domainModel) {
		fossil = new DataComponent<>(domainModel);
        buriedTreasure = new DataComponent<>(domainModel);
        firstPortal = new DataComponent<>(domainModel);
        discretizedAngularDensity = new DiscretizedDensity(0, 2.0 * Math.PI);
		simulator = new DivineMonteCarloSimulator();
		disposeHandler.add(fossil.subscribeInternal(this::onChanged));
		disposeHandler.add(buriedTreasure.subscribeInternal(this::onChanged));
		disposeHandler.add(firstPortal.subscribeInternal(this::onChanged));
	}

	@Override
	public Fossil getFossil() {
		return fossil.get();
	}

	@Override
	public double relativeDensity() {
		return fossil.get() == null ? 1.0 : (16.0 / 3.0);
	}

	@Override
	public IDataComponent<Fossil> fossil() {
		return fossil;
	}

	@Override
	public IDataComponent<BuriedTreasure> buriedTreasure() {
		return buriedTreasure;
	}

	@Override
	public IDataComponent<FirstPortal> firstPortal() {
		return firstPortal;
	}

	@Override
	public boolean hasDivine() {
		return fossil.get() != null || buriedTreasure.get() != null || firstPortal.get() != null;
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
		if (fossil.get() == null) {
			double multiplier = r / Coords.dist(x, z, 0, 0);
			return new BlindPosition(x * multiplier, z * multiplier);
		}
		int n = Ring.get(0).numStrongholds;
		double minDist2 = Double.MAX_VALUE;
		int angleIndex = -4 + fossil.get().x;
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
			simulator.reset();
			if (fossil.get() != null) {
				simulator.addDivineObject(fossil.get());
			}
			if (buriedTreasure.get() != null) {
				simulator.addDivineObject(buriedTreasure.get());
			}
			if (firstPortal.get() != null) {
				simulator.addDivineObject(firstPortal.get());
			}
			System.out.println("Updating " + simulator.divineObjects.size() + " divine objects");
			for (int i = 0; i < 100000; i++) {
				double phi = simulator.nextAngle();
				addDensityThreeStrongholds(phi, 1);
			}
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
