import java.io.IOException;
import java.util.Map.Entry;

public class Start {

    public static void main(String[] args) {
        try {
            String path = "Data_with_filename.csv";
            Process.loadData(path);

            Entry<Integer[], Double> result = Process.cluster(4);

            // Optional: Print clustering result summary
            System.out.println("Clustering evaluation score: " + result.getValue());
            System.out.println("Cluster assignments: " + java.util.Arrays.toString(result.getKey()));
        } catch (IOException e) {
            System.out.println("Problem in loading the file: " + e.getMessage());
        }
    }
}
