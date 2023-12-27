package ninjabrainbot.model.datastate.divine;

public class Fossil implements IDivinable {

	public final int x;

	public Fossil(int x) {
		this.x = x;
	}

	/**
	 * Returns a Fossil object if the given string is the result of an F3+I command
	 * in the 0,0 chunk, null otherwise.
	 */
	public static Fossil parseF3I(String string) {
		if (!string.startsWith("/setblock "))
			return null;
		String[] substrings = string.split(" ");
		if (substrings.length != 5)
			return null;
		try {
			int x = Integer.parseInt(substrings[1]);
			int z = Integer.parseInt(substrings[3]);
			if (0 <= x && x < 16 && 0 <= z && z < 16) {
				return new Fossil(x);
			}
			return null;
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}
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

}
