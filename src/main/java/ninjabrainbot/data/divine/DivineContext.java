package ninjabrainbot.data.divine;

import ninjabrainbot.data.blind.BlindPosition;
import ninjabrainbot.data.datalock.IModificationLock;
import ninjabrainbot.data.datalock.LockableField;
import ninjabrainbot.data.statistics.DiscretizedDensity;
import ninjabrainbot.data.stronghold.Ring;
import ninjabrainbot.event.ISubscribable;
import ninjabrainbot.event.ObservableField;
import ninjabrainbot.event.ObservableProperty;
import ninjabrainbot.util.Coords;

public class DivineContext implements IDivineContext {

	private DivineMonteCarloSimulator simulator;

	private DiscretizedDensity discretizedAngularDensity;

	private ObservableField<Fossil> fossil;
	private ObservableField<FirstPortal> firstPortal;
	private ObservableField<BuriedTreasure> buriedTreasure;
	private ObservableProperty<DivineContext> whenPhiDistributionChanged;

	public DivineContext(IModificationLock modificationLock) {
		fossil = new LockableField<Fossil>(modificationLock);
		buriedTreasure = new LockableField<BuriedTreasure>(modificationLock);
		firstPortal = new LockableField<FirstPortal>(modificationLock);
		discretizedAngularDensity = new DiscretizedDensity(0, 2.0 * Math.PI);
		simulator = new DivineMonteCarloSimulator();
		whenPhiDistributionChanged = new ObservableProperty<>();
	}

	@Override
	public Fossil getFossil() {
		return fossil.get();
	}

	@Override
	public void reset() {
		fossil.set(null);
		buriedTreasure.set(null);
		firstPortal.set(null);
		simulator.reset();
		onChanged();
	}

	@Override
	public double relativeDensity() {
		return fossil.get() == null ? 1.0 : (16.0 / 3.0);
	}

	@Override
	public ISubscribable<FirstPortal> whenFirstPortalChanged() {
		return firstPortal;
	}

	@Override
	public ISubscribable<BuriedTreasure> whenBuriedTreasureChanged() {
		return buriedTreasure;
	}

	@Override
	public ISubscribable<Fossil> whenFossilChanged() {
		return fossil;
	}

	@Override
	public ISubscribable<DivineContext> whenPhiDistributionChanged() {
		return whenPhiDistributionChanged;
	}

	public void setFossil(Fossil f) {
		if (!f.equals(fossil.get())) {
			System.out.println("Added fossil: " + f.x);
			simulator.removeDivineObject(fossil.get());
			simulator.addDivineObject(f);
			fossil.set(f);
			onChanged();
		}
	}

	public void setFirstPortal(FirstPortal fp) {
		System.out.println("Added first portal: " + fp.orientation);
		simulator.removeDivineObject(firstPortal.get());
		simulator.addDivineObject(fp);
		firstPortal.set(fp);
		onChanged();
	}

	public void setBuriedTreasure(BuriedTreasure bt) {
		if (simulator.addDivineObject(bt)) {
			System.out.println("Added buried treasure: " + bt.x + " " + bt.z);
			buriedTreasure.set(bt);
			onChanged();
		}
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
			System.out.println("Updating " + simulator.divineObjects.size() + " divine objects");
			for (int i = 0; i < 100000; i++) {
				double phi = simulator.nextAngle();
				addDensityThreeStrongholds(phi, 1);
			}
		}
		discretizedAngularDensity.normalize();
		whenPhiDistributionChanged.notifySubscribers(this);
	}

	private void addDensityThreeStrongholds(double phi, double density) {
		for (int i = 0; i < 3; i++) {
			if (phi > 2 * Math.PI)
				phi -= 2 * Math.PI;
			discretizedAngularDensity.addDensity(phi, 1.0);
			phi += 2.0 / 3.0 * Math.PI;
		}
	}

}
