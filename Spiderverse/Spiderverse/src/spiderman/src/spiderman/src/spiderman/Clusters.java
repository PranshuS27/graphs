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
 *
 * Step 2:
 * ClusterOutputFile name is passed in through the command line as args[1]
 * Output to ClusterOutputFile with the format:
 * 1. n lines, listing all of the dimension numbers connected to 
 *    that dimension in order (space separated)
 *    n is the size of the cluster table.
 *
 * @author Seth Kelley
 */

public class Clusters {

    public LinkedList<Integer>[] clusters;
    public int numOfDimensions;

    public Clusters (String filename)
    {
        StdIn.setFile(filename);
        numOfDimensions = StdIn.readInt();
        int hashtableSize = StdIn.readInt();
        double rehashThreshold = StdIn.readDouble();
        StdIn.readLine();
        clusters = (LinkedList<Integer>[]) new LinkedList<?>[hashtableSize];
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

        numOfDimensions = dimensionCounter;

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


    }

    public int getDimensionNumber()
    {
        return numOfDimensions;
    }

    public int getClustersSize()
    {
        return clusters.length;
    }

    public LinkedList<Integer> getClusterRow(int index)
    {
        return clusters[index];
    }



    public static void main(String[] args) {

        if ( args.length < 2 ) {
            StdOut.println(
                    "Execute: java -cp bin spiderman.Clusters <dimension INput file> <collider OUTput file>");
            return;
        }

        Clusters newClusters = new Clusters(args[0]);
        StdOut.setFile(args[1]);

        for(int i = 0; i<newClusters.getClustersSize(); i++)
        {
            LinkedList<Integer> currentRow = newClusters.getClusterRow(i);
            for (int k = 0; k<currentRow.size(); k++)
            {
                StdOut.print(currentRow.get(k) + " ");
            }
            StdOut.println();
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
