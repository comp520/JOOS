public class Cons {
  // Fields 
  protected Object first;
  protected Cons rest;
 
  // Constructor 
  public Cons(Object f, Cons r)
    { super();
      first = f;
      rest = r; 
    }

  // Methods 
  public void setFirst(Object newfirst)
    { first = newfirst; }

  public Object getFirst() 
  { return first; }
 
  public Cons getRest()
  { return rest; }

  public boolean member(Object item) 
    { if (first.equals(item))
        return(true);
      else if (rest == null)
        return(false);
      else
        return(rest.member(item));
    }

  public String toString()
    { if (rest == null) 
        return(first.toString());
      else
        return(first + " " + rest);
    }
}
