package ninjabrainbot.model.datastate.statistics;

import ninjabrainbot.io.preferences.enums.McVersion;
import ninjabrainbot.model.datastate.common.IOverworldRay;
import ninjabrainbot.model.datastate.divine.IDivineContext;
import ninjabrainbot.model.datastate.stronghold.BiomeSnappingDistribution;
import ninjabrainbot.model.datastate.stronghold.Chunk;
import ninjabrainbot.model.datastate.stronghold.Ring;
import ninjabrainbot.model.datastate.stronghold.StrongholdConstants;
import ninjabrainbot.util.Coords;
import ninjabrainbot.util.Logger;

import java.util.ArrayList;

/**
 * A prior computed only close to a ray.
 */
public class RayPrior implements IPrior {

	ArrayList<Chunk> chunks;
	private Double[] initialChunkWeights;
	int x0, x1, z0, z1;
	final IDivineContext divineContext;
	static final int discretisationPoints = 2;

	public RayPrior(IOverworldRay r, IDivineContext divineContext, McVersion version, boolean version1_13Plus) {
		this(r, 1.0 / 180.0 * Math.PI, divineContext, version, version1_13Plus); // 1 degree tolerance
	}

	public RayPrior(IOverworldRay r, double tolerance, IDivineContext divineContext, McVersion version, boolean version1_13Plus) {
		long t0 = System.currentTimeMillis();
		this.divineContext = divineContext;
		construct(r, tolerance, version, version1_13Plus);
		Logger.log("Time to construct ray prior: " + (System.currentTimeMillis() - t0) / 1000f + " seconds.");
	}

	private void construct(IOverworldRay r, double tolerance, McVersion version, boolean version1_13Plus) {
		double range = 5000.0 / 16;
		chunks = new ArrayList<>();
		double phi = r.horizontalAngle() / 180.0 * Math.PI;
		// direction vector
		double dx = -Math.sin(phi);
		double dz = Math.cos(phi);
		// boundary vectors
		double ux = -Math.sin(phi - tolerance);
		double uz = Math.cos(phi - tolerance);
		double vx = -Math.sin(phi + tolerance);
		double vz = Math.cos(phi + tolerance);
		// Is the major direction X or Z?
		boolean majorX = Math.cos(phi) * Math.cos(phi) < 0.5;
		boolean majorPositive = majorX ? -Math.sin(phi) > 0 : Math.cos(phi) > 0;
		// Subtract StrongholdChunkCoord to center grid at (8,8) (or 0,0 in 1.19).
		double origin_major = ((majorX ? r.xInOverworld() : r.zInOverworld()) - StrongholdConstants.getStrongholdChunkCoord(version)) / 16.0;
		double origin_minor = ((majorX ? r.zInOverworld() : r.xInOverworld()) - StrongholdConstants.getStrongholdChunkCoord(version)) / 16.0;
		double iter_start_major = getIterStartMajor(origin_major, origin_minor, ux, uz, vx, vz, majorX, majorPositive);
		double uk = majorX ? uz / ux : ux / uz;
		double vk = majorX ? vz / vx : vx / vz;
		boolean rightPositive = majorPositive ? vk - uk > 0 : uk - vk > 0;
		int i = (int) (majorPositive ? Math.ceil(iter_start_major) : Math.floor(iter_start_major));

		this.setSize(iter_start_major, origin_major, origin_minor, dx, dz, uk, vk, range, majorX, majorPositive, rightPositive);

		while ((majorX ? (i - iter_start_major) / dx : (i - iter_start_major) / dz) < range) {
			double minor_u = origin_minor + uk * (i - origin_major);
			double minor_v = origin_minor + vk * (i - origin_major);
			int j = (int) (rightPositive ? Math.ceil(minor_u) : Math.floor(minor_u));
			if (j < -StrongholdConstants.maxChunk)
				j = -StrongholdConstants.maxChunk;
			if (j > StrongholdConstants.maxChunk)
				j = StrongholdConstants.maxChunk;
			for (; rightPositive ? j < minor_v : j > minor_v && j <= StrongholdConstants.maxChunk && j >= -StrongholdConstants.maxChunk; j += rightPositive ? 1 : -1) {
				int cx = majorX ? i : j;
				int cz = majorX ? j : i;
				if (Ring.get(Math.sqrt(cx * cx + cz * cz)) == null)
					continue;

				double weight = 0;
				for (int xOffset = -StrongholdConstants.snappingRadius; xOffset <= StrongholdConstants.snappingRadius; xOffset++) {
					for (int zOffset = -StrongholdConstants.snappingRadius; zOffset <= StrongholdConstants.snappingRadius; zOffset++) {
						weight += getInitialWeight(cx + xOffset, cz + zOffset) * BiomeSnappingDistribution.getProbability(-xOffset, -zOffset, version1_13Plus);
					}
				}
				if (weight != 0) {
					Chunk chunk = new Chunk(cx, cz);
					chunk.weight = weight;
					chunks.add(chunk);
				}
			}
			i += majorPositive ? 1 : -1;
		}
	}

