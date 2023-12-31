package ninjabrainbot.model.datastate.divine;

public class BuriedTreasure implements IDivinable {

	public final int x, z;
	public final long salt;

	public BuriedTreasure(int x, int z) {
		this.x = x;
		this.z = z;
		this.salt = x * 341873128712L + z * 132897987541L + 10387320L;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof BuriedTreasure && ((BuriedTreasure) obj).x == x && ((BuriedTreasure) obj).z == z;
	}

	@Override
	public boolean seedSatisfiesDivineCondition(long seed) {
		this.random.setSeed(seed + this.salt);
		return random.nextFloat() < 0.01;
	}

	@Override
	public DivineType divineType() {
		return DivineType.BURIED_TREASURE;
	}

}
