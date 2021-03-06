/* This file was generated by SableCC (http://www.sablecc.org/). */

package joosc.node;

import joosc.analysis.*;

@SuppressWarnings("nls")
public final class ATmpcastExp extends PExp
{
    private PExp _caster_;
    private PExp _castee_;

    public ATmpcastExp()
    {
        // Constructor
    }

    public ATmpcastExp(
        @SuppressWarnings("hiding") PExp _caster_,
        @SuppressWarnings("hiding") PExp _castee_)
    {
        // Constructor
        setCaster(_caster_);

        setCastee(_castee_);

    }

    @Override
    public Object clone()
    {
        return new ATmpcastExp(
            cloneNode(this._caster_),
            cloneNode(this._castee_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseATmpcastExp(this);
    }

    public PExp getCaster()
    {
        return this._caster_;
    }

    public void setCaster(PExp node)
    {
        if(this._caster_ != null)
        {
            this._caster_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._caster_ = node;
    }

    public PExp getCastee()
    {
        return this._castee_;
    }

    public void setCastee(PExp node)
    {
        if(this._castee_ != null)
        {
            this._castee_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._castee_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._caster_)
            + toString(this._castee_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._caster_ == child)
        {
            this._caster_ = null;
            return;
        }

        if(this._castee_ == child)
        {
            this._castee_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._caster_ == oldChild)
        {
            setCaster((PExp) newChild);
            return;
        }

        if(this._castee_ == oldChild)
        {
            setCastee((PExp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
