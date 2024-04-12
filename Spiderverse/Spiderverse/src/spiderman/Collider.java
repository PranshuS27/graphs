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
 * ColliderOutputFile name is passed in through the command line as args[2]
 * Output to ColliderOutputFile with the format:
 * 1. e lines, each with a different dimension number, then listing
 *       all of the dimension numbers connected to that dimension (space separated)
 * 
 * @author Seth Kelley
 */

public class Collider {


    public LinkedList<Integer>[] adjList; 

    public Collider (Clusters clusters)
    {
        ArrayList<Integer> discreteDimensions = new ArrayList<Integer>();
        int numOfDimensions = clusters.getDimensionNumber();
        
        adjList = (LinkedList<Integer>[]) new LinkedList<?>[numOfDimensions];
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

    }

    public int getColliderSize()
    {
        return adjList.length;
    }

    public LinkedList<Integer> getColliderRow(int index)
    {
        return adjList[index];
    }

    public static void main(String[] args) {

        if ( args.length < 3 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.Collider <dimension INput file> <spiderverse INput file> <collider OUTput file>");
                return;
        }

        Clusters clusters = new Clusters(args[0]);
        Collider adjList = new Collider(clusters);


        for(int i  = 0; i<adjList.getColliderSize(); i++)
        {
            StdOut.println(adjList.getColliderRow(i));
        }
        

    }


        
}



    

 

    
