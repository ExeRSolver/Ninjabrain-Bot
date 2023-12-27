package ninjabrainbot.model.input;

import ninjabrainbot.model.datastate.divine.Fossil;

public class F3IBlockData {

	public final int x, y, z;

	private F3IBlockData(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static F3IBlockData tryParseF3IString(String string) {
		if (!string.startsWith("/setblock "))
			return null;
		String[] substrings = string.split(" ");
		if (substrings.length != 5)
			return null;
		try {
			int x = Integer.parseInt(substrings[1]);
			int y = Integer.parseInt(substrings[2]);
			int z = Integer.parseInt(substrings[3]);
			return new F3IBlockData(x, y, z);
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}
	}

}
