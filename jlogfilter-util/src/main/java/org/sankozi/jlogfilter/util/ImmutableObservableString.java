package org.sankozi.jlogfilter.util;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;

/**
 *  Implementation of ObservableStringValue that contains immutable string
 */
public class ImmutableObservableString implements ObservableStringValue {
    private final String value;

    private ImmutableObservableString(String value) {
        this.value = value;
    }

    public static ObservableStringValue immutableString(String string){
        return new ImmutableObservableString(string);
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {}

    @Override
    public void removeListener(InvalidationListener invalidationListener) {}

    @Override
    public void addListener(ChangeListener<? super String> changeListener) {}

    @Override
    public void removeListener(ChangeListener<? super String> changeListener) {}
}
