package ninjabrainbot.model.datastate.divine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DivineMonteCarloSimulator {

	private final Random seedRng;
	private final Random strongholdAngleRng;

	private int seedsRemaining = 100_000_000;

	public final List<IDivinable> divineObjects = new ArrayList<>();

	public DivineMonteCarloSimulator() {
		seedRng = new Random();
		strongholdAngleRng = new Random();
	}

	public boolean addDivineObject(IDivinable divineObject) {
		if (divineObjects.contains(divineObject))
			return false;
		divineObjects.add(divineObject);
		return true;
	}

	public void reset() {
		divineObjects.clear();
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
		return (strongholdAngleRng.nextDouble() * 3.141592653589793D * 2.0D + 3 * Math.PI / 2) % (2 * Math.PI);
	}

	private boolean seedSatisfiesAllDivineObjectConditions(long seed) {
		return divineObjects.stream().allMatch(object -> object.seedSatisfiesDivineCondition(seed));
	}

}
