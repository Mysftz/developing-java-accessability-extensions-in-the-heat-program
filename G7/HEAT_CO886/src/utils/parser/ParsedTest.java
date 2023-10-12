/**
  * ParsedTest.java
  *
  * Created on 03 February 2006, 14:18
  * @author jjsg2
  */

package utils.parser;

public class ParsedTest extends ParsedComponent {
	
    private String name;
    private String state;
            
    /** Creates a new instance of ElementTypes */
    public ParsedTest(String name, String state, int location) {
        super("prop_"+name,location);
        this.name = name;
        this.state=state;
     
    }
        
    public String getFunctionName()
    {
        return this.name;
    }
    
    public void setName ( String name)
    {
        this.name = name;
    }
    
    
    /*Gets the state of the test(prop), i.e., if it failed, passed or unknown*/
    public String getState()
    {
      return this.state;
    }
    
    public void setState(String state)
    {
      this.state=state;
    }
      
    public String toString()
    {
        return getFunctionName();
    }

}
