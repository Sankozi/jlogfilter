package org.sankozi.jlogfilter;

import com.google.inject.AbstractModule;
import org.sankozi.jlogfilter.util.StringPool;

/**
 *
 */
public class StoreModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LogStore.class).to(ListLogStore.class);
        bind(LogConsumer.class).to(ListLogStore.class);
        bind(StringPool.class).toInstance(new StringPool());
        bind(LogEntryFactory.class).asEagerSingleton();
    }
}
