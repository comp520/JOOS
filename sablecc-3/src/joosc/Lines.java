package joosc;

import joosc.analysis.*;
import joosc.node.*;
import java.util.*;

public class Lines extends ReversedDepthFirstAdapter
{
    public static void setLines(List theProgram)
    {
	ClassFile classfile;
	Iterator iter = theProgram.iterator();
	Node ast;
	Lines lineSetter = new Lines();

	while (iter.hasNext()) {
	    classfile = (ClassFile)iter.next();
	    ast = classfile.getAst();
	    ast.apply(lineSetter);
	}
    }

    static public int getLine(Node node)
    {
	return ((Integer) lines.get(node)).intValue();
    }

    static public int getPos(Node node)
    {
	return ((Integer) positions.get(node)).intValue();
    }


    static private int last_line = -1;
    static private int last_pos = -1;

    static private final Map lines = new HashMap();
    static private final Map positions = new HashMap();

    // All non-token nodes
    public void defaultOut(Node node)
    {
	lines.put(node, new Integer(last_line));
	positions.put(node, new Integer(last_pos));
    }

    // All tokens
    public void defaultCase(Node node)
    {
	Token token = (Token) node;
	last_line = token.getLine();
	last_pos = token.getPos();
	lines.put(node, new Integer(last_line));
	positions.put(node, new Integer(last_pos));
    }

}
