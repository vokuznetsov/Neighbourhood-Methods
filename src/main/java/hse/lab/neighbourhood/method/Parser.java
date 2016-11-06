package hse.lab.neighbourhood.method;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Parser {

    private RealMatrix realMatrix;

    public RealMatrix parser(String path) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(openFile(path)));

            // read the size of matrix
            String str = in.readLine().trim();
            int space = str.indexOf(" ");
            realMatrix = new BlockRealMatrix(Integer.parseInt(str.substring(0, space)),
                    Integer.parseInt(str.substring(space + 1, str.length())));

            int count = 0;
            while ((str = in.readLine()) != null) {
                List<String> indexes = new ArrayList<>(Arrays.asList(str.trim().split(" ")));
                indexes.remove(0);

                double[] indexList = new double[realMatrix.getColumnDimension()];
                for (int i = 0; i < realMatrix.getColumnDimension(); i++) {
                    indexList[i] = 0;
                }
                indexes.forEach(index -> indexList[Integer.parseInt(index) - 1] = 1);
                realMatrix.setRow(count++, indexList);
            }
            in.close();
            return realMatrix;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private InputStream openFile(String path) throws FileNotFoundException {
        InputStream is = this.getClass().getResourceAsStream(path);
        return is;
    }
}
