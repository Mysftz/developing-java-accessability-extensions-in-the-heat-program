/*
 * ParsedFunction.java
 *
 * Created on 13 December 2005, 12:46
 */

package utils.parser;
import java.util.*;
/**
 *
 * @author sk22/jjsg2
 */
public class ParsedFunction extends ParsedComponent
{

    private String[] value;
    private ArrayList tests = new ArrayList();
    
    /** Creates a new instance of ParsedFunction */
    public ParsedFunction(String name, String[] value, int location)
    {
        super(name, location);
        this.value = value;
    }
    
    public ParsedFunction(String name, String[] value, int location, String comment, ArrayList tests)
    {
        super(name, location, comment);
        this.value = value;
        this.tests = tests;
    }
    
    public String[] getValue()
    {
        return this.value;
    }
    public String getValueString()
    {
        String value = "";
        for (int i=0; i<this.value.length; i++)
        {
            value += this.value[i]+"!";
        }
        
        return value;
    }

    public ArrayList getTests()
    {
        return this.tests;
    }
    
    public void setValue(String[] input)
    {
        this.value = value;
    }

    public boolean hasTests()
    {
        return (tests.size() > 0);
    }
    
}
