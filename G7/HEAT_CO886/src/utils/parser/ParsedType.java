/*
 * ParsedType.java
 *
 * Created on 07 December 2005, 16:04
 */

package utils.parser;

/**
 *
 * @author dn9/jjsg2
 */
public class ParsedType extends ParsedComponent{
    
    private String value;
            
    
    /** Creates a new instance of ElementTypes */
    public ParsedType(String name,String value, int location ) {
        
        super(name, location);
        this.value = value;
     
    }
     public ParsedType(String name,String value, int location, String comment ) {
        
        super(name, location, comment);
        this.value = value;
     }
        
    public String getValue()
    {
        return this.value;    
   }

    public void setValue(String value)
    {
        this.value= value;
    }
}
