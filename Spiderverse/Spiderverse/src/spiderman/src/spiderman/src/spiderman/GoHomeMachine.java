package spiderman;
import java.util.*;
/**
 * Steps to implement this class main method:
 *
 * Step 1:
 * DimensionInputFile name is passed through the command line as args[0]
 * Read from the DimensionsInputFile with the format:
 * 1. The first line with three numbers:
 *      i.    a (int): number of dimensions in the graph
 *      ii.   b (int): the initial size of the cluster table prior to rehashing
 *      iii.  c (double): the capacity(threshold) used to rehash the cluster table
 * 2. a lines, each with:
 *      i.    The dimension number (int)
 *      ii.   The number of canon events for the dimension (int)
 *      iii.  The dimension weight (int)
 *
 * Step 2:
 * SpiderverseInputFile name is passed through the command line as args[1]
 * Read from the SpiderverseInputFile with the format:
 * 1. d (int): number of people in the file
 * 2. d lines, each with:
 *      i.    The dimension they are currently at (int)
 *      ii.   The name of the person (String)
 *      iii.  The dimensional signature of the person (int)
 *
 * Step 3:
 * HubInputFile name is passed through the command line as args[2]
 * Read from the SpotInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 *
 * Step 4:
 * AnomaliesInputFile name is passed through the command line as args[3]
 * Read from the AnomaliesInputFile with the format:
 * 1. e (int): number of anomalies in the file
 * 2. e lines, each with:
 *      i.   The Name of the anomaly which will go from the hub dimension to their home dimension (String)
 *      ii.  The time allotted to return the anomaly home before a canon event is missed (int)
 *
 * Step 5:
 * ReportOutputFile name is passed in through the command line as args[4]
 * Output to ReportOutputFile with the format:
 * 1. e Lines (one for each anomaly), listing on the same line:
 *      i.   The number of canon events at that anomalies home dimensionafter being returned
 *      ii.  Name of the anomaly being sent home
 *      iii. SUCCESS or FAILED in relation to whether that anomaly made it back in time
 *      iv.  The route the anomaly took to get home
 *
 * @author Seth Kelley
 */

public class GoHomeMachine {


    HashMap<Integer, Integer> oldToNew;
    HashMap<Integer, Integer> newToOld;
    LinkedList<Integer>[] normalizedAdjList;
    HashMap<Integer, Integer> weightMap;
    HashMap<Integer, Integer> canonMap;
    int dist[];
    Set<Integer> settled;
    PriorityQueue<Integer> pq;


    public GoHomeMachine (LinkedList<Integer>[] adjList)
    {
        canonMap = new HashMap<Integer, Integer>();
        weightMap = new HashMap<Integer, Integer>();
        oldToNew = new HashMap<Integer, Integer>();
        newToOld = new HashMap<Integer, Integer>();
        for (int i = 0; i<adjList.length; i++)
        {
            int curr = adjList[i].getFirst();
            oldToNew.put(curr, i);
            newToOld.put(i,curr);
        }
        normalizedAdjList = (LinkedList<Integer>[]) new LinkedList<?>[adjList.length];
        for(int i = 0; i<adjList.length; i++)
        {
            LinkedList<Integer> temp = adjList[i];
            normalizedAdjList[i] = new LinkedList<Integer>();
            for(int k = 0; k<temp.size(); k++)
            {
                normalizedAdjList[i].add(oldToNew.get(temp.get(k)));
            }
        }

    }

    public LinkedList<Integer> getNormalizedAdjListRow(int index)
    {
        return normalizedAdjList[index];
    }

    public int getNormalizedAdjListLength()
    {
        return normalizedAdjList.length;
    }

    public static LinkedList<Integer>[] makeAdjList(Clusters clusters)
    {
        ArrayList<Integer> discreteDimensions = new ArrayList<Integer>();
        int numOfDimensions = clusters.getDimensionNumber();
        LinkedList<Integer>[] adjList = (LinkedList<Integer>[]) new LinkedList<?>[clusters.getDimensionNumber()];
        int adjListCounter=0;
        for (int i = 0; i<clusters.getClustersSize(); i++)
        {
            LinkedList<Integer> traversal = clusters.getClusterRow(i);
            for (int k = 0; k<traversal.size(); k++)
            {
                if(adjListCounter<adjList.length)
                {
                    if(!discreteDimensions.contains(traversal.get(k)))
                    {
                        LinkedList<Integer> temp = new LinkedList<Integer>();
                        adjList[adjListCounter] = temp;
                        adjList[adjListCounter].addFirst(traversal.get(k));
                        discreteDimensions.add(traversal.get(k));
                        adjListCounter++;
                    }
                }
            }

        }

        for (int i = 0; i<adjList.length; i++)
        {
            for (int k = 0; k<clusters.getClustersSize(); k++)
            {
                if(clusters.getClusterRow(k).getFirst() == adjList[i].getFirst())
                {
                    for(int y = 1; y<clusters.getClusterRow(k).size(); y++)
                    {
                        adjList[i].add(clusters.getClusterRow(k).get(y));
                    }
                }
            }
        }


        for (int i = 0; i<adjList.length; i++)
        {
            LinkedList<Integer> temp = adjList[i];
            for (int k = 1; k<temp.size(); k++)
            {
                int target = temp.get(k);
                for (int j = 0; j<adjList.length; j++)
                {
                    if (target == adjList[j].getFirst() && !adjList[j].contains(temp.getFirst()))
                    {
                        adjList[j].add(temp.getFirst());
                    }
                }

            }
        }
        return adjList;
    }

