package ninjabrainbot.model.datastate.divine;

public class FirstPortal implements IDivinable {

	public final int orientation;

	public FirstPortal(int orientation) {
		this.orientation = orientation;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof FirstPortal && ((FirstPortal) obj).orientation == orientation;
	}

	@Override
	public boolean seedSatisfiesDivineCondition(long seed) {
		this.random.setSeed(seed);
		return this.random.nextInt(4) == this.orientation;
	}

}
