package ninjabrainbot.data.divine;

public class FirstPortal implements IDivinable {

	public final int orientation;

	public FirstPortal(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * Returns a FirstPortal object if the given string is the result of an F3+C command
	 * with pitch between 0 and 10 degrees, null otherwise.
	 */
	public static FirstPortal parseF3C(String string) {
		if (!string.startsWith("/execute in minecraft:"))
			return null;
		String[] substrings = string.split(" ");
		if (substrings.length != 11)
			return null;
		try {
			double yaw = Double.parseDouble(substrings[9]);
			double pitch = Double.parseDouble(substrings[10]);
			if (0 < pitch && pitch < 10) {
				yaw = ((yaw % 360) + 360) % 360;
				int orientation = 3;
				if (yaw >= 45 && yaw < 135) {
					orientation = 2;
				}
				else if (yaw >= 135 && yaw < 225) {
					orientation = 1;
				}
				else if (yaw >= 225 && yaw < 315) {
					orientation = 0;
				}
				return new FirstPortal(orientation);
			}
			return null;
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}
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
