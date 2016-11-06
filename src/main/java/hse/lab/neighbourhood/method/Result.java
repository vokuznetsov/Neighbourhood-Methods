package hse.lab.neighbourhood.method;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Result {

    private double ge = -1.0;
    private List<Matrix> cluster;
    private int numberOfCluster = 0;

    public Result() {
        cluster = new ArrayList<>();
    }

    public Result(List<Matrix> cluster) {
        this.cluster = cluster;
    }

    public Result(double ge, List<Matrix> cluster) {
        this.ge = ge;
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return "Result{" +
                "ge=" + ge +
                ", cluster=" + cluster +
                ", numberOfCluster=" + numberOfCluster +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (Double.compare(result.ge, ge) != 0) return false;
        if (numberOfCluster != result.numberOfCluster) return false;
        return cluster != null ? cluster.equals(result.cluster) : result.cluster == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(ge);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (cluster != null ? cluster.hashCode() : 0);
        result = 31 * result + numberOfCluster;
        return result;
    }

    public int getNumberOfCluster() {
        return numberOfCluster;
    }

    public void setNumberOfCluster(int numberOfCluster) {
        this.numberOfCluster = numberOfCluster;
    }

    public double getGe() {
        return ge;
    }

    public void setGe(double ge) {
        this.ge = ge;
    }

    public List<Matrix> getCluster() {
        return cluster;
    }

    public void setCluster(List<Matrix> cluster) {
        this.cluster = cluster;
    }

    public void writeToFile(String pathToFile) {
        try {
            System.out.println("\nWriting to file: " + pathToFile);
            // Files.newBufferedWriter() uses UTF-8 encoding by default

            BufferedWriter writer = Files.newBufferedWriter(Paths.get(pathToFile));
            for (Matrix matrix : cluster) {
                StringBuilder stringBuilder = new StringBuilder();
                matrix.getMatrix().keySet().forEach(machine -> stringBuilder.append(machine + " "));
                stringBuilder.append("- ");
                matrix.getColumnIndexes().forEach(part -> stringBuilder.append(part + " "));
                stringBuilder.append("\n");
                writer.write(stringBuilder.toString());
            }
            writer.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
