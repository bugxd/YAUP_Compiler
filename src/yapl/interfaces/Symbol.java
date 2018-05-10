package yapl.interfaces;

import yapl.lib.Type;

/** Interface to YAPL symbols contained in the symbol table.
 * A symbol's data type is represented by an abstract class <code>Type</code>,
 * which needs to be defined before using this interface.
 * 
 * @author Mario Taschwer
 * @version $Id: Symbol.java 288 2013-04-17 15:41:07Z mt $
 * @see Symboltable
 */
public interface Symbol
{
	/** Return the symbol's kind. */
	public SymbolKind getKind();
	
	/** Return the text version of the symbol's kind. 
	 * @return One of the literal strings: <code>program, procedure, variable, constant,
	 *         typename, field, parameter</code>.
	 */
	public String getKindString();

	/** Set the symbol's kind. */
	public void setKind(SymbolKind symbolKind);
	
	/** Return the symbol's name (identifier). */
	public String getName();
	
	/** Return the symbol's data type. */
	public Type getType();

	/** Set the symbol's data type. */
	public void setType(Type type);
	
	/** Return <code>true</code> iff this symbol represents a formal parameter 
	 * passed by reference.
	 * @see #setReference()
	 */
	public boolean isReference();
	
	/** Specify whether this symbol represents a formal parameter passed by reference. */
	public void setReference(boolean isReference);
	
    /** Return <code>true</code> iff this symbol is a readonly variable. */
    public boolean isReadonly();
    
    /** Specify whether this symbol is a readonly variable. */
    public void setReadonly(boolean isReadonly);
    
	/** Return <code>true</code> iff this symbol belongs to a global scope.
	 * That is, its value is stored in heap memory if applicable. 
	 * @see #getOffset()
	 */
	public boolean isGlobal();
	
	/** Specify whether this symbol belongs to a global scope. 
	 * @see #isGlobal() 
	 */
	public void setGlobal(boolean isGlobal);
	
	/** Return the symbol's address offset on the heap or on the stack.
	 * For global symbols, the offset is relative to the heap storage area
	 * for global variables.
	 * For local symbols, the offset is relative to the current stack frame.
	 * @see #isGlobal()
	 */
	public int getOffset();
	
	/** Set the symbol's address offset. 
	 * @see #getOffset()
	 */
	public void setOffset(int offset);
	
	/** Return the next symbol linked to this one.
	 * Parameter symbols are represented as a linked list attached
	 * to the procedure symbol.
	 * @return <code>null</code> if there is no symbol linked to this one.
	 */
	public Symbol getNextSymbol();
	
	/** Link another symbol to this one. 
	 * @see #getNextSymbol()
	 */
	public void setNextSymbol(Symbol symbol);
	
	/** Procedure symbol: return <code>true</code> iff the parser has encountered
	 * a Return statement within the procedure's body.
	 */
	public boolean getReturnSeen();
	
	/** Procedure symbol: indicate whether the parser has encountered a Return
	 * statement within the procedure's body.
	 */
	public void setReturnSeen(boolean seen);
	
	/** Return a text representation of this symbol for debugging purposes. */
	public String toString();
}
