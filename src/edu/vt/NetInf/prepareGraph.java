import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class prepareGraph {
    public static void main(String args[]) throws FileNotFoundException {
        Graph<String, String> g;
        // Pattern vertexer = Pattern.compile("^[0-9]{1,2}([,][0-9]{1,2})?$");
        Integer counter = 1;
        g = new SparseMultigraph<String, String>();
        //File inputFile = new File("/Users/bipashabanerjee/Documents/CS/sem2/DBMS/project/graphData/newOutput.txt");
        File inputFile = new File("/Users/bipashabanerjee/Documents/CS/sem2/DBMS/project/power/powerOutput.txt");
        Scanner s2 = new Scanner(inputFile);
        while (s2.hasNextLine()) {
            String str = s2.nextLine();
            String[] linesplit = str.split(",");
            String src = linesplit[0];
            String dst = linesplit[1];
            // Matcher extractor = vertexer.matcher(str);
            // String source = extractor.group(1);
            // String target = extractor.group(2);
            g.addVertex(src);
            g.addVertex(dst);
            g.addEdge(counter.toString(), src, dst);
            counter++;
        }


        Layout<Integer, String> layout = new ISOMLayout(g);
        layout.setSize(new Dimension(600,600));
        VisualizationViewer<Integer,String> vv = new
                VisualizationViewer<Integer,String>(layout);
        vv.setPreferredSize(new Dimension(600,600));
        JFrame frame = new JFrame("Graph View ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);

    }
}

