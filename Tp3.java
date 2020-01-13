import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.TreeMap;

public class Tp3 {
    private String inputFilename; //First argument, args[0]
    private String outputFilename; //Second argument, args[1]
    private TreeMap<String,ArrayList<Edge>> graph; // Map, the key is the name of the vertex and the corresponding list of edges
    private ArrayList<String> nodeList;

    //for Prim algorithm
    private ArrayList<Edge> addedEdges;   //List of Edges added to the final MST
    private ArrayList<String> addedNodes; //List of Nodes added to the final MST

    public Tp3 (String inputFilename, String outputFilename) throws IOException, ParseException {
        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;

        graph = new TreeMap< >();
        nodeList = new ArrayList< >();
        parse();

        addedEdges = new ArrayList< >();
        addedNodes = new ArrayList< >();
        primJarnik();

        Collections.sort(addedNodes);
        Collections.sort(addedEdges);
        printFile();
    }

    public class Edge implements Comparable<Edge> {
        String etiquette;
        String start;
        String end;
        int weight;

        public Edge(String etiquette, String start, String end, int weight){
            this.etiquette = etiquette;
            this.start = start;
            this.end = end;
            this.weight = weight;
        }
        //Bunch of getter set
        public int getWeight() {
            return this.weight;
        }

        public String getStart() {
            return this.start;
        }

        public String getEnd() {
            return this.end;
        }

        public int compareTo(Edge edge) {
            int compareStart;
            int compareEnd;
            compareStart = this.start.compareTo(edge.getStart());
            compareEnd = this.end.compareTo(edge.getEnd());

            if (compareStart == 0) {
                return compareEnd;
            } else {
                return compareStart;
            }
        }

        @Override
        public String toString() {
            return this.etiquette+"\t"+this.start+"\t"+this.end+"\t"+this.weight;
        }
    }

    //Makes ArrayList of valid Edges to add
    public ArrayList<Edge> validEdges() {
        ArrayList<Edge> result = new ArrayList< >();
        ArrayList<Edge> start = new ArrayList< >();

        //Makes ArrayList of all edges connected to a node in addedNodes
        for (String nodeName : addedNodes) {
            start.addAll(graph.get(nodeName));
        }

        //Adds edges with end nodes that not already connected
        if (!start.isEmpty()) {
            for (Edge e : start) {
                //Return true if the node is already connected
                if (!addedNodes.contains(e.getEnd())) {
                    result.add(e);
                }
            }
        }

        return result;
    }

    //Adds edge with minimal span/distance to MST
    public void bestEdge(ArrayList<Edge> edges) {
        Edge minEdge = edges.get(0);

        for (Edge e : edges) {
            if (e.getWeight() < minEdge.getWeight()) {
                minEdge = e;
            }
        }

        addedNodes.add(minEdge.getEnd());   //adds the end node of that edge
        addedEdges.add(minEdge);            //adds the minimal edge
    }

    //Prim algorithm, result is in addedEdges
    public void primJarnik() {
        ArrayList<Edge> goodEdges;
        addedNodes.add(nodeList.get(0));

        while (nodeList.size() > addedNodes.size()) {
            goodEdges = validEdges();
            bestEdge(goodEdges);
        }
    }

    // Parse the input file
    private void parse(){
        Pattern stop = Pattern.compile("---");
        ArrayList<Edge> tempEdgeList;

        try {
            //Initiate Scanner
            FileReader inputFile = new FileReader(inputFilename);
            Scanner in = new Scanner(inputFile);

            //Initiate first line
            String line = in.nextLine();
            Scanner scanLine = new Scanner(line);
            //Loop to store name of all nodes
            while (!scanLine.hasNext(stop)){
                String newNode = scanLine.next();
                //If we get 2 node of the same name we skip
                if (!nodeList.contains(newNode)){
                    //Add node name
                    nodeList.add(newNode);
                }
                scanLine.close();
                //Initiate next line
                line = in.nextLine();
                scanLine = new Scanner(line);
            }

            //Initiate next line
            line = in.nextLine();
            scanLine = new Scanner(line);

            //Loop to store node in graph
            while (!scanLine.hasNext(stop)){

                //Get info
                String etiquette = scanLine.next();
                scanLine.next();                        //Skip the ":"
                String start = scanLine.next();
                String end = scanLine.next();
                int weight;
                //Check if weight and ; is not separeted
                if(!scanLine.hasNextInt()) {
                    String weightS = scanLine.next();
                    weightS = weightS.substring(0, weightS.length()-1);
                    weight = Integer.parseInt(weightS);
                }
                else {
                    weight = scanLine.nextInt();
                    scanLine.next();                        //Skip the ";"
                }

                //Create edge
                Edge newEdge = new Edge(etiquette,start,end,weight);
                Edge newEdge2 = new Edge(etiquette, end, start, weight);

                //Add edges to List of edge for both start and end node.
                tempEdgeList = new ArrayList< >();
                if (graph.containsKey(start)) {
                    tempEdgeList = graph.get(start);
                    graph.remove(start);
                }
                tempEdgeList.add(newEdge);
                graph.put(start, tempEdgeList);

                tempEdgeList = new ArrayList< >();
                if (graph.containsKey(end)) {
                    tempEdgeList = graph.get(end);
                    graph.remove(end);
                }
                tempEdgeList.add(newEdge2);
                graph.put(end, tempEdgeList);

                scanLine.close();
                //Initiate next line
                line = in.nextLine();
                scanLine = new Scanner(line);
            }

            //Close file reading
            in.close();
            inputFile.close();

        } catch (IOException e){
            System.out.println("Input error");
            System.exit(0);
        }
    }


    public void printFile() throws IOException{
        Writer writer = null;
        int cost = 0;

        try {
            FileOutputStream outputStream = new FileOutputStream(outputFilename);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            writer = new BufferedWriter(outputWriter);

            for (String nodeName: addedNodes) {
                writer.write(nodeName + System.lineSeparator());
            }
            for (Edge theEdge: addedEdges) {
                writer.write(theEdge.toString() + System.lineSeparator());
                cost += theEdge.getWeight();
            }

            writer.write("---" + System.lineSeparator());
            writer.write(cost + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Output error");
            System.exit(0);
        }
        writer.close();
    }

    public static void main(String[] args) throws Exception {

        if (args.length==2) {
            Tp3 tp3 = new Tp3(args[0], args[1]);
        }

        //Tp3 tp0 = new Tp3("carte0.txt", "lol0.txt");
        //Tp3 tp3 = new Tp3("carte3.txt", "lol3.txt");

    }
}