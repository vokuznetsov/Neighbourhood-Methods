package hse.lab.neighbourhood.method;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author vkuzn on 05.11.2016.
 */
public class Parser {

    public Matrix parser(String path) {
        try {
            Matrix matrix = new Matrix();
            BufferedReader in = new BufferedReader(new InputStreamReader(openFile(path)));

            String str = in.readLine();
            int space = str.indexOf(" ");
            matrix.setNumberOfRows(Integer.parseInt(str.substring(0, space)));
            matrix.setNumberOfColumns(Integer.parseInt(str.substring(space + 1, space + 2)));

            while ((str = in.readLine()) != null) {
                List<String> indexes = new ArrayList<>(Arrays.asList(str.trim().split(" ")));
                indexes.remove(0);
                matrix.addRow(indexes);
            }
            return matrix;
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
