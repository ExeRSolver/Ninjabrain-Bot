package ninjabrainbot.io.mcinstance;

import java.io.File;

public class MinecraftWorldFile implements IMinecraftWorldFile {

	private final MinecraftInstance minecraftInstance;
	// Name can be null if the name has not been found out yet
	private String name;
	private boolean hasEnteredEnd;

	private File endDimensionFile;

	public MinecraftWorldFile(MinecraftInstance minecraftInstance, String name) {
		this.minecraftInstance = minecraftInstance;
		this.name = name;
		hasEnteredEnd = false;
	}

	@Override
	public MinecraftInstance minecraftInstance() {
		return minecraftInstance;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public boolean hasEnteredEnd() {
		return hasEnteredEnd;
	}

	public void setName(String name) {
		this.name = name;
	}

	File getEndDimensionFile() {
		if (endDimensionFile == null && name != null)
			endDimensionFile = new File(minecraftInstance.savesDirectory + "\\" + name + "\\DIM1\\region");
		return endDimensionFile;
	}

	void setHasEnteredEnd(boolean hasEnteredEnd) {
		this.hasEnteredEnd = hasEnteredEnd;
	}

	@Override
	public String toString() {
		return minecraftInstance.savesDirectory + "\\" + name;
	}

}
