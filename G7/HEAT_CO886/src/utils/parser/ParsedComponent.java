/*
 * ParsedComponent.java
 *
 * Created on 15 February 2006, 15:32
 */

package utils.parser;

/**
 *
 * @author jjsg2
 */
public class ParsedComponent {
    private String name;
    private String comment;
    private int location;
    
    /** Creates a new instance of ParsedComponent */
    public ParsedComponent(String name, int location )
    {
        this.name = name;
        this.location = location;
     
    }
    
    public ParsedComponent(String name, int location, String comment )
    {
        this.name = name;
        this.location = location;
        this.comment = comment;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public String getComment()
    {
        return this.comment;
    }
    
    public int getLocation()
    {
        return this.location;
    }
    
    public void setName ( String name)
    {
       this.name = name;
    }
    
    public void setComment(String comment)
    {
        this.comment = comment;
    }
    
    public void setLocation(int location)
    {
        this.location = location;
    }
    
    public String toString()
    {
        return name;
    }
}
