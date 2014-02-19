package org.sankozi.jlogfilter.util;

import javafx.beans.property.IntegerProperty;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;

import java.text.DecimalFormat;

public class NumberField extends TextField {

    {
        setPrefWidth(50);
    }

    public static NumberField bidirectionalBinding(IntegerProperty property){
        NumberField ret = new NumberField();
        DecimalFormat df = new DecimalFormat();
        df.setGroupingSize(1000); //max int doesn't work
        ret.textProperty().bindBidirectional(property, df);
        return ret;
    }

    @Override
    public void replaceText(IndexRange indexRange, String text) {
        if (text.matches("[0-9]*")) {
            super.replaceText(indexRange, text);
        }
    }

    @Override
    public void replaceText(int i, int i2, String text) {
        if (text.matches("[0-9]*")) {
            super.replaceText(i, i2, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (text.matches("[0-9]*")) {
            super.replaceSelection(text);
        }
    }
}
