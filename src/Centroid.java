import java.util.ArrayList;

class Centroid extends Point {
    private ArrayList<Integer> clusterPoints;
    private double[] sumOfPoints;

    Centroid(Point p) {
        super(p.pos);
        clusterPoints = new ArrayList<>();
        this.sumOfPoints = new double[this.dimension];
    }

    void addPointToCluster(int index) {
        Point p = Process.INSTANCES.get(index);
        clusterPoints.add(index);
        double[] po = p.getPosition();
        for (int i = 0, dim = this.dimension; i < dim; ++i) {
            sumOfPoints[i] += po[i];
        }
    }

    Centroid getNewCenter() {
        int clusterSize = this.clusterPoints.size();
        double[] pos = new double[Process.DIMENSION];
        if (clusterSize == 0) {
            // Return the same centroid if no points assigned
            System.arraycopy(this.pos, 0, pos, 0, this.dimension);
        } else {
            for (int i = 0, dim = this.dimension; i < dim; ++i) {
                pos[i] = sumOfPoints[i] / clusterSize;
            }
        }
        return new Centroid(new Point(pos));
    }

    double evaluate() {
        double ret = 0.0;
        for (int in : clusterPoints) {
            ret += Point.squareDistance(Process.INSTANCES.get(in), this);
        }
        return ret;
    }

    ArrayList<Integer> belongedPoints() {
        return new ArrayList<>(this.clusterPoints);
    }

    void clearCluster() {
        clusterPoints.clear();
        for (int i = 0, dim = this.dimension; i < dim; ++i) {
            sumOfPoints[i] = 0.0;
        }
    }
}
