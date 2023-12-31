package ninjabrainbot.model.datastate.divine;

public class Fossil implements IDivinable {

	public final int x;

	public Fossil(int x) {
		this.x = x;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Fossil && ((Fossil) obj).x == x;
	}

	@Override
	public boolean seedSatisfiesDivineCondition(long seed) {
		this.random.setSeed(seed);
		return this.random.nextInt(16) == this.x;
	}

	@Override
	public DivineType divineType() {
		return DivineType.FOSSIL;
	}

}
