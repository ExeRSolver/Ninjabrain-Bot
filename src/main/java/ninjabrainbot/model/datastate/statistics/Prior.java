package ninjabrainbot.model.datastate.statistics;

import java.util.Arrays;
import java.util.HashMap;

import ninjabrainbot.model.datastate.divine.IDivineContext;
import ninjabrainbot.model.datastate.stronghold.*;
import ninjabrainbot.util.Coords;
import ninjabrainbot.util.Logger;

public class Prior implements IPrior {

	int size1d;
	final int radius;
	int x0, z0, x1, z1;
	Chunk[] chunks;
	final IDivineContext divineContext;
	final boolean version1_13Plus;

	public Prior(int centerX, int centerZ, int radius, IDivineContext divineContext, boolean version1_13Plus) {
		this(centerX, centerZ, radius, divineContext, version1_13Plus, 5);
	}

	public Prior(int centerX, int centerZ, int radius, IDivineContext divineContext, boolean version1_13Plus, int discretisationPoints) {
		long t0 = System.currentTimeMillis();
		this.divineContext = divineContext;
		this.version1_13Plus = version1_13Plus;
		this.radius = radius;
		setInitialSize(centerX, centerZ);
		chunks = new Chunk[size1d * size1d];
		for (int i = x0; i <= x1; i++) {
			for (int j = z0; j <= z1; j++) {
				int idx = idx(i, j);
				chunks[idx] = new Chunk(i, j);
			}
		}
		setInitialWeights(discretisationPoints);
		smoothWeights();
		Logger.log("Time to construct prior: " + (System.currentTimeMillis() - t0) / 1000f + " seconds.");
	}

	/**
	 * Calculates weights (prior probabilities) for all chunks in the domain.
	 */
	protected void setInitialWeights(int discretisationPoints) {
		RingIterator ringIterator = new RingIterator();
		for (Ring ring : ringIterator) {
			int c0 = (int) ring.innerRadius;
			int c1 = (int) ring.outerRadius;
			int xStart = Math.max(-c1, x0);
			int xEnd = Math.min(c1, x1);
			int zStart = Math.max(-c1, z0);
			int zEnd = Math.min(c1, z1);
			int innerThresholdSqr = (c0 - 1) * (c0 - 1);
			int outerThresholdSqr = (c1 + 1) * (c1 + 1);
			for (int i = xStart; i <= xEnd; i++) {
				for (int j = zStart; j <= zEnd; j++) {
					int distanceSqr = i * i + j * j;
					if (distanceSqr < innerThresholdSqr || distanceSqr > outerThresholdSqr)
						continue;
					// Approximate integral by discretising the chunk into 4 points (centered at
					// (0,0), not (8,8))
					double weight = 0;
					if (discretisationPoints == 1) {
						weight = strongholdDensity(i, j, ring);
					} else {
						for (int k = 0; k < discretisationPoints; k++) {
							double x = i - 0.5 + k / (discretisationPoints - 1.0);
							for (int l = 0; l < discretisationPoints; l++) {
								double z = j - 0.5 + l / (discretisationPoints - 1.0);
								weight += strongholdDensity(x, z, ring);
							}
						}
						weight /= (double) discretisationPoints * discretisationPoints; // Approximate percentage of chunk that's inside the ring
					}
					chunks[idx(i, j)].weight = weight;
				}
			}
		}
	}

	/**
	 * Returns offset weights for biome snapping.
	 */
	protected static HashMap<Integer, Integer> getOffsetWeights() {
		HashMap<Integer, Integer> offsetWeights = new HashMap<>();
		for (int i = -26; i <= 30; i++) {
			int chunkOffset = i >> 2;
			offsetWeights.put(-chunkOffset, offsetWeights.getOrDefault(-chunkOffset, 0) + 1);
		}
		return offsetWeights;
	}

	/**
	 * Simulates biome snapping to smooth the weights.
	 */
	protected void smoothWeights() {
		Chunk[] oldChunks = chunks;
		int oldSize1d = size1d;
		int oldX0 = x0;
		int oldZ0 = z0;
		setSize((x0 + x1) / 2, (z0 + z1) / 2);
		chunks = new Chunk[size1d * size1d];
		for (int i = x0; i <= x1; i++) {
			for (int j = z0; j <= z1; j++) {
				chunks[idx(i, j)] = new Chunk(i, j);
			}
		}
		for (int i = x0; i <= x1; i++) {
			for (int j = z0; j <= z1; j++) {
				if (Ring.get(Math.sqrt(i * i + j * j)) == null)
					continue;
				double w = 0;
				for (int xOffset = -StrongholdConstants.snappingRadius; xOffset <= StrongholdConstants.snappingRadius; xOffset++) {
					for (int zOffset = -StrongholdConstants.snappingRadius; zOffset <= StrongholdConstants.snappingRadius; zOffset++) {
						int chunkIdx = (j + zOffset - oldZ0) + (i + xOffset - oldX0) * oldSize1d;
						double snapProbability = BiomeSnappingDistribution.getProbability(-xOffset, -zOffset, version1_13Plus);
						w += oldChunks[chunkIdx].weight * snapProbability;
					}
				}
				chunks[idx(i, j)].weight = w;
			}
		}
	}

