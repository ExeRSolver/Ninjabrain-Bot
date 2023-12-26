package ninjabrainbot.data.divine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DivineMonteCarloSimulator {

	private final Random seedRng;
	private final Random strongholdAngleRng;

	public List<IDivinable> divineObjects = new ArrayList<>();

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

	public boolean removeDivineObject(IDivinable divineObject) {
		return divineObjects.remove(divineObject);
	}

	public void reset() {
		divineObjects.clear();
	}

	public double nextAngle() {
		long seed = seedRng.nextLong();
		while (!seedSatisfiesAllDivineObjectConditions(seed)) {
			seed = seedRng.nextLong();
		}
		strongholdAngleRng.setSeed(seed);
		return (strongholdAngleRng.nextDouble() * 3.141592653589793D * 2.0D + 3 * Math.PI / 2) % (2 * Math.PI);
	}

	private boolean seedSatisfiesAllDivineObjectConditions(long seed) {
		return divineObjects.stream().allMatch(object -> object.seedSatisfiesDivineCondition(seed));
	}

}
