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

    public static void main(String[] args) {

        if ( args.length < 3 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.Collider <dimension INput file> <spiderverse INput file> <collider OUTput file>");
                return;
        }

        StdIn.setFile(args[0]);
        int numOfDimensions = StdIn.readInt(); 
        int hashtableSize = StdIn.readInt(); 
        int rehashThreshold = StdIn.readInt(); 
        StdIn.readLine();
        LinkedList<Integer>[] clusters = (LinkedList<Integer>[]) new LinkedList<?>[hashtableSize];
        ArrayList<Integer> discreteDimensions = new ArrayList<Integer>();
        int dimensionCounter = 0; 
        while(dimensionCounter < numOfDimensions) 
        {
            int dimension = StdIn.readInt();
            int key = dimension % clusters.length;
            if (clusters[key] == null)
            {
                LinkedList<Integer> newList = new LinkedList<Integer>();
                newList.addFirst(dimension);
                clusters[key] = newList; 
            }
            else
            {
                clusters[key].addFirst(dimension); 
            }
            dimensionCounter++;
            if (dimensionCounter/clusters.length>= rehashThreshold)
            {
                clusters = rehash(clusters, dimensionCounter);
            }
            StdIn.readLine();
        }

        for (int i = 0; i<clusters.length; i++)
        {
            if (i == 0)
            {
                clusters[i].addLast(clusters[clusters.length-1].getFirst());
                clusters[i].addLast(clusters[clusters.length-2].getFirst());

            }
            else if (i == 1)
            {
                clusters[i].addLast(clusters[0].getFirst());
                clusters[i].addLast(clusters[clusters.length-1].getFirst());
            }
            else
            {
                clusters[i].addLast(clusters[i-1].getFirst());
                clusters[i].addLast(clusters[i-2].getFirst());
            }
        }

        StdOut.setFile(args[2]);
        /*for(int i = 0; i<clusters.length; i++)
        {
            LinkedList<Integer> currentRow = clusters[i];
            for (int k = 0; k<currentRow.size(); k++)
            {
                StdOut.print(currentRow.get(k) + " ");
            }
            StdOut.println();
        }*/
        

        LinkedList<Integer>[] adjList = (LinkedList<Integer>[]) new LinkedList<?>[numOfDimensions];
        int adjListCounter=0;
        for (int i = 0; i<clusters.length; i++)
        {
            LinkedList<Integer> traversal = clusters[i];
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
            for (int k = 0; k<clusters.length; k++)
            {
                if(clusters[k].getFirst() == adjList[i].getFirst())
                {
                    for(int y = 1; y<clusters[k].size(); y++)
                    {
                        adjList[i].add(clusters[k].get(y)); 
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







        for(int i  = 0; i<adjList.length; i++)
        {
            StdOut.println(adjList[i]);
        }
        

    }

   
    private static LinkedList<Integer>[] rehash (LinkedList<Integer>[] curr, int numOfDimensions)
    {
        int size = curr.length; 
        ArrayList<Integer> transfer = new ArrayList<Integer>();
        LinkedList<Integer>[] rehashedTable = (LinkedList<Integer>[]) new LinkedList<?>[size*2]; 
        for (int i = 0; i<size; i++)
        {
            LinkedList<Integer> temp = curr[i];
            for (int k = 0; k<temp.size(); k++)
            {
                transfer.add(temp.get(k));
            }
        }
        for (int i = 0; i < transfer.size(); i++)
        {
            int key = transfer.get(i) % rehashedTable.length;
            if (rehashedTable[key] == null)
            {
                LinkedList<Integer> newList = new LinkedList<Integer>();
                newList.addFirst(transfer.get(i));
                rehashedTable[key] = newList; 
                
            }
            else
            {
                rehashedTable[key].addFirst(transfer.get(i)); 
    
            }
        }
        return rehashedTable; 

    } 
        
}



    

 

    
