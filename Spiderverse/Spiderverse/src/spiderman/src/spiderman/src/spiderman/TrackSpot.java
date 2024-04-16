package spiderman;
import java.util.*;

import javax.sound.midi.Track;
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
 * SpotInputFile name is passed through the command line as args[2]
 * Read from the SpotInputFile with the format:
 * Two integers (line seperated)
 *      i.    Line one: The starting dimension of Spot (int)
 *      ii.   Line two: The dimension Spot wants to go to (int)
 *
 * Step 4:
 * TrackSpotOutputFile name is passed in through the command line as args[3]
 * Output to TrackSpotOutputFile with the format:
 * 1. One line, listing the dimenstional number of each dimension Spot has visited (space separated)
 *
 * @author Seth Kelley
 */

public class TrackSpot {


    HashMap<Integer, Integer> oldToNew;
    HashMap<Integer, Integer> newToOld;
    LinkedList<Integer>[] normalizedAdjList;

    public TrackSpot (LinkedList<Integer>[] adjList)
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

    void printPath(Vector<Integer> stack, String fileName, int last)
    {
        StdOut.setFile(fileName);

        for(int i = 0; i < stack.size()-1; i++)
        {
            if(stack.get(stack.size()-1) != last)
            {
                stack.remove(stack.size()-1);
            }
            StdOut.print(newToOld.get(stack.get(i)) + " ");
        }
        StdOut.print(newToOld.get(stack.get(stack.size()-1)));
    }

    void DFS(boolean vis[], int current, int lastVal, Vector<Integer> stack, String fileName)
    {
        if(stack.contains(current) == false)
        {
            stack.add(current);
        }

        if (current == lastVal)
        {
            printPath(stack, fileName, lastVal);
            return;
        }
        if(stack.contains(lastVal))
        {
            return;
        }
        vis[current] = true;

        if (normalizedAdjList[current].size() > 0)
        {
            for(int j = 0; j < normalizedAdjList[current].size(); j++)
            {

                if (vis[normalizedAdjList[current].get(j)] == false)
                {
                    DFS(vis, normalizedAdjList[current].get(j), lastVal, stack, fileName);
                }
            }
        }

    }


    void DFSCall(int x, int y, int n,
                 Vector<Integer> stack, String fileName)
    {

        boolean vis[] = new boolean[n + 1];
        Arrays.fill(vis, false);
        DFS(vis, x, y, stack, fileName);
    }

    public static void main(String[] args) {

        if ( args.length < 4 ) {
            StdOut.println(
                    "Execute: java -cp bin spiderman.TrackSpot <dimension INput file> <spiderverse INput file> <spot INput file> <trackspot OUTput file>");
            return;
        }

        Clusters clusters = new Clusters(args[0]);
        LinkedList<Integer>[] list = makeAdjList(clusters);
        TrackSpot tspot = new TrackSpot(list);
        StdOut.setFile(args[3]);
        Vector<Integer> stack = new Vector<Integer>();
        int numOfNodes = clusters.getDimensionNumber();
        StdIn.setFile(args[2]);
        int startingIndex = tspot.oldToNew.get(StdIn.readInt());
        StdIn.readLine();
        int finishingIndex = tspot.oldToNew.get(StdIn.readInt());
        String fileName = args[3];
        tspot.DFSCall(startingIndex, finishingIndex, numOfNodes, stack, fileName);


    }



}

