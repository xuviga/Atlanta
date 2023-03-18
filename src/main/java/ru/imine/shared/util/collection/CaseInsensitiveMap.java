package ru.imine.shared.util.collection;

import java.util.Map;
import java.util.TreeMap;


public class CaseInsensitiveMap<V> extends TreeMap<String,V>
{
    public CaseInsensitiveMap()
    {
        super(String.CASE_INSENSITIVE_ORDER);
    }
    public CaseInsensitiveMap(Map<String, V> map)
    {
        super(String.CASE_INSENSITIVE_ORDER);
        this.putAll(map);
    }
}