	private double getInitialWeight(int cx, int cz) {
		int idx = this.idx(cx, cz);
		if (this.initialChunkWeights[idx] != null)
			return this.initialChunkWeights[idx];

		double weight = 0;
		if (discretisationPoints == 1) {
			weight = strongholdDensity(cx, cz);
		} else {
			for (int k = 0; k < discretisationPoints; k++) {
				double x = cx - 0.5 + k / (discretisationPoints - 1.0);
				for (int l = 0; l < discretisationPoints; l++) {
					double z = cz - 0.5 + l / (discretisationPoints - 1.0);
					weight += strongholdDensity(x, z);
				}
			}
			weight /= (double) discretisationPoints * discretisationPoints; // Approximate percentage of chunk that's inside the ring
		}
		this.initialChunkWeights[idx] = weight;
		return weight;
	}

	protected double strongholdDensity(double cx, double cz) {
		double distance = Math.sqrt(cx * cx + cz * cz);
		Ring ring = Ring.get(distance);
		if (ring != null && distance > ring.innerRadius && distance < ring.outerRadius) {
			double phi = Coords.getPhi(cx, cz);
			double pdfPhi = ring.ring == 0 ? divineContext.getDensityAtAngleBeforeSnapping(phi) : (1.0 / (2.0 * Math.PI));
			double pdfR = ring.numStrongholds / ((ring.outerRadius - ring.innerRadius) * distance);
			return pdfR * pdfPhi;
		}
		return 0;
	}

	private void setSize(double iter_start_major, double o_major, double o_minor, double dx, double dz, double uk, double vk, double range, boolean majorX, boolean majorPositive, boolean rightPositive) {
		int majorStart, majorEnd, minorStart, minorEnd;
		if (majorPositive) {
			majorStart = (int) Math.ceil(iter_start_major);
			majorEnd = (int) Math.floor((majorX ? dx : dz) * range + iter_start_major);
		}
		else {
			majorStart = (int) Math.ceil((majorX ? dx : dz) * range + iter_start_major);
			majorEnd = (int) Math.floor(iter_start_major);
		}
		if (rightPositive) {
			double minor_u1 = o_minor + uk * (majorStart - o_major);
			double minor_v1 = o_minor + vk * (majorStart - o_major);
			double minor_u2 = o_minor + uk * (majorEnd - o_major);
			double minor_v2 = o_minor + vk * (majorEnd - o_major);
			minorStart = (int) Math.ceil(Math.min(minor_u1, minor_u2));
			minorEnd = (int) Math.floor(Math.max(minor_v1, minor_v2));
		}
		else {
			double minor_u1 = o_minor + uk * (majorStart - o_major);
			double minor_v1 = o_minor + vk * (majorStart - o_major);
			double minor_u2 = o_minor + uk * (majorEnd - o_major);
			double minor_v2 = o_minor + vk * (majorEnd - o_major);
			minorStart = (int) Math.ceil(Math.min(minor_v1, minor_v2));
			minorEnd = (int) Math.floor(Math.max(minor_u1, minor_u2));
		}

		this.x0 = Math.max(majorX ? majorStart : minorStart, -StrongholdConstants.maxChunk) - StrongholdConstants.snappingRadius;
		this.x1 = Math.min(majorX ? majorEnd : minorEnd, StrongholdConstants.maxChunk) + StrongholdConstants.snappingRadius;
		this.z0 = Math.max(majorX ? minorStart : majorStart, -StrongholdConstants.maxChunk) - StrongholdConstants.snappingRadius;
		this.z1 = Math.min(majorX ? minorEnd : majorEnd, StrongholdConstants.maxChunk) + StrongholdConstants.snappingRadius;
		this.initialChunkWeights = new Double[(x1 - x0 + 1) * (z1 - z0 + 1)];
	}

