import java.util.ArrayList;

class Point {
	protected double[] pos;
	protected int dimension;

	public Point(int size) {
		this.pos = new double[size];
		this.dimension = size;
	}

	public Point(double[] p) {
		this.pos = p.clone(); // Defensive copy for immutability
		this.dimension = pos.length;
	}

	int getDimension() {
		return this.dimension;
	}

	double[] getPosition() {
		return pos.clone(); // Defensive copy to prevent external modification
	}

	public static double euclideanDistance(Point p1, Point p2) {
		if (p1.dimension != p2.dimension)
			throw new IllegalArgumentException("Points must have the same dimension.");
		double sum = 0.0;
		for (int i = 0; i < p1.dimension; ++i) {
			double diff = p1.pos[i] - p2.pos[i];
			sum += diff * diff;
		}
		return Math.sqrt(sum);
	}

	public static double squareDistance(Point p1, Point p2) {
		if (p1.dimension != p2.dimension)
			throw new IllegalArgumentException("Points must have the same dimension.");
		double sum = 0.0;
		for (int i = 0; i < p1.dimension; ++i) {
			double diff = p1.pos[i] - p2.pos[i];
			sum += diff * diff;
		}
		return sum;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Point))
			return false;
		Point p = (Point) o;
		if (this.dimension != p.dimension)
			return false;
		for (int i = 0; i < this.dimension; i++)
			if (Double.compare(this.pos[i], p.pos[i]) != 0)
				return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = dimension;
		for (double v : pos) {
			long bits = Double.doubleToLongBits(v);
			result = 31 * result + (int) (bits ^ (bits >>> 32));
		}
		return result;
	}

}
