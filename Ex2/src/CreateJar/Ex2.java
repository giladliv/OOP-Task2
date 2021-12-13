package CreateJar;

import Simulator.FrameAlgo;
import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import imps.GraphAlgorithms;

import java.util.Arrays;

/**
 * This class is the main class for Ex2 - your implementation will be tested using this class.
 */
public class Ex2
{
    /**
     * This static function will be used to test your implementation
     * @param json_file - a json file (e.g., G1.json - G3.gson)
     * @return
     */
    public static DirectedWeightedGraph getGrapg(String json_file) {
        DirectedWeightedGraphAlgorithms directedWeightedGraphAlgorithms = new GraphAlgorithms();
        if (directedWeightedGraphAlgorithms.load(json_file))
        {
            return directedWeightedGraphAlgorithms.getGraph();
        }
        return null;
    }
    /**
     * This static function will be used to test your implementation
     * @param json_file - a json file (e.g., G1.json - G3.gson)
     * @return
     */
    public static DirectedWeightedGraphAlgorithms getGrapgAlgo(String json_file) {
        DirectedWeightedGraphAlgorithms ans = new GraphAlgorithms();;
        ans.load(json_file);
        // ****** Add your code here ******
        //
        // ********************************
        return ans;
    }
    /**
     * This static function will run your GUI using the json fime.
     * @param json_file - a json file (e.g., G1.json - G3.gson)
     *
     */
    public static void runGUI(String json_file) {
        DirectedWeightedGraphAlgorithms alg = getGrapgAlgo(json_file);
        // ****** Add your code here ******
        //
        // ********************************
        FrameAlgo form = new FrameAlgo(alg);
    }

    public static void main(String[] args)
    {
        if (args.length >= 1)
        {
            runGUI(args[0]);
        }
        else
        {
            System.out.println("Please give a Json file as argument");
        }
    }
}