package hse.lab.neighbourhood.method;

import java.util.*;

/**
 * @author vkuzn on 05.11.2016.
 */
public class Matrix {

    private int numberOfRows = 0;
    private int numberOfColumns = 0;
    private Map<Integer, List<Integer>> matrix;

    public Matrix() {
        matrix = new HashMap<>();
    }

    public Matrix(Map<Integer, List<Integer>> matrix) {
        this.matrix = matrix;
        if (!matrix.isEmpty()) {
            Optional<List<Integer>> firstRow = matrix.values().stream().findFirst();
            numberOfColumns = firstRow.isPresent() ? firstRow.get().size() : 0;
            numberOfRows = matrix.size();
        }
        else {
            numberOfRows=0;
            numberOfColumns = 0;
        }
    }

    public Map<Integer, List<Integer>> getMatrix() {
        return matrix;
    }

    public void setMatrix(Map<Integer, List<Integer>> matrix) {
        this.matrix = matrix;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    public void addRow(List<String> indexes) {
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < numberOfColumns; i++) {
            indexList.add(0);
        }

        indexes.forEach(index -> indexList.set(Integer.parseInt(index) - 1, 1));
        matrix.put(matrix.size() + 1, new ArrayList<>(indexList));
    }

    @Override
    public String toString() {
        return "\nMatrix: {\n" +
                "numberOfRows=" + numberOfRows +
                ", numberOfColumns=" + numberOfColumns + ", \n" +
                "matrix=" + matrix +
                "\n}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Matrix matrix1 = (Matrix) o;

        if (numberOfRows != matrix1.numberOfRows) return false;
        if (numberOfColumns != matrix1.numberOfColumns) return false;
        return matrix != null ? matrix.equals(matrix1.matrix) : matrix1.matrix == null;

    }

    @Override
    public int hashCode() {
        int result = numberOfRows;
        result = 31 * result + numberOfColumns;
        result = 31 * result + (matrix != null ? matrix.hashCode() : 0);
        return result;
    }
}
