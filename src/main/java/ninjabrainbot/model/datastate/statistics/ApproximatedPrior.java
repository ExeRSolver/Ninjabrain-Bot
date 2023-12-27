package ninjabrainbot.model.datastate.statistics;

import ninjabrainbot.model.datastate.divine.IDivineContext;
import ninjabrainbot.model.datastate.stronghold.Ring;
import ninjabrainbot.model.datastate.stronghold.RingIterator;
import ninjabrainbot.model.datastate.stronghold.StrongholdConstants;
import ninjabrainbot.util.Coords;
import ninjabrainbot.util.Logger;

/**
 * A prior that approximates biome snapping as opposed to calculating it
 * exactly, which is costly.
 */
public class ApproximatedPrior extends Prior {

	public ApproximatedPrior(int centerX, int centerZ, int radius, IDivineContext divineContext) {
		super(centerX, centerZ, radius, divineContext);
	}

	/**
	 * Test the accuracy of of the approximated prior.
	 */
	public void evaluateError() {
		Logger.log("Evaluating approximated prior.");
		Logger.log("Constructing true prior...");
		Prior prior = new Prior((x1 + x0) / 2, (z1 + z0) / 2, radius, divineContext);
		Logger.log("Comparing approximation to true prior...");
		double largestRelError = 0;
		double largestError = 0;
		double sump = 0;
		double sum = 0;
		int falseNegativeCount = 0;
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
			sump += prior.chunks[i].weight;
			sum += chunks[i].weight;
		}
		Logger.log("Average non-zero weight: " + sum / numNonZeroChunks);
		Logger.log("Root-mean-square error (on non-zero weights): " + Math.sqrt(totalSquaredError / numNonZeroChunks));
		Logger.log("Largest relative error: " + largestRelError);
		Logger.log("Largest error: " + largestError);
		Logger.log("False negative count: " + falseNegativeCount);
		Logger.log("Prior sum: " + sump);
		Logger.log("Approx prior sum: " + sum);
		RingIterator ringIterator = new RingIterator();
		Ring ring = ringIterator.next();
		Logger.log("Density at 1600: Approx: " + strongholdDensity(100, 0, ring) + ", True (pre snapping): " + super.strongholdDensity(100, 0, ring));
		ring = ringIterator.next();
		ring = ringIterator.next();
		Logger.log("Density at 8000: Approx: " + strongholdDensity(500, 0, ring) + ", True (pre snapping): " + super.strongholdDensity(500, 0, ring));
	}

	@Override
	protected double strongholdDensity(double cx, double cz, Ring ring) {
		double d2 = cx * cx + cz * cz;
		double relativeWeight = 1.0;
		if (ring.ring == 0 && divineContext.hasDivine()) {
			int m = StrongholdConstants.snappingRadius;
			double w = 0;
			for (int i = -m; i <= m; i++) {
				for (int j = -m; j <= m; j++) {
					w += divineContext.getDensityAtAngleBeforeSnapping(Coords.getPhi(cx + i, cz + j));
				}
			}
			w /= (2 * m + 1) * (2 * m + 1);
			relativeWeight *= w * 2.0 * Math.PI;
		}
		// Post snapping circle radiuses (dont have to be exact, tighter margins only
		// affect performance, not the result)
		double c0_ps = ring.innerRadius - 2 * StrongholdConstants.snappingRadius;
		double c1_ps = ring.outerRadius + 2 * StrongholdConstants.snappingRadius;
		if (d2 < c0_ps * c0_ps || d2 > c1_ps * c1_ps)
			return 0;
		return relativeWeight * ApproximatedDensity.density(cx, cz);
	}

	@Override
	protected int discretisationPointsPerChunkSide() {
		return 2;
	}

	@Override
	protected int margin() {
		return StrongholdConstants.snappingRadius;
	}

	@Override
	protected void setInitialSize(int centerX, int centerZ, int radius) {
		setSize(centerX, centerZ, radius);
	}

	@Override
	protected void setInitialWeights() {
		super.setInitialWeights();
	}

	@Override
	protected void smoothWeights() {
		// Skip biome snapping (already accounted for by approximation)
	}

}
