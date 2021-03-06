/* This file was generated by SableCC (http://www.sablecc.org/). */

package joosc.node;

import java.util.*;
import joosc.analysis.*;

@SuppressWarnings("nls")
public final class ABlockStm extends PStm
{
    private final LinkedList<PStm> _stmts_ = new LinkedList<PStm>();

    public ABlockStm()
    {
        // Constructor
    }

    public ABlockStm(
        @SuppressWarnings("hiding") List<?> _stmts_)
    {
        // Constructor
        setStmts(_stmts_);

    }

    @Override
    public Object clone()
    {
        return new ABlockStm(
            cloneList(this._stmts_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABlockStm(this);
    }

    public LinkedList<PStm> getStmts()
    {
        return this._stmts_;
    }

    public void setStmts(List<?> list)
    {
        for(PStm e : this._stmts_)
        {
            e.parent(null);
        }
        this._stmts_.clear();

        for(Object obj_e : list)
        {
            PStm e = (PStm) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._stmts_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._stmts_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._stmts_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PStm> i = this._stmts_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PStm) newChild);
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