    public static ArrayList<dimensionNode> dimensions(String inputFile)
    {
        ArrayList<dimensionNode> dimensions = new ArrayList<dimensionNode>();
        StdIn.setFile(inputFile);
        int numOfPeople = StdIn.readInt();
        StdIn.readLine();
        for(int i = 0; i<numOfPeople; i++)
        {
            int curr = StdIn.readInt();
            String name = StdIn.readString();
            int home = StdIn.readInt();
            dimensionNode node = new dimensionNode(curr, name, home);
            dimensions.add(i, node);
            StdIn.readLine();
        }
        return dimensions;
    }

    public HashMap<Integer, Integer> makeWeightMap(String inputFile)
    {
        StdIn.setFile(inputFile);
        int length = StdIn.readInt();
        StdIn.readLine();
        int counter = 0;
        while(counter < length)
        {
            Integer dimensionName = StdIn.readInt();
            Integer canonEvents = StdIn.readInt();
            Integer weights = StdIn.readInt();
            weightMap.put(dimensionName, weights);
            counter++;

        }
        return weightMap;

    }

    public HashMap<Integer, Integer> makeCanonMap(String inputFile)
    {
        StdIn.setFile(inputFile);
        int length = StdIn.readInt();
        StdIn.readLine();
        int counter = 0;
        while(counter < length)
        {
            Integer dimensionName = StdIn.readInt();
            Integer canonEvents = StdIn.readInt();
            canonMap.put(dimensionName, canonEvents);
            counter++;
            StdIn.readLine();

        }
        return canonMap;

    }


    public void shortestPath(int V, int source, int dest, int cost, String name) {
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;
        Queue<Integer> q = new LinkedList<>();
        q.offer(source);
        int[] parent = new int[V];
        Arrays.fill(parent, -1);
        parent[source] = source;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int i = 0; i < normalizedAdjList[u].size(); i++) {
                int v = normalizedAdjList[u].get(i);
                int wt = weightMap.get(newToOld.get(normalizedAdjList[u].get(i)));
                if (dist[v] > dist[u] + wt) {
                    dist[v] = dist[u] + wt;
                    q.offer(v);
                    parent[v] = u;
                }
            }
        }
        if(dist[dest]*2>cost)
        {
            StdOut.print(canonMap.get(newToOld.get(dest))-1 + " ");
            StdOut.print(name + " ");
            StdOut.print("FAILED" + " ");
        }
        else
        {
            StdOut.print (canonMap.get(newToOld.get(dest))+ " ");
            StdOut.print(name + " ");
            StdOut.print("SUCCESS" + " ");
        }
        Stack<Integer> st = new Stack<>();
        int final_node = dest;
        while (parent[dest] != source) {
            dest = parent[dest];
            st.push(dest);
        }
        StdOut.print(newToOld.get(source)+ " ");
        while (!st.isEmpty()) {
            StdOut.print(newToOld.get(st.pop()) + " ");
        }
        StdOut.print(newToOld.get(final_node) + " ");

        StdOut.println();
    }



    public static void main(String[] args) {

        if ( args.length < 5 ) {
            StdOut.println(
                    "Execute: java -cp bin spiderman.GoHomeMachine <dimension INput file> <spiderverse INput file> <hub INput file> <anomalies INput file> <report OUTput file>");
            return;
        }
        StdOut.setFile(args[4]);

        Clusters clusters = new Clusters(args[0]);
        LinkedList<Integer>[] list = makeAdjList(clusters);
        ArrayList<dimensionNode> dimensions = dimensions(args[1]);
        GoHomeMachine gh = new GoHomeMachine(list);
        dimensions(args[1]);
        gh.makeWeightMap(args[0]);
        gh.makeCanonMap(args[0]);
        StdIn.setFile(args[2]);
        int hub = StdIn.readInt();
        StdIn.setFile(args[3]);
        int numOfAnomalies = StdIn.readInt();
        StdIn.readLine();
        for(int i = 0; i<numOfAnomalies; i++)
        {
            String name = StdIn.readString();
            int cost = StdIn.readInt();
            int dimension = 0;
            for(int k = 0; k<dimensions.size(); k++)
            {
                if(dimensions.get(k).getName().equals(name))
                {
                    dimension = dimensions.get(k).getCorrect();
                }
            }
            gh.shortestPath(gh.getNormalizedAdjListLength(), gh.oldToNew.get(hub), gh.oldToNew.get(dimension), cost, name);
        }

        // WRITE YOUR CODE HERE

    }
}
