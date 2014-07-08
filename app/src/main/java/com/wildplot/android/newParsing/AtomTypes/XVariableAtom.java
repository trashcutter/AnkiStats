package com.wildplot.android.newParsing.AtomTypes;

import com.wildplot.android.newParsing.Atom;
import com.wildplot.android.newParsing.ExpressionFormatException;
import com.wildplot.android.newParsing.TopLevelParser;
import com.wildplot.android.newParsing.TreeElement;

/**
 * @author Michael Goldbach
 *
 */
public class XVariableAtom implements TreeElement {
    private Atom.AtomType atomType = Atom.AtomType.VARIABLE;
    private TopLevelParser parser;

    public XVariableAtom(TopLevelParser parser){
        this.parser = parser;
    }

    public Atom.AtomType getAtomType() {
        return atomType;
    }

    @Override
    public double getValue() {

        if (atomType != Atom.AtomType.INVALID){

            return parser.getX();
        }
        else
            throw new ExpressionFormatException("Number is Invalid, cannot parse");
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
