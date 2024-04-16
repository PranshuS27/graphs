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
 *      ii.   b (int): the initial size of the cluster table size prior to rehashing
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
 * MeetupInputFile name is passed through the command line as args[4]
 * Read from the MeetupInputFile with the format:
 * 1. Same line:
 *      f (int): number of Spiders in the file
 *      g (int): number of people being gathered
 *      h (int): time given for them to arrive
 *      j (int): the Dimension they will gather at
 * 2. f lines, each with:
 *      i. The name of the Spider (String)
 * 
 * Step 6:
 * RescueOutputFile name is passed in through the command line as args[5]
 * Output to RescueOutputFile with the format:
 * 1. One Line, TRUE or FALSE
 * 
 * @author Seth Kelley
 */

public class SaveMiles {
    
    HashMap<Integer, Integer> oldToNew; 
    HashMap<Integer, Integer> newToOld; 
    LinkedList<Integer>[] normalizedAdjList; 
    HashMap<Integer, Integer> weightMap; 
    HashMap<Integer, Integer> canonMap;
    int dist[];
    Set<Integer> settled;
    PriorityQueue<Integer> pq;


    public SaveMiles (LinkedList<Integer>[] adjList)
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


    /*public LinkedList<weightedNode>[] makeWeightedGraph()
    {
        weightedGraph =  (LinkedList<weightedNode>[]) new LinkedList<?>[normalizedAdjList.length]; 
        for(int i = 0; i<normalizedAdjList.length; i++)
        {
            LinkedList<Integer> temp = normalizedAdjList[i];
            LinkedList<weightedNode> temp2 = new LinkedList<>();
            for(int k = 0; k<temp.size(); k++)
            {
                int dimensionNumber = newToOld.get(temp.get(k)); 
                System.out.println(newToOld.get(0));
                System.out.println(temp.get(k));
                System.out.println(weightMap.get(0));
                int weight = weightMap.get(newToOld.get(temp.get(k)));
                weightedNode node = new weightedNode(dimensionNumber, weight);
                temp2.add(node);
            } 
            weightedGraph[i] = temp2; 

        }
        return weightedGraph; 
    }*/


    public void dijkstra(int src, int V)
    {

        dist = new int[V];
        settled = new HashSet<Integer>();
        pq = new PriorityQueue<Integer>(V);
        for (int i = 0; i < V; i++)
            dist[i] = Integer.MAX_VALUE;
 
        // Add source node to the priority queue
        pq.add(src);
 
        // Distance to the source is 0
        dist[src] = 0;
 
        while (settled.size() != V) {
 
            // Terminating condition check when
            // the priority queue is empty, return
            if (pq.isEmpty())
                return;
 
            // Removing the minimum distance node
            // from the priority queue
            int u = pq.remove();
 
            // Adding the node whose distance is
            // finalized
            if (settled.contains(u))
                // Continue keyword skips execution for
                // following check
                continue;

 
            // We don't have to call e_Neighbors(u)
            // if u is already present in the settled set.
            settled.add(u);
 
            e_Neighbours(u);
        }
    }
 
    // Method 2
    // To process all the neighbours
    // of the passed node
    private void e_Neighbours(int u)
    {
 
        int edgeDistance = -1;
        int newDistance = -1;
 
        // All the neighbors of v
        for (int i = 0; i < normalizedAdjList[u].size(); i++) {
            int v = normalizedAdjList[u].get(i);
 
            // If current node hasn't already been processed
            if (!settled.contains(v)) {
                edgeDistance = weightMap.get(newToOld.get(v));

                newDistance = dist[u] + edgeDistance;
 
                // If new distance is cheaper in cost
                if (newDistance < dist[v])
                    dist[v] = newDistance;
 
                // Add the current node to the queue
                pq.add(v);
            }
        }
    }
    public static void main(String[] args) {

        if ( args.length < 6 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.SaveMiles <dimension INput file> <spiderverse INput file> <hub INput file> <anomalies INput file> <meetup INput file> <rescue OUTput file>");
                return;
        }

        Clusters clusters = new Clusters(args[0]);
        LinkedList<Integer>[] list = makeAdjList(clusters);
        SaveMiles sm = new SaveMiles(list); 
        dimensions(args[1]); 
        sm.makeWeightMap(args[0]); 
        sm.makeCanonMap(args[0]);
        sm.dijkstra(sm.oldToNew.get(928), sm.normalizedAdjList.length);
        StdIn.setFile(args[4]);
        int numPeople = StdIn.readInt();
        int numNeeded = StdIn.readInt();
        int time = StdIn.readInt();
        int dimMeet = StdIn.readInt();
        StdIn.readLine();
        for (int i = 0; i < numPeople; i++)
        {
            
        }
        

        // WRITE YOUR CODE HERE
        
    }
}