	/**
	 * Returns the major coord at which to start looking for chunks to add to the
	 * prior. If the player is outside the 8th ring this is necessary because the
	 * nearest stronghold might be very far away.
	 */
	private double getIterStartMajor(double o_major, double o_minor, double ux, double uz, double vx, double vz, boolean majorX, boolean majorPositive) {
		if (o_major * o_major + o_minor * o_minor <= StrongholdConstants.maxChunk * StrongholdConstants.maxChunk) // in 8 rings
			return o_major;
		double ox = majorX ? o_major : o_minor;
		double oz = majorX ? o_minor : o_major;
		// Determine if (0,0) is in the frustum
		double u_orth_mag = orthogonalComponent(-ox, -oz, ux, uz);
		double v_orth_mag = orthogonalComponent(-ox, -oz, vx, vz);
		// (0,0) is in the frustum if it is to the right of u and to the left of v
		if (u_orth_mag > 0 && v_orth_mag < 0) {
			// intersection
			double o_mag = Math.sqrt(ox * ox + oz * oz);
			double ix = ox / o_mag * StrongholdConstants.maxChunk;
			double iz = oz / o_mag * StrongholdConstants.maxChunk;
			double m1 = o_major + projectAndGetMajorComponent(ix - ox, iz - oz, ux, uz, majorX);
			double m2 = o_major + projectAndGetMajorComponent(ix - ox, iz - oz, vx, vz, majorX);
			return majorPositive ^ m1 > m2 ? m1 : m2;
		}
		double i_u_major = findCircleIntersection(ox, oz, ux, uz, StrongholdConstants.maxChunk, majorX);
		double i_v_major = findCircleIntersection(ox, oz, vx, vz, StrongholdConstants.maxChunk, majorX);
		if (i_u_major != 0 || i_v_major != 0) {
			if (i_u_major != 0 && i_v_major != 0) {
				return majorPositive ^ i_u_major > i_v_major ? i_u_major : i_v_major;
			}
			if (i_u_major != 0)
				return i_u_major;
			return i_v_major;
		}
		return o_major;
	}

	/**
	 * Returns the magnitude of the vector pointing orthogonally from u to a
	 * (positive = right). u is a unit vector.
	 */
	private double orthogonalComponent(double ax, double az, double ux, double uz) {
		double u_par_mag = ux * ax + uz * az;
		double u_par_x = ux * u_par_mag;
		double u_par_z = uz * u_par_mag;
		double u_orth_x = u_par_x - ax;
		double u_orth_z = u_par_z - az;
		double u_orth_mag = uz * u_orth_x - ux * u_orth_z;
		return u_orth_mag;
	}

	/**
	 * Projects a onto the unit vector u and returns the major component.
	 */
	private double projectAndGetMajorComponent(double ax, double az, double ux, double uz, boolean majorX) {
		double proj_mag = ax * ux + az * uz;
		return majorX ? (ux * proj_mag) : (uz * proj_mag);
	}

	private double findCircleIntersection(double ox, double oz, double ux, double uz, double r, boolean majorX) {
		double o_dot_u = ox * ux + oz * uz;
		double a = o_dot_u * o_dot_u + r * r - ox * ox - oz * oz;
		if (a < 0) // no intersection
			return 0;
		double b = -o_dot_u - Math.sqrt(a);
		return majorX ? ox + b * ux : oz + b * uz;
	}

	private int idx(int i, int j) {
		return (j - z0) + (i - x0) * (z1 - z0 + 1);
	}

	@Override
	public Iterable<Chunk> getChunks() {
		return chunks;
	}

}
