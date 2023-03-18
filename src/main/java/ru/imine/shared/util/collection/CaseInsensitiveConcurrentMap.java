package ru.imine.shared.util.collection;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;


public class CaseInsensitiveConcurrentMap<V> extends ConcurrentSkipListMap<String, V>
{
    public CaseInsensitiveConcurrentMap()
    {
        super(String.CASE_INSENSITIVE_ORDER);
    }
    public CaseInsensitiveConcurrentMap(Map<String, V> map)
    {
        super(String.CASE_INSENSITIVE_ORDER);
        this.putAll(map);
    }
}