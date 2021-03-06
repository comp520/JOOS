/*
 * JOOSJ is Copyright (C) 2000 Othman Alaoui
 *
 * Reproduction of all or part of this software is permitted for
 * educational or research use on condition that this copyright notice is
 * included in any copy. This software comes with no warranty of any
 * kind. In no event will the author be liable for any damages resulting from
 * use of this software.
 *
 * email: oalaou@po-box.mcgill.ca
 */


/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

/* Reference Version: $SootVersion: 1.beta.6.dev.34 $ */

package soot.util;

import java.util.*; // modified by Othman

/** Reference implementation of the Chain interface, using a HashMap as the underlying structure. */
public class HashChain extends AbstractCollection
    implements Chain 
{
    
    private HashMap map = new HashMap(); 
    private Object firstItem;
    private Object lastItem;
    private long stateCount = 0;  

	// commented out by Othman
    /** Converts the given List to a freshly-generated HashChain. */
//    public static Chain listToHashChain(List l)
//    {
//        Iterator it = l.iterator();
//        HashChain chain = new HashChain();
//        HashMap bindings = new HashMap();
//
//        while(it.hasNext()) {
//            Unit tempp = (Unit) it.next();
//            System.out.println(tempp.toString());
//            Unit copy = (Unit) tempp.clone();
//            bindings.put(tempp,copy);
//            chain.addLast(copy);
//        }
//
//        return chain;
//    }

    /** Erases the contents of the current HashChain. */
    public void clear() 
    {
        stateCount++;
        firstItem = lastItem = null;
        map.clear();
    }

    public void swapWith(Object out, Object in)
    {
        insertBefore(in, out);
        remove(out);
    }
    
    /** Adds the given object to this HashChain. */
    public boolean add(Object item) 
    {
        stateCount++;
        addLast(item);
        return true;
    }

    /** Returns an unbacked list containing the contents of the given Chain. */
    public static List toList(Chain c)
    {
        Iterator it = c.iterator();
        List list = new ArrayList();
        
        while(it.hasNext()) {
            list.add(it.next());
        }
        
        return list;
    }

    /** Constructs an empty HashChain. */
    public HashChain()
    { 
        firstItem = lastItem = null;
    }
   

   
    public boolean follows(Object someObject, Object someReferenceObject)
    {
        Iterator it = iterator(someObject);
        while(it.hasNext()) {

            if(it.next() == someReferenceObject)
                return false;
        }
         return true;
    }
    
    public void insertAfter(Object toInsert, Object point)
    {
        if(map.containsKey(toInsert))
            throw new RuntimeException("Chain already contains object.");
        stateCount++;
        Link temp = (Link) map.get(point);
        
        Link newLink = temp.insertAfter(toInsert);
        map.put(toInsert, newLink);    
    }

    public void insertAfter(List toInsert, Object point)
    {
        Object previousPoint = point;
        Iterator it = toInsert.iterator();
        while (it.hasNext())
        {
            Object o = it.next();
            insertAfter(o, previousPoint);
            previousPoint = o;
        }
    }

    
    public void insertBefore(List toInsert, Object point)
    {
        LinkedList backwardList = new LinkedList();
        // Insert toInsert backwards into the list
        {
            Iterator it = toInsert.iterator();
            
            while(it.hasNext())
                backwardList.addFirst(it.next());
        }
                
        Object previousPoint = point;
        Iterator it = backwardList.iterator();
        while (it.hasNext())
        {
            Object o = it.next();
            insertBefore(o, previousPoint);
            previousPoint = o;
        }
    }

    public void insertBefore(Object toInsert, Object point)
    {

        if(map.containsKey(toInsert))
            throw new RuntimeException("Chain already contains object.");
        stateCount++;
        Link temp = (Link) map.get(point);
        
        Link newLink = temp.insertBefore(toInsert);
        map.put(toInsert, newLink);
    }
  
    public boolean remove(Object item)
    { 
        stateCount++;
        Link link = (Link) map.get(item);
        
        link.unlinkSelf();
        map.remove(item);
        return true;
    }

    public void addFirst(Object item)
    {
        stateCount++;
        Link newLink, temp;

        if(map.containsKey(item))
            throw new RuntimeException("Chain already contains object.");

        if(firstItem != null) {
            temp = (Link) map.get(firstItem);
            newLink = temp.insertBefore(item);
        } else {
            newLink = new Link(item);
            firstItem = lastItem = item;
        }
        map.put(item, newLink);
    }

    public void addLast(Object item)
    {
        stateCount++;
        Link newLink, temp;
        if(map.containsKey(item))
            throw new RuntimeException("Chain already contains object.");
        
        if(lastItem != null) {
            temp = (Link) map.get(lastItem);
            newLink = temp.insertAfter(item);   
        } else {
            newLink = new Link(item);
            firstItem = lastItem = item;
        }            
              map.put(item, newLink);
    }
    
    public void removeFirst()
    {
        stateCount++;
        ((Link) map.get(firstItem)).unlinkSelf();
    }

    public void removeLast()
    {
        stateCount++;
        //        Object temp = getPredOf(
        ((Link) map.get(lastItem)).unlinkSelf();
        //lastItem = temp;
    }

    public Object getFirst(){ 
        
        if(firstItem == null) 
            throw new NoSuchElementException();
        return firstItem; 
    }
    
    public Object getLast(){ 
        if(lastItem == null) 
            throw new NoSuchElementException();
        return lastItem; 
    }

    public Object getSuccOf(Object point) 
        throws NoSuchElementException
    {
        Link link = (Link) map.get(point);
        try {
            link = link.getNext();
        }
        catch (NullPointerException e) {
            throw new NoSuchElementException();
        }
        if(link == null) 
            return null;
        
        return link.getItem();
    } 
    
    public Object getPredOf(Object point)
        throws NoSuchElementException
    {
        Link link = (Link) map.get(point);
        if(point == null)
            throw new RuntimeException("trying to hash null value.");

        try {
            link = link.getPrevious();
        }
        catch (NullPointerException e) {
            throw new NoSuchElementException();
        }
        
        if(link == null) 
            return null;
        else
            return link.getItem();
    } 
    
    public Iterator snapshotIterator() 
    {
        List l = new ArrayList(); l.addAll(this);
        return l.iterator();
    }
   
    public Iterator iterator(){ return new LinkIterator(firstItem); }
    public Iterator iterator(Object item) 
    {
        return new LinkIterator(item);
    }

    /** Returns an iterator ranging from head to tail, inclusive. */
    public Iterator iterator(Object head, Object tail)
    {
        return new LinkIterator(head, tail);
    }


    public int size(){ return map.size(); }               

    /** Returns a textual representation of the contents of this Chain. */
    public String toString() 
    {
        StringBuffer strBuf = new StringBuffer();
        Iterator it = iterator();
        boolean b = false;

        strBuf.append("[");
        while(it.hasNext()) {
            if (!b) b = true; else strBuf.append(", ");
            strBuf.append(it.next().toString());
        }
        strBuf.append("]");
        return strBuf.toString();
    }
    

    class Link {
        private Link nextLink;
        private Link previousLink;
        private Object item;
        private int index;
        
        public Link(Object item)
        {
            this.item = item;
            nextLink = previousLink = null;
        }

        public Link getNext() { return nextLink;}
        public Link getPrevious() { return previousLink;}

        public void setNext(Link link) { this.nextLink = link;}
        public void setPrevious(Link link) { this.previousLink = link;}

        
        public void unlinkSelf() 
        {
            bind(previousLink, nextLink);
            
        }

        public Link insertAfter(Object item)
        {
            Link newLink = new Link(item);
            
            bind(newLink, nextLink);
            bind(this, newLink);
            return newLink;
        }
        
        public Link insertBefore(Object item)
        {
            Link newLink = new Link(item);
            
            bind(previousLink, newLink);
            bind(newLink, this);
            return newLink;
        }
        

        private void bind(Link a, Link b) 
        {
            if(a == null) {
                if(b != null)
                    firstItem = b.getItem();
                else
                    firstItem = null;
            } else 
                a.setNext(b);
          
            if(b == null){
                if(a != null)
                    lastItem = a.getItem();
                else
                    lastItem = null;
            }
            else
                b.setPrevious(a);
        }

        public Object getItem() { return item; }


        public String toString() 
        {
            if(item != null)
                return item.toString();
            else
                return  "Link item is null" + super.toString();

        }
    
    }

 class LinkIterator implements Iterator 
    {
        private Link currentLink;
        boolean state;    // only when this is true can remove() be called 
                          // (in accordance w/ iterator semantics)
            
        boolean stop;
        private Object destination;
        private long iteratorStateCount;


        public LinkIterator(Object item) 
        {
            currentLink = new Link(null);
            currentLink.setNext((Link) map.get(item));
            state = false;
            stop = false;
            destination = null;
            iteratorStateCount = stateCount;
        }

        public LinkIterator(Object from, Object to)
        {
            this(from);
            destination = to;
        }

            
        public boolean hasNext() 
        {
            if(stateCount != iteratorStateCount)
                throw new ConcurrentModificationException();
            
            if(currentLink.getNext() == null)
                return false;
            else
                return !stop;
        }
            
        public Object next()
            throws NoSuchElementException
        {
            if(stateCount != iteratorStateCount)
                throw new ConcurrentModificationException();
                        
            Link temp = currentLink.getNext();
            if(temp == null || stop)
                throw new NoSuchElementException(temp + " " + stop);

            currentLink = temp;

            if(destination != null)
                if(destination == currentLink.getItem()) 
                    stop = true;

            state = true;
            return currentLink.getItem();
        }

        public void remove()
            throws IllegalStateException
     {
         if(stateCount != iteratorStateCount)
             throw new ConcurrentModificationException();
            
            stateCount++; iteratorStateCount++;
            if(!state)
                throw new IllegalStateException();
            else {
                currentLink.unlinkSelf();
                map.remove(currentLink.getItem());
                state = false;
            }

        }
        
            
        public String toString() 
        {
            if(currentLink == null) 
                return  "Current object under iterator is null" + super.toString();
            else
                return currentLink.toString();
        }
    }    
}

