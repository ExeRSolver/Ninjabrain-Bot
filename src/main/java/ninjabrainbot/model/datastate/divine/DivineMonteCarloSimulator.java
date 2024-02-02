package ninjabrainbot.model.datastate.divine;

import ninjabrainbot.event.IReadOnlyList;

import java.util.Random;

public class DivineMonteCarloSimulator {

	private final Random seedRng;
	private final Random strongholdAngleRng;

	private final IReadOnlyList<IDivinable> divineObjects;

	private int seedsRemaining;

	public DivineMonteCarloSimulator(IReadOnlyList<IDivinable> divineObjects) {
		this.seedRng = new Random();
		this.strongholdAngleRng = new Random();
		this.divineObjects = divineObjects;
		this.seedsRemaining = 100_000_000;
	}

	public void reset() {
		seedsRemaining = 100_000_000;
	}

	public boolean shouldContinue() {
		return seedsRemaining > 0;
	}

	public double nextAngle() {
		long seed = seedRng.nextLong();
		--seedsRemaining;
		while (!seedSatisfiesAllDivineObjectConditions(seed)) {
			seed = seedRng.nextLong();
			--seedsRemaining;
		}
		strongholdAngleRng.setSeed(seed);
		// Stronghold angles are offset 90 degrees from in-game yaw
		return (strongholdAngleRng.nextDouble() * 3.141592653589793D * 2.0D + 3 * Math.PI / 2) % (2 * Math.PI);
	}

	private boolean seedSatisfiesAllDivineObjectConditions(long seed) {
		for (IDivinable divineObject : divineObjects) {
			if (!divineObject.seedSatisfiesDivineCondition(seed))
				return false;
		}
		return true;
	}

}
