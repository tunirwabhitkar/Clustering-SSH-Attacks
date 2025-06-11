import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

class LoadProperties {
    static String load(String p) {
        Properties prop = new Properties();
        try (InputStream is = LoadProperties.class.getResourceAsStream("conf.properties")) {
            if (is == null) {
                throw new IOException("conf.properties not found in classpath.");
            }
            prop.load(new BufferedReader(new InputStreamReader(is)));
            return prop.getProperty(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

public class Process {
    static ArrayList<Centroid> CENTERS = new ArrayList<>();
    static ArrayList<Point> INSTANCES = new ArrayList<>();
    static ArrayList<Centroid> PRE_CENS;
    static int DIMENSION;
    static final int MAX_INSTANCE_NUM_NOT_SPLIT = Integer.parseInt(LoadProperties.load("max_instances_num_not_split"));
    static final int TRY_TIMES = Integer.parseInt(LoadProperties.load("try_times"));
    // Map cluster center results to its evaluation
    static ArrayList<Entry<ArrayList<Centroid>, Double>> RESULTS = new ArrayList<>(TRY_TIMES);

    static boolean converge() {
        if (PRE_CENS == null || PRE_CENS.size() != CENTERS.size())
            return false;
        for (int i = 0; i < CENTERS.size(); i++) {
            if (!CENTERS.get(i).equals(PRE_CENS.get(i)))
                return false;
        }
        return true;
    }

    // Loads dataset and builds internal data structures.
    public static void loadData(String path) throws IOException {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
            String line = r.readLine();
            while ((line = r.readLine()) != null) {
                String[] fs = line.split(",");
                int l = fs.length - 1;
                double[] pos = new double[l];
                for (int j = 0; j < l; j++) {
                    pos[j] = Double.parseDouble(fs[j]);
                }
                Process.DIMENSION = l;
                Process.INSTANCES.add(new Point(pos));
            }
        }
    }

    static double evaluate(ArrayList<Centroid> cens) {
        double ret = 0.0;
        for (Centroid cc : cens) {
            ret += cc.evaluate();
        }
        return ret;
    }

    public static Entry<Integer[], Double> cluster(int k) {
        RESULTS.clear();
        for (int t = 0; t < Process.TRY_TIMES; t++) {
            // Randomly pick the cluster centers
            CENTERS.clear();
            PRE_CENS = null;
            Random rand = new Random();
            HashSet<Integer> rSet = new HashSet<>();
            int size = INSTANCES.size();
            while (rSet.size() < k) {
                rSet.add(rand.nextInt(size));
            }
            for (int index : rSet) {
                Process.CENTERS.add(new Centroid(Process.INSTANCES.get(index)));
            }

            boolean changed;
            do {
                // Clear clusters before assignment
                for (Centroid cc : CENTERS) {
                    cc.clearCluster();
                }
                // Assign points to nearest centroid
                for (int i = 0; i < INSTANCES.size(); i++) {
                    Point p = INSTANCES.get(i);
                    int nearest = 0;
                    double minDist = Double.MAX_VALUE;
                    for (int c = 0; c < CENTERS.size(); c++) {
                        double dist = Point.squareDistance(p, CENTERS.get(c));
                        if (dist < minDist) {
                            minDist = dist;
                            nearest = c;
                        }
                    }
                    CENTERS.get(nearest).addPointToCluster(i);
                }
                // Save previous centroids
                PRE_CENS = new ArrayList<>(CENTERS);
                // Update centroids
                ArrayList<Centroid> newCenters = new ArrayList<>();
                for (Centroid cc : CENTERS) {
                    newCenters.add(cc.getNewCenter());
                }
                changed = false;
                for (int i = 0; i < CENTERS.size(); i++) {
                    if (!CENTERS.get(i).equals(newCenters.get(i))) {
                        changed = true;
                        break;
                    }
                }
                CENTERS = newCenters;
            } while (changed);

            Process.RESULTS.add(new SimpleEntry<>(PRE_CENS, Process.evaluate(PRE_CENS)));
        }

        double minEvaluate = Double.MAX_VALUE;
        int minIndex = 0, i = 0;
        for (Entry<ArrayList<Centroid>, Double> entry : RESULTS) {
            double e = entry.getValue();
            if (e < minEvaluate) {
                minEvaluate = e;
                minIndex = i;
            }
            i++;
        }
        CENTERS = RESULTS.get(minIndex).getKey();
        double evaluate = RESULTS.get(minIndex).getValue();

        Integer[] ret = new Integer[INSTANCES.size()];
        for (int cNum = 0; cNum < CENTERS.size(); cNum++) {
            Centroid cc = CENTERS.get(cNum);
            for (int pi : cc.belongedPoints()) {
                ret[pi] = cNum;
            }
        }
        return new SimpleEntry<>(ret, evaluate);
    }
}
