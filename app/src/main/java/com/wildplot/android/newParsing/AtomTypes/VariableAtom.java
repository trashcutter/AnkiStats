package com.wildplot.android.newParsing.AtomTypes;

import com.wildplot.android.newParsing.Atom;
import com.wildplot.android.newParsing.ExpressionFormatException;
import com.wildplot.android.newParsing.TopLevelParser;
import com.wildplot.android.newParsing.TreeElement;

import java.util.regex.Pattern;

/**
 * @author Michael Goldbach
 *
 */
public class VariableAtom implements TreeElement {
    //Todo register VarName in TopLevelParser
    private Atom.AtomType atomType = Atom.AtomType.NUMBER;
    private TopLevelParser parser;
    private String varName;

    public VariableAtom(String factorString, TopLevelParser parser){
        this.parser = parser;
        this.varName = factorString;
        Pattern p = Pattern.compile("[^a-zA-Z0-9]");
        boolean hasSpecialChar = p.matcher(varName).find();
        if(hasSpecialChar || !(varName.length() > 0)){
            this.atomType = Atom.AtomType.INVALID;
        }
    }

    public Atom.AtomType getAtomType() {
        return atomType;
    }

    @Override
    public double getValue() {

        if (atomType != Atom.AtomType.INVALID){

            return parser.getVarVal(varName);
        }
        else
            throw new ExpressionFormatException("Number is Invalid, cannot parse");
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
