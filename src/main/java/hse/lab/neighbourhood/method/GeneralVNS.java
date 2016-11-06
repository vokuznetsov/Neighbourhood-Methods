package hse.lab.neighbourhood.method;

import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;

/**
 * @author vkuzn on 05.11.2016.
 */
public class GeneralVNS {


    public void generalVNS(RealMatrix originalMatrix, int kMax, int lMax) {

        for (int i = 1; i <= kMax; i++) {
            List<Matrix> clusters = shaking(originalMatrix, i);
            if (clusters == null) {
                return;
            }
            System.out.println("----------------CLUSTERS----------------");
            System.out.println("Number of parts is " + i);
            System.out.println(clusters + "\n");
        }
    }

    private List<Matrix> shaking(RealMatrix originalMatrix, int amountOfParts) {

        int sizeOfRow = originalMatrix.getRowDimension();
        int sizeOfColumn = originalMatrix.getColumnDimension();
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
                    int endRow = originalMatrix.getRowDimension();
                    int endColumn = originalMatrix.getColumnDimension();
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
            cluster.put(i, sequenceNumbers);
        }
        return cluster;
    }
}
