package hse.lab.neighbourhood.method;

import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;


public class Main {
    private final static int K_MAX = 40;
    private final static int L_MAX = 150;
    private final static String outputPath = "C:\\Users\\Vladimir\\Documents\\IdeaProjects\\HSE\\Git\\Neighbourhood-Methods\\result\\";

    public static void main(String[] args) {

        List<String> fileNames = new ArrayList<>();
        fileNames.add("#1-Stanfel(1985)-1_30x50.txt");
        fileNames.add("#2-Stanfel(1985)-2_30x50.txt");
        fileNames.add("#3-McCormick(1982)_37x53.txt");
        fileNames.add("#4-King&Nakorinchai_30x90.txt");

        for (int i = 1; i <= fileNames.size(); i++) {
            String fileName = fileNames.get(i-1);
            String path = "/large/" + fileName;

            Parser parser = new Parser();
            RealMatrix matrix = parser.parser(path);
            GeneralVNS generalVNS = new GeneralVNS(matrix);
            Result result = generalVNS.generalVNS(K_MAX, L_MAX);
            result.writeToFile(outputPath + "cfp_" + i + "_sol.txt");
        }
    }
}
