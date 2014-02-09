package org.sankozi.jlogfilter.util;

import com.google.common.collect.MapMaker;

import java.util.Map;

/**
 *  Object storing instances of strings for reuse
 */
public final class StringPool {

    private final Map<String, String> pool = new MapMaker()
                        .concurrencyLevel(2)
                        .initialCapacity(16)
                        .makeMap();

    /**
     * Returns equal string that is inside this pool. Adding strings if necessary.
     * @param string string
     * @return string equal to parameter that is inside this pool
     */
    public String getString(String string){
        String ret = pool.get(string);
        if(ret == null){
            pool.put(string, string);
            ret = string;
            System.out.append(Integer.toString(pool.size())).append(" strings in StringPool").append('\n');
        }
        return ret;
    }
}
