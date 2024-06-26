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
 * Read from the HubInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 * 
 * Step 4:
 * CollectedOutputFile name is passed in through the command line as args[3]
 * Output to CollectedOutputFile with the format:
 * 1. e Lines, listing the Name of the anomaly collected with the Spider who
 *    is at the same Dimension (if one exists, space separated) followed by 
 *    the Dimension number for each Dimension in the route (space separated)
 * 
 * @author Seth Kelley
 */

public class CollectAnomalies {
    
    HashMap<Integer, Integer> oldToNew; 
    HashMap<Integer, Integer> newToOld; 
    LinkedList<Integer>[] normalizedAdjList; 

    public CollectAnomalies (LinkedList<Integer>[] adjList)
    {
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


    public static void main(String[] args) {

        if ( args.length < 4 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.CollectAnomalies <dimension INput file> <spiderverse INput file> <hub INput file> <collected OUTput file>");
                return;
        }

        Clusters clusters = new Clusters(args[0]);
        LinkedList<Integer>[] list = makeAdjList(clusters);
        CollectAnomalies ca = new CollectAnomalies(list); 
        ArrayList<dimensionNode> dimensions = dimensions(args[1]); 
        

        // WRITE YOUR CODE HERE
        
    }
}
