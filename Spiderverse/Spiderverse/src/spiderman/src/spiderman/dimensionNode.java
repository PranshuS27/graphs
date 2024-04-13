package spiderman;
import java.util.*; 

public class dimensionNode {
    int currentDimension; 
    String name; 
    int correctDimension; 

    public dimensionNode(int current, String name, int correct)
    {
        this.currentDimension = current; 
        this.name = name; 
        this.correctDimension = correct;
    }
    
    public int getCurrent()
    {
        return currentDimension; 
    }

    public String getName()
    {
        return name; 
    }

    public int getCorrect()
    {
        return correctDimension; 
    }

}
