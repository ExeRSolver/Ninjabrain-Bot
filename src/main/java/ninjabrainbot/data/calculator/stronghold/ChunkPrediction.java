package ninjabrainbot.data.calculator.stronghold;

import java.util.Locale;

import ninjabrainbot.data.calculator.common.IPlayerPosition;
import ninjabrainbot.data.calculator.common.StructurePosition;
import ninjabrainbot.data.calculator.endereye.IEnderEyeThrow;
import ninjabrainbot.event.IObservable;
import ninjabrainbot.event.IReadOnlyList;
import ninjabrainbot.io.preferences.MultipleChoicePreferenceDataTypes.McVersion;

public class ChunkPrediction extends StructurePosition {

	public final Chunk chunk;
	public final boolean success;

	private final McVersion version;

	/**
	 * Creates a failed triangulation result.
	 */
	public ChunkPrediction() {
		this(new Chunk(0, 0), null, McVersion.PRE_119);
	}

	/**
	 * Creates a triangulation result.
	 */
	public ChunkPrediction(Chunk chunk, IObservable<IPlayerPosition> playerPosition, McVersion version) {
		super(chunk.fourFourX(), chunk.fourFourZ(), playerPosition);
		this.version = version;
		this.chunk = chunk;
		this.success = Double.isFinite(chunk.weight) && chunk.weight > 0.0005;
	}

	public String formatCertainty() {
		return String.format(Locale.US, "%.1f%%", chunk.weight * 100);
	}

	public double[] getAngleErrors(IReadOnlyList<IEnderEyeThrow> eyeThrows) {
		return chunk.getAngleErrors(version, eyeThrows);
	}

	public double getAngleError(IEnderEyeThrow t) {
		return t == null ? -1 : chunk.getAngleError(version, t);
	}

	public double xInNether() {
		return chunk.x * 2;
	}

	public double zInNether() {
		return chunk.z * 2;
	}


}
