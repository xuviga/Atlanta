package ru.imine.server.core.database;

import ru.imine.shared.util.collection.CaseInsensitiveConcurrentMap;
import ru.imine.shared.util.collection.CaseInsensitiveMap;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Row
{
    Map<Integer, Object> data_int = new ConcurrentHashMap<>();
    Map<String, Object> data = new CaseInsensitiveConcurrentMap<>();

    public Object getObject(String column)
    {
        return data.get(column);
    }

    public String getString(String column)
    {
        Object object = getObject(column);
        return object == null ? null : object.toString();
    }

    public boolean getBoolean(String column)
    {
        try
        {
            return (boolean) data.get(column);
        }
        catch (Exception ignore)
        {
            try
            {
                return getNumber(column).intValue() != 0;
            }
            catch (Exception ignored)
            {
            }
        }
        return false;
    }

    public Number getNumber(String column)
    {
        return (Number) data.get(column);
    }

    public Integer getInteger(String column)
    {
        Number number = getNumber(column);
        return number==null ? null : number.intValue();
    }

    public BigInteger getBigInteger(String column)
    {
        return (BigInteger) getNumber(column);
    }

    public Long getLong(String column)
    {
        Number number = getNumber(column);
        return number==null ? null : number.longValue();
    }

    public Double getDouble(String column)
    {
        Number number = getNumber(column);
        return number==null ? null : number.doubleValue();
    }

    public <T> T getGeneric(String column)
    {
        return (T) data.get(column);
    }

    public Object getObject(int column)
    {
        return data_int.get(column);
    }

    public String getString(int column)
    {
        Object object = getObject(column);
        return object == null ? null : object.toString();
    }

    public boolean getBoolean(int column)
    {
        try
        {
            return (boolean) data_int.get(column);
        }
        catch (Exception ignored)
        {
            try
            {
                return getNumber(column).intValue() != 0;
            }
            catch (Exception ignore)
            {
            }
        }
        return false;
    }

    public Number getNumber(int column)
    {
        return (Number) data_int.get(column);
    }

    public Integer getInteger(int column)
    {
        Number number = getNumber(column);
        return number==null ? null : number.intValue();
    }

    public BigInteger getBigInteger(int column)
    {
        return (BigInteger) data_int.get(column);
    }

    public Long getLong(int column)
    {
        Number number = getNumber(column);
        return number==null ? null : number.longValue();
    }

    public Double getDouble(int column)
    {
        Number number = getNumber(column);
        return number==null ? null : number.doubleValue();
    }

    public <T> T getGeneric(int column)
    {
        return (T) data_int.get(column);
    }

    public Map<String, Object> getData()
    {
        return new CaseInsensitiveMap<>(data);
    }
}