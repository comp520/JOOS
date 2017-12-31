# JOOS

JOOS (Java Object-Oriented Subset) is a strict subset of Java designed by Laurie Hendren of McGill University and Michael Schwartzbach from Aarhus University in Denmark.

Design goals:
* Extract the object-oriented essence of Java
* Make the language small enough for course work, yet large enough to be interesting
* Provide a mechanism to link to existing Java code
* Ensure that every JOOS program is a valid Java program, such that JOOS is a *strict* subset of Java

Programs in JOOS consist of a collection of classes that have:
* Protected fields
* Constructors
* Public methods

Ordinary classes are used to develop JOOS code, while external classes are interfaces to Java libraries.

Example JOOS program:
```java
public class Cons {
    protected Object first;
    protected Cons rest;
  
    public Cons(Object f, Cons r) {
        super();
        first = f;
        rest = r;
    }
  
    public void setFirst(Object newFirst) {
        first = newFirst;
    }
  
    public Object getFirst() {
        return first;
    }
  
    public Cons getRest() {
        return rest;
    }
  
    public boolean member(Object item) {
        if (first.equals(item))
            return true;
        else if (rest == null)
            return false;
        else
            return rest.member(item);
    }
  
    public String toString() {
        if (rest == null)
            return first.toString();
        else
            return first + " " + rest;
    }
}
```

## Directory Layout

* **joos/lib**: JOOS interface to Java class library
* **joos/extern**: JOOS extern declarations
* **programs**: example JOOS programs
* **flex+bison**: A- compiler implemented in C using flex and bison
* **sablecc-2**: A- compiler implemented in Java using SableCC 2
* **sablecc-3**: A- compiler implemented in Java using SableCC 3 (CST to AST conversion)
