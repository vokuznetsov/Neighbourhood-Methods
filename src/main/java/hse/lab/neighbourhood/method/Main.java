package hse.lab.neighbourhood.method;

/**
 * @author vkuzn on 05.11.2016.
 */
public class Main {
    public static void main(String[] args) {

        String path = "/small/cfp_small_1.txt";
        Parser parser = new Parser();

        Matrix matrix = parser.parser(path);
        System.out.println(matrix);
    }
}