	/**
	 * Density (pdf) of strongholds at chunk coords (cx, cy), in the given ring.
	 */
	protected double strongholdDensity(double cx, double cz, Ring ring) {
		double d2 = cx * cx + cz * cz;
		if (d2 > ring.innerRadius * ring.innerRadius && d2 < ring.outerRadius * ring.outerRadius) {
			double phi = Coords.getPhi(cx, cz);
			double pdfPhi = ring.ring == 0 ? divineContext.getDensityAtAngleBeforeSnapping(phi) : (1.0 / (2.0 * Math.PI));
			double pdfR = ring.numStrongholds / ((ring.outerRadius - ring.innerRadius) * Math.sqrt(d2));
			return pdfR * pdfPhi;
		}
		return 0;
	}

	protected void setInitialSize(int centerX, int centerZ) {
		x0 = centerX - radius - StrongholdConstants.snappingRadius;
		z0 = centerZ - radius - StrongholdConstants.snappingRadius;
		x1 = centerX + radius + StrongholdConstants.snappingRadius;
		z1 = centerZ + radius + StrongholdConstants.snappingRadius;
		size1d = 2 * (radius + StrongholdConstants.snappingRadius) + 1;
	}

	protected void setSize(int centerX, int centerZ) {
		x0 = centerX - radius;
		z0 = centerZ - radius;
		x1 = centerX + radius;
		z1 = centerZ + radius;
		size1d = 2 * radius + 1;
	}

	private int idx(int i, int j) {
		return (j - z0) + (i - x0) * size1d;
	}

	@Override
	public Iterable<Chunk> getChunks() {
		return Arrays.asList(chunks);
	}

	/**
	 * Test the accuracy against a target prior with a different number of discretisation points.
	 */
	public void evaluateError(int discretisationPoints) {
		Logger.log("Evaluating test prior.");
		Logger.log("Constructing target prior...");
		Prior prior = new Prior((x1 + x0) / 2, (z1 + z0) / 2, radius, divineContext, version1_13Plus, discretisationPoints);
		Logger.log("Comparing to target prior...");
		double largestRelError = 0;
		double largestError = 0;
		double sump = 0;
		double sum = 0;
		int falseNegativeCount = 0;
		int falsePositiveCount = 0;
		double falsePositiveWeight = 0;
		// ArrayList<Pair<Double, String>> errors = new ArrayList<>();
		double totalSquaredError = 0;
		int numNonZeroChunks = 0;
		for (int i = 0; i < chunks.length; i++) {
			if (prior.chunks[i].weight != 0) {
				if (chunks[i].weight == 0) {
					Logger.log("x: " + (i % size1d - radius) + ", z: " + (i / size1d - radius));
					Logger.log(prior.chunks[i].weight);
					falseNegativeCount++;
				}
				double relError = chunks[i].weight / prior.chunks[i].weight;
				if (relError < 1f)
					relError = 1f / relError;
				relError -= 1f;
				if (relError > largestRelError)
					largestRelError = relError;
				numNonZeroChunks++;
				double error = prior.chunks[i].weight - chunks[i].weight;
				if (Math.abs(error) > largestError) {
					largestError = Math.abs(error);
				}
				totalSquaredError += error * error;
			}
			else if (chunks[i].weight != 0) {
				++falsePositiveCount;
				falsePositiveWeight += chunks[i].weight;
			}
			sump += prior.chunks[i].weight;
			sum += chunks[i].weight;
		}
		Logger.log("Average non-zero weight: " + sum / numNonZeroChunks);
		Logger.log("Root-mean-square error (on non-zero weights): " + Math.sqrt(totalSquaredError / numNonZeroChunks));
		Logger.log("Largest relative error: " + largestRelError);
		Logger.log("Largest error: " + largestError);
		Logger.log("False negative count: " + falseNegativeCount);
		Logger.log("False positive count: " + falsePositiveCount);
		Logger.log("False positive weight: " + falsePositiveWeight);
		Logger.log("Target prior sum: " + sump);
		Logger.log("Test prior sum: " + sum);
	}

}
