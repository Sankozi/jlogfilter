package org.sankozi.logfilter;

import com.google.inject.AbstractModule;

/**
 *
 */
public class StoreModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LogStore.class).to(ListLogStore.class);
        bind(LogConsumer.class).to(ListLogStore.class);
    }
}
