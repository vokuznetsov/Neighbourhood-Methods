package hse.lab.neighbourhood.method;

import com.rits.cloning.Cloner;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;


public class GeneralVNS {

    private RealMatrix realMatrix;
    private Result finalResult = new Result();

    public GeneralVNS(RealMatrix realMatrix) {
        this.realMatrix = realMatrix;
    }

    public Result generalVNS(int kMax, int lMax) {

        for (int i = 1; i <= kMax; i++) {
            List<Matrix> clusters = shaking(i);
            if (clusters != null) {
                List<Matrix> bestClusters = vnd(clusters, lMax);
                if (bestClusters == null) {
                    printMaxGroupEfficacy(finalResult);
                    return finalResult;
                }

                double ge = groupingEfficacy(bestClusters);
                if (ge > finalResult.getGe()) {
                    finalResult.setGe(ge);
                    finalResult.setCluster(bestClusters);
                    finalResult.setNumberOfCluster(i);
                    i = 1;
                }
            }
        }
        printMaxGroupEfficacy(finalResult);
        return finalResult;
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
                    clusters.add(new Matrix(getClusterMatrix(start, end, end)));
                } else {
                    int endRow = realMatrix.getRowDimension();
                    int endColumn = realMatrix.getColumnDimension();
                    clusters.add(new Matrix(getClusterMatrix(start, endRow, endColumn)));
                }
            }
            return clusters;
        }
        return null;
    }

    private Map<Integer, List<Integer>> getClusterMatrix(int startIndex, int endRowIndex, int endColumnIndex) {
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
                Result rowResult = getBestRowCluster(iteratedCluster);
                Result columnResult = getBestColumnCluster(iteratedCluster);

                double maxGE = Math.max(rowResult.getGe(), columnResult.getGe());
                if (maxGE > groupingEfficacy) {
                    groupingEfficacy = maxGE;
                    bestAddingRowCluster = maxGE == rowResult.getGe() ? rowResult.getCluster() : columnResult.getCluster();
                    l = 0;
                }
            }
            return bestAddingRowCluster;
        }
    }

    private void printMaxGroupEfficacy(Result result) {
        System.out.println("\n\n----------------MAX GROUPING EFFICACY----------------");
        System.out.println("Max group efficacy equals " + result.getGe());
        System.out.println("Number of cluster(s) is " + result.getNumberOfCluster());
        System.out.println("Cluster with max GE is \n" + result.getCluster());
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

    private Result getBestRowCluster(List<Matrix> iteratedCluster) {

        Result result = new Result(iteratedCluster);
        List<Matrix> rowChangedClusters;

        for (int i = 1; i <= realMatrix.getRowDimension(); i++) {
            rowChangedClusters = addRow(iteratedCluster, i);
            double rowGE = groupingEfficacy(rowChangedClusters);
            if (rowGE > result.getGe()) {
                result.setGe(rowGE);
                result.setCluster(rowChangedClusters);
            }
        }
        return result;
    }

    private Result getBestColumnCluster(List<Matrix> iteratedCluster) {

        Result result = new Result(iteratedCluster);
        List<Matrix> columnChangedClusters;

        for (int i = 1; i <= realMatrix.getColumnDimension(); i++) {
            columnChangedClusters = addColumn(iteratedCluster, i);
            double columnGE = groupingEfficacy(columnChangedClusters);
            if (columnGE > result.getGe()) {
                result.setGe(columnGE);
                result.setCluster(columnChangedClusters);
            }
        }
        return result;
    }
}
