package hse.lab.neighbourhood.method;

import com.rits.cloning.Cloner;
import org.apache.commons.math3.analysis.function.Max;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author vkuzn on 05.11.2016.
 */
public class GeneralVNS {

    private RealMatrix realMatrix;

    private double maxGroupingEfficacy = -1;
    private List<Matrix> clustersWithMaxGE;
    private int numberOFClusters;

    public GeneralVNS(RealMatrix realMatrix) {
        this.realMatrix = realMatrix;
    }

    public void generalVNS(int kMax, int lMax) {

        for (int i = 1; i <= kMax; i++) {
            List<Matrix> clusters = shaking(i);
            if (clusters != null) {
                List<Matrix> bestClusters = vnd(clusters, lMax);
                if (bestClusters == null) {
                    printMaxGroupEfficacy();
                    return;
                }
                double ge = groupingEfficacy(bestClusters);
                if (ge > maxGroupingEfficacy) {
                    maxGroupingEfficacy = ge;
                    clustersWithMaxGE = bestClusters;
                    numberOFClusters = i;
                }
                printResult(bestClusters, i, ge);
            }
            printMaxGroupEfficacy();
        }
    }

    // objective function
    private double groupingEfficacy(List<Matrix> clusters) {

        int totalNumberOne = 0;
        for (int i = 0; i < realMatrix.getRowDimension(); i++) {
            totalNumberOne += Arrays.stream(realMatrix.getRow(i)).filter(value -> value == 1.0).count();
        }

        final int[] numberOneInsideClusters = {0};
        final int[] numberZeroInsideClusters = {0};

        clusters.forEach(matrix -> {
            matrix.getMatrix().keySet().forEach(key -> {
                matrix.getRow(key).forEach(index -> {
                    if (realMatrix.getEntry(key - 1, index - 1) == 1.0)
                        numberOneInsideClusters[0] += 1;
                    else
                        numberZeroInsideClusters[0] += 1;
                });
            });
        });

        return (double) numberOneInsideClusters[0] / (totalNumberOne + numberZeroInsideClusters[0]);
    }

    private List<Matrix> shaking(int amountOfParts) {

        int sizeOfRow = realMatrix.getRowDimension();
        int sizeOfColumn = realMatrix.getColumnDimension();
        int partSize;

        if (amountOfParts <= Math.min(sizeOfRow, sizeOfColumn)) {
            List<Matrix> clusters = new ArrayList<>();
            partSize = sizeOfRow / amountOfParts;

            for (int i = 0; i < amountOfParts; i++) {
                int start = i * partSize + 1;
                if (i != amountOfParts - 1) {
                    int end = i * partSize + partSize;
                    clusters.add(new Matrix(getCluster(start, end, end)));
                } else {
                    int endRow = realMatrix.getRowDimension();
                    int endColumn = realMatrix.getColumnDimension();
                    clusters.add(new Matrix(getCluster(start, endRow, endColumn)));
                }
            }
            return clusters;
        }
        return null;
    }

    private Map<Integer, List<Integer>> getCluster(int startIndex, int endRowIndex, int endColumnIndex) {
        List<Integer> sequenceNumbers = new ArrayList<>();
        Map<Integer, List<Integer>> cluster = new HashMap<>();

        for (int i = startIndex; i <= endColumnIndex; i++) {
            sequenceNumbers.add(i);
        }
        for (int i = startIndex; i <= endRowIndex; i++) {
            cluster.put(i, new ArrayList<>(sequenceNumbers));
        }
        return cluster;
    }

