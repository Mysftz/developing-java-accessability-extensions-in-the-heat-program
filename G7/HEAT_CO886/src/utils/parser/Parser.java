/*
 * Parser.java
 *
 * @author sk22/jjsg2
 */

package utils.parser;

import java.util.ArrayList;
import java.util.regex.*;
import java.util.Arrays;

/**
 *  Parses a document for Haskell elements, types and datatypes.
 */
public class Parser
{
    private ArrayList<String> inText;
    private ArrayList<ParsedFunction> elements;
    private ArrayList<ParsedType> types;
    private ArrayList<ParsedDatatype> dataTypes;
    private ArrayList<ParsedTest> tests;
    
    /**
     *  Creates a new instance of Parser
     *
     *  @param ArrayList inText List of strings to parse
     */
    public Parser(ArrayList<String> inText)
    {
        this.inText = inText;
        elements = new ArrayList<ParsedFunction>();
        types = new ArrayList<ParsedType>();
        dataTypes = new ArrayList<ParsedDatatype>();
        tests = new ArrayList<ParsedTest>();
    }
    
    /**
     *  Creates a new instance of Parser
     *
     *  @param String inText text to parse
     */
    public Parser(String inText)
    {
        this.inText = new ArrayList<String>();
        elements = new ArrayList<ParsedFunction>();
        types = new ArrayList<ParsedType>();
        dataTypes = new ArrayList<ParsedDatatype>();
        tests = new ArrayList<ParsedTest>();
        setText(inText);
    }
    
    /**
     *  Parsing the list for elements, types and datatypes
     */
    public void parse()
    {
        //this.matches = new ArrayList();
        this.tests = new ArrayList<ParsedTest>();
        this.elements = new ArrayList<ParsedFunction>();
        this.types = new ArrayList<ParsedType>();
        this.dataTypes = new ArrayList<ParsedDatatype>();
        
        
        this.findTests();
        this.findTypes();
        this.findDataTypes();
        this.findElements();
    }
    
    public boolean hasUncheckedTests() {
    	boolean hasUnchecked = false;
    	for (ParsedTest test : tests) {
    		hasUnchecked = hasUnchecked || (test.getState() == "testUnknown");
    	}
    	return hasUnchecked;
    }
    
    public ArrayList<ParsedTest> getTests(){
    	return tests;
    }
    
    public ArrayList<ParsedFunction> getElements()
    {
        return this.elements;
    }
    
    public ArrayList<ParsedType> getTypes()
    {
        return this.types;
    }
    
    public ArrayList<ParsedDatatype> getDataTypes()
    {
        return this.dataTypes;
    }
    
    private void findElements()
    {
        String currentString = "";
        //Pattern pattern = Pattern.compile("^([\\w_]+)\\s?::\\s?(.*)\\s?$");	// no comments
        Pattern pattern = Pattern.compile("^([\\w_,'\\s]+)\\s?::\\s?(.*?)\\s?(?:\\-\\-(.*))?$");	// with comments
        Matcher matcher;
        
        for (int i=0; i<this.inText.size(); i++)
        {
            currentString = (String) this.inText.get(i);
            matcher = pattern.matcher(currentString);
            
            if (matcher.find()) // found somethin with comments. now lets break it apart
            {
				String comment = "";
				//	check comments
				if (matcher.group(3) != null)
					comment = matcher.group(3);
				
				//	replace '->' which is not applicable
                String value = matcher.group(2);
                value = replaceInsideArrows(value);
                //	break the string into the array of values
                String[] values = value.split("\\s?->\\s?");
                //	restore '->' in those values.
				values = restoreInsideArrows(values);
                  
				// in case a few elements defined at the same time
				String[] names = matcher.group(1).split("\\s?,\\s?");
				
				// if multiple elements defined on the same line
				for (int n=0; n<names.length; n++)
				{
                    ArrayList matches = findMatches(names[n].trim());
                                if (!names[n].trim().startsWith("prop_")) {
			            this.elements.add(new ParsedFunction(names[n].trim(), values, i, comment, matches));
                                }
                                         
                }
            }
        }
    }

    private ArrayList<ParsedTest> findMatches(String element)
    {
        ArrayList<ParsedTest> matches = new ArrayList<ParsedTest>();

        for(int i = 0; i < tests.size(); i++)
        {
            ParsedTest test = tests.get(i);
            if (test.getFunctionName().contains(element))
              matches.add(test);
        }

        return matches;
    }
  
    private String replaceInsideArrows(String currentString)
	{
		char[] line = currentString.toCharArray();
		String newLine = "";

		if(line.length > 0)
		{
			int count1 = 0;   
			int count = 0;
			
			for(int i=0; i<line.length; i++)
			{
				if(line[i] == '(')
					count++;
				else if(line[i] == '[')
                    count1++;
				else if((line[i] == '-') && (count > 0 || count1 > 0))
					line[i] = '$';
				else if(line[i] == ')')
                     count--;
				else if(line[i] == ']')
                    count1--;

				newLine = String.copyValueOf(line);
			}
		}
		
		return newLine;
	}
	
	private String[] restoreInsideArrows(String[] values)
	{
        for (int i=0; i<values.length; i++)
        {
            values[i] = values[i].replace('$','-');
        }
        return values;
	}
               
                   
    private void findTypes()
    {
        String currentString = "";
        
        Pattern pattern = Pattern.compile("^type\\s+([\\w_\\s]+)\\s*=\\s*([^\\-]*)(.*)$");
        Matcher matcher;

        for (int i=0; i<this.inText.size(); i++)
        {
            currentString = (String) this.inText.get(i);
            matcher = pattern.matcher(currentString);
            
            if (matcher.find())
                this.types.add(new ParsedType(matcher.group(1), matcher.group(2), i));
        }
    }
    
    private void findDataTypes()
    {
        String currentString = "";
        
        Pattern pattern = Pattern.compile("^data\\s+([\\w_\\s]+)\\s?=\\s*(.*)$");
        Matcher matcher;

        for (int i=0; i<this.inText.size(); i++)
        {
            currentString = (String) this.inText.get(i);
            matcher = pattern.matcher(currentString);
            
            if (matcher.find())
                this.dataTypes.add(new ParsedDatatype(matcher.group(1), matcher.group(2), i));
        }
    }
    
    /**
     * Finds all the tests from the current file
     */
    private void findTests()
    {
        String currentString = "";
        /*The pattern by which the tests are found*/
        Pattern pattern = Pattern.compile("^prop_([\\w_]+)\\s?(.*)=(.*)$");
                  // old: "^prop_([\\w_]+)\\s*=\\s*(.*)\\s*==\\s*(.*)"
        Matcher matcher;
        for (int i=0; i<this.inText.size(); i++)
        {
            currentString = (String) this.inText.get(i);
            matcher = pattern.matcher(currentString);
            if (matcher.find())
            {
                ParsedTest test = new ParsedTest(matcher.group(1),"testUnknown",i);
                this.tests.add(test);
            }
        }
    }
    
    public void clearComponents()
    {
        elements.clear();
        types.clear();
        dataTypes.clear();
        tests.clear();
    }
    
    private void setText(String inText)
    {
        String[] lines = inText.split("\n");
        ArrayList<String> text = new ArrayList<String>(Arrays.asList(lines));
        this.inText = text;
    }
    
    public void reloadComponents(String inText)
    {
        clearComponents();
        setText(inText);
        parse();
    }
    
   
    
}
