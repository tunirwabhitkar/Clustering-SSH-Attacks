import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;

import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class Cluster {

    static final int NUM_CLUSTERS = 3;
    static final int NUM_ATTRS = 14;
    static int[][] cluster = new int[NUM_CLUSTERS][2];
    static double[] avgPackets = new double[NUM_CLUSTERS];
    static int[] instanceCount = new int[NUM_CLUSTERS];

    public static BufferedReader readDataFile(String filename) {
        try {
            return new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        SimpleKMeans kmeans = new SimpleKMeans();
        kmeans.setSeed(10);
        kmeans.setPreserveInstancesOrder(true);
        kmeans.setNumClusters(NUM_CLUSTERS);

        BufferedReader datafile = readDataFile("Data_with_filename.arff");
        if (datafile == null) {
            System.err.println("Could not open data file.");
            return;
        }

        Remove remove = new Remove();
        remove.setOptions(new String[]{"-R", "15"});
        Instances data = new Instances(datafile);
        String[] pcapFileName = new String[data.numInstances()];

        remove.setInputFormat(data);
        Instances newData = Filter.useFilter(data, remove);

        kmeans.buildClusterer(newData);

        double squaredSumError = kmeans.getSquaredError();
        System.out.println("Squared sum error of clusters is :" + squaredSumError);

        try (
            PrintWriter writer = new PrintWriter("clusterAssignments.txt", "UTF-8");
            PrintWriter writer1 = new PrintWriter("clusterAnalysis.txt", "UTF-8")
        ) {
            int[] assignments = kmeans.getAssignments();

            String src = "C:/Users/ashk43712/Documents/Summer Term/CS F468/SSH_Sessions/22/";
            String path = "C:/Users/ashk43712/Documents/Summer Term/CS F468/SSH_Sessions/22/Cluster";

            System.out.println("Deleting existing folders...");
            for (int i = 0; i < NUM_CLUSTERS; i++) {
                File f = new File(path + i);
                if (f.exists()) {
                    String[] entries = f.list();
                    if (entries != null) {
                        for (String s : entries) {
                            File currentFile = new File(f.getPath(), s);
                            currentFile.delete();
                        }
                    }
                    if (!f.delete()) {
                        System.err.println("Could not delete folder: " + f.getPath());
                    }
                }
            }

            System.out.println("Creating new folders...");
            for (int i = 0; i < NUM_CLUSTERS; i++) {
                new File(path + i).mkdir();
            }

            for (int i = 0; i < NUM_CLUSTERS; i++) {
                cluster[i][0] = Integer.MAX_VALUE;
                cluster[i][1] = Integer.MIN_VALUE;
                avgPackets[i] = 0.0;
                instanceCount[i] = 0;
            }

            int i = 0;
            int corruptedFile = 0;
            System.out.println("Copying and overriding files ...");
            for (int clusterNum : assignments) {
                writer.printf("Instance %d -> Cluster %d%n", i, clusterNum);
                Instance temp = data.get(i);
                // Use NUM_ATTRS - 1 for last attribute (filename)
                pcapFileName[i] = temp.stringValue(NUM_ATTRS - 1);

                String source = src + pcapFileName[i];
                source = source.replace("\\", "/");
                String dest = path + clusterNum + "/" + pcapFileName[i];
                dest = dest.replace("\\", "/");

                instanceCount[clusterNum]++;

                final StringBuilder errbuf = new StringBuilder();
                final Pcap pcap = Pcap.openOffline(source, errbuf);

                int packetCount = 0;

                if (pcap != null) {
                    try {
                        final int[] countArr = new int[1];
                        pcap.loop(Pcap.LOOP_INFINITE, new JPacketHandler<StringBuilder>() {
                            public void nextPacket(JPacket packet, StringBuilder errbuf) {
                                countArr[0]++;
                            }
                        }, errbuf);
                        packetCount = countArr[0];
                    } catch (Exception e) {
                        corruptedFile++;
                    }
                    pcap.close();
                } else {
                    corruptedFile++;
                }

                avgPackets[clusterNum] += packetCount;

                cluster[clusterNum][0] = Math.min(packetCount, cluster[clusterNum][0]);
                cluster[clusterNum][1] = Math.max(packetCount, cluster[clusterNum][1]);

                try {
                    Files.copy(new File(source).toPath(), new File(dest).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (NoSuchFileException e) {
                    System.err.println("File not found: " + pcapFileName[i]);
                }

                i++;
            }
            System.out.println("Files Successfully copied.");
            System.out.println("No. of corrupted files= " + corruptedFile);

            for (int c = 0; c < NUM_CLUSTERS; c++) {
                System.out.printf("Minimum no. of packets in cluster %d : %d%n", c, cluster[c][0]);
                System.out.printf("Maximum no. of packets in cluster %d : %d%n", c, cluster[c][1]);
            }

            // Calculate average packets per cluster
            for (int c = 0; c < NUM_CLUSTERS; c++) {
                if (instanceCount[c] > 0) {
                    avgPackets[c] /= instanceCount[c];
                }
            }

            writer.println("Attributes\t\tCluster 0\t\tCluster 1\t\tCluster2");
            writer1.printf("%nAverage Number of packets\t\t%f\t\t%f\t\t%f%n", avgPackets[0], avgPackets[1], avgPackets[2]);
        }
    }
}