    private List<Matrix> addRow(List<Matrix> clusters, int rowIndex) {

        double ge = groupingEfficacy(clusters);
        List<Matrix> bestCluster = clusters;

        final int[] indexOfMatrix = {0};
        clusters.stream()
                .filter(matrix -> matrix.getMatrix().keySet().contains(rowIndex))
                .findFirst().ifPresent(matrix -> indexOfMatrix[0] = clusters.indexOf(matrix));

        if (clusters.get(indexOfMatrix[0]).getMatrix().size() > 1) {

            for (int i = 0; i < clusters.size(); i++) {
                if (i != indexOfMatrix[0]) {
                    List<Matrix> copyClusters = getClone(clusters);
                    copyClusters.stream().filter(matrix -> matrix.getMatrix().keySet().contains(rowIndex))
                            .forEach(matrix -> matrix.getMatrix().remove(rowIndex));

                    Matrix cluster = copyClusters.get(i);
                    cluster.getMatrix().put(rowIndex, new ArrayList<>(cluster.getColumnIndexes()));
                    double tempGE = groupingEfficacy(copyClusters);
                    if (tempGE > ge) {
                        ge = tempGE;
                        bestCluster = copyClusters;
                    }
                }
            }
        }
        return bestCluster;
    }

    private List<Matrix> addColumn(List<Matrix> clusters, int columnIndex) {
        double ge = groupingEfficacy(clusters);
        List<Matrix> bestCluster = clusters;

        final int[] indexOfMatrix = {0};
        clusters.stream()
                .filter(matrix -> matrix.getColumnIndexes().contains(columnIndex))
                .findFirst().ifPresent(matrix -> indexOfMatrix[0] = clusters.indexOf(matrix));


        if (clusters.get(indexOfMatrix[0]).getColumnIndexes().size() > 1) {

            for (int i = 0; i < clusters.size(); i++) {
                if (i != indexOfMatrix[0]) {
                    List<Matrix> copyClusters = getClone(clusters);
                    copyClusters.stream()
                            .filter(matrix -> matrix.getColumnIndexes().contains(columnIndex))
                            .forEach(matrix -> matrix.getMatrix().keySet()
                                    .forEach(key -> matrix.getRow(key).remove((Integer) columnIndex)));

                    Matrix cluster = copyClusters.get(i);
                    for (Integer index : cluster.getMatrix().keySet()) {
                        cluster.getMatrix().get(index).add(columnIndex);
                    }

                    double tempGE = groupingEfficacy(copyClusters);
                    if (tempGE > ge) {
                        ge = tempGE;
                        bestCluster = copyClusters;
                    }
                }
            }
        }
        return bestCluster;
    }

    private List<Matrix> vnd(List<Matrix> clusters, int lMax) {

        if (clusters != null && clusters.size() <= 1) {
            return clusters;
        } else {
            double groupingEfficacy = -1;
            List<Matrix> bestAddingRowCluster = clusters;

            for (int l = 0; l < lMax; l++) {
                List<Matrix> iteratedCluster = bestAddingRowCluster;
                List<Matrix> rowChangedClusters;
                List<Matrix> columnChangedClusters;

                for (int i = 1; i <= realMatrix.getRowDimension(); i++) {
                    rowChangedClusters = addRow(iteratedCluster, i);
                    double rowGE = groupingEfficacy(rowChangedClusters);
                    if (rowGE > groupingEfficacy) {
                        groupingEfficacy = rowGE;
                        bestAddingRowCluster = rowChangedClusters;
                    }
                }

                for (int i = 1; i <= realMatrix.getColumnDimension(); i++) {
                    columnChangedClusters = addColumn(iteratedCluster, i);
                    double columnGE = groupingEfficacy(columnChangedClusters);
                    if (columnGE > groupingEfficacy) {
                        groupingEfficacy = columnGE;
                        bestAddingRowCluster = columnChangedClusters;
                    }
                }
            }

            return bestAddingRowCluster;
        }
    }

    private void printMaxGroupEfficacy() {
        System.out.println("\n\n----------------MAX GROUPING EFFICACY----------------");
        System.out.println("Max group efficacy equals " + maxGroupingEfficacy);
        System.out.println("Number of cluster(s) is " + numberOFClusters);
        System.out.println("Cluster with max GE is \n" + clustersWithMaxGE);
    }

    private void printResult(List<Matrix> clusters, int numberOfPart, double ge) {
        System.out.println("----------------CLUSTERS----------------");
        System.out.println("Number of parts is " + numberOfPart);
        System.out.println(clusters);
        System.out.println("Grouping efficacy is " + ge + "\n");
    }

    private <T> T getClone(T o) {
        Cloner cloner = new Cloner();
        return cloner.deepClone(o);
    }
}
