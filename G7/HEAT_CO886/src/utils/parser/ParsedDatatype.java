/**
 * ParsedDatatype.java
 *
 * @author sk22/jjsg2
 */

package utils.parser;

/**
 * Super class for typical parsed items (elements, types etc.)
 */
public class ParsedDatatype extends ParsedComponent {

    private String value;

    /**
     *  Creates a new instance of ParsedDatatype.
     *
     *  @param String name Name of the item
     *  @param String value Value of the item
     *  @param int location line number of the current item
     */
    public ParsedDatatype(String name, String value, int location )
    {
        super(name, location);
        this.value = value;
     
    }
    
    /**
     *  Creates a new instance of ParsedDatatype.
     *  In this alternative constructor a comment for current item can be specified
     *
     *  @param String name Name of the item
     *  @param String value Value of the item
     *  @param int location line number of the current item
     */
    public ParsedDatatype(String name, String value, int location, String comment )
    {
        super(name, location, comment);
        this.value = value;
    }

    /**
     *  Return value of the current item
     *
     *  @return String value
     */
    public String getValue()
    {
        return value;    
    }

    /**
     *  Set value of the current item
     *
     *  @param String value
     */
    public void setValue(String value)
    {
        value = value;
    }
 
}
