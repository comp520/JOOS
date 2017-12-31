/* This file was generated by SableCC (http://www.sablecc.org/). */

package joosc.node;

import java.util.*;
import joosc.analysis.*;

@SuppressWarnings("nls")
public final class ASuperconsStm extends PStm
{
    private final LinkedList<PExp> _args_ = new LinkedList<PExp>();

    public ASuperconsStm()
    {
        // Constructor
    }

    public ASuperconsStm(
        @SuppressWarnings("hiding") List<?> _args_)
    {
        // Constructor
        setArgs(_args_);

    }

    @Override
    public Object clone()
    {
        return new ASuperconsStm(
            cloneList(this._args_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseASuperconsStm(this);
    }

    public LinkedList<PExp> getArgs()
    {
        return this._args_;
    }

    public void setArgs(List<?> list)
    {
        for(PExp e : this._args_)
        {
            e.parent(null);
        }
        this._args_.clear();

        for(Object obj_e : list)
        {
            PExp e = (PExp) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._args_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._args_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._args_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PExp> i = this._args_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PExp) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}