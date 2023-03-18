package ru.imine.server.core.database;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.imine.shared.util.JsonUtil;
import ru.imine.shared.util.collection.CaseInsensitiveConcurrentMap;
import ru.imine.shared.util.collection.CaseInsensitiveMap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("StatementWithEmptyBody")
public class Storage
{
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
    private static final CaseInsensitiveConcurrentMap<Storage> STORAGES = new CaseInsensitiveConcurrentMap<>();

    private static final Logger LOGGER = LogManager.getLogger(Storage.class);
    public final String name;

    protected final Path filePath;
    protected final Path tmpPath;

    private static final long LOCK_MAX_TIME = 5000L;

    private static final class LockData
    {
        public final long pid;
        private final long timestamp;

        public LockData(long pid)
        {
            this.pid = pid;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired()
        {
            return this.timestamp + LOCK_MAX_TIME <= System.currentTimeMillis();
        }
    }

    protected CaseInsensitiveConcurrentMap<LockData> fullLocks = new CaseInsensitiveConcurrentMap<>();
    protected CaseInsensitiveConcurrentMap<CaseInsensitiveConcurrentMap<String>> data = new CaseInsensitiveConcurrentMap<>();
    protected CaseInsensitiveConcurrentMap<CaseInsensitiveConcurrentMap<LockData>> locks = new CaseInsensitiveConcurrentMap<>();

    static
    {
        boolean ignored = new File("storage").mkdir();
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                STORAGES.values().forEach(Storage::save),
                "Storage Database Shutdown Hook"));
        EXECUTOR_SERVICE.scheduleAtFixedRate(() ->
                        STORAGES.values().forEach(Storage::save),
                30L, 30L, TimeUnit.SECONDS);
    }

    protected Storage(String name)
    {
        this.name = name;
        filePath = new File("storage", name + ".db").toPath();
        tmpPath = new File("storage", name + ".db.tmp").toPath();
        STORAGES.put(this.name, this);
        load();
    }

    public static Storage getStorage(String name)
    {
        Storage storage = STORAGES.get(name);
        if (storage != null)
            return storage;
        return load(name);
    }

    public static Storage load(String name)
    {
        return new Storage(name);
    }

    public void save()
    {
        try
        {
            try (PrintWriter out = new PrintWriter(tmpPath.toFile()))
            {
                JSONObject wholeData = new JSONObject();
                for (Map.Entry<String, CaseInsensitiveConcurrentMap<String>> entry : data.entrySet())
                {
                    JSONObject object = new JSONObject();
                    wholeData.put(entry.getKey(), object);
                    for (Map.Entry<String, String> subEntry : entry.getValue().entrySet())
                        object.put(subEntry.getKey(), subEntry.getValue());
                }
                SerializeWriter see = new SerializeWriter();
                JSONSerializer serializer = new JSONSerializer(see);
                serializer.config(SerializerFeature.PrettyFormat, true);
                serializer.write(wholeData);
                out.write(serializer.toString());
            }
            Files.move(tmpPath, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException exception)
        {
            LOGGER.fatal("Failed to save storage '{}'", name, exception);
        }
    }

    public void load()
    {
        try
        {
            JSONObject wholeData = JsonUtil.fromFile(filePath);
            if (wholeData != null)
                for (Map.Entry<String, Object> entry : wholeData.entrySet())
                {
                    JSONObject object = (JSONObject) entry.getValue();
                    CaseInsensitiveConcurrentMap<String> data = new CaseInsensitiveConcurrentMap<>();
                    for (Map.Entry<String, Object> subEntry : object.entrySet())
                        data.put(subEntry.getKey(), subEntry.getValue().toString());
                    this.data.put(entry.getKey(), data);
                }
        }
        catch (Exception exception)
        {
            LOGGER.fatal("Failed load load storage '{}'", name, exception);
        }
    }

    public boolean isEntirePathLocked(String path)
    {
        LockData lockData = fullLocks.get(path);
        return lockData != null && !lockData.isExpired() && lockData.pid != Thread.currentThread().getId();
    }

    public String get(String path, String key, String def)
    {
        while (isSingleKeyLocked(path, key)) ;
        CaseInsensitiveConcurrentMap<String> map = data.get(path);
        if (map == null)
            return def;
        String value = map.get(key);
        return value != null ? value : def;
    }

    public String get(String path, String key)
    {
        return get(path, key, null);
    }

    public int getInteger(String path, String key, int def)
    {
        while (isSingleKeyLocked(path, key)) ;
        try
        {
            CaseInsensitiveConcurrentMap<String> map = data.get(path);
            if (map == null)
                return def;
            String data = map.get(key);
            if (data == null)
                return def;
            return Integer.parseInt(data);
        }
        catch (Exception ignored)
        {
            return def;
        }
    }


    public long getLong(String path, String key, long def)
    {
        while (isSingleKeyLocked(path, key)) ;
        try
        {
            CaseInsensitiveConcurrentMap<String> map = data.get(path);
            if (map == null)
                return def;
            String data = map.get(key);
            if (data == null)
                return def;
            return Long.parseLong(data);
        }
        catch (Exception ignored)
        {
            return def;
        }
    }

    public float getFloat(String path, String key, float def)
    {
        while (isSingleKeyLocked(path, key)) ;
        try
        {
            CaseInsensitiveConcurrentMap<String> map = data.get(path);
            if (map == null)
                return def;
            String data = map.get(key);
            if (data == null)
                return def;
            return Float.parseFloat(data);
        }
        catch (Exception ignored)
        {
            return def;
        }
    }

    public double getDouble(String path, String key, double def)
    {
        while (isSingleKeyLocked(path, key)) ;
        try
        {
            CaseInsensitiveConcurrentMap<String> map = data.get(path);
            if (map == null)
                return def;
            String data = map.get(key);
            if (data == null)
                return def;
            return Double.parseDouble(data);
        }
        catch (Exception ignored)
        {
            return def;
        }
    }

    public boolean getBoolean(String path, String key)
    {
        while (isSingleKeyLocked(path, key)) ;
        CaseInsensitiveConcurrentMap<String> map = data.get(path);
        return map != null && map.containsKey(key);
    }

    public CaseInsensitiveMap<String> getAll(String path)
    {
        while (isAnyKeyAtPathLocked(path)) ;
        CaseInsensitiveConcurrentMap<String> map = data.get(path);
        return map == null ? new CaseInsensitiveMap<>() : new CaseInsensitiveMap<>(map);
    }

    public void set(String path, String key, Number value)
    {
        if (value == null)
            deleteKey(path, key);
        else
            set(path, key, value.toString());
    }

    public void set(String path, String key, String value)
    {
        while (isSingleKeyLocked(path, key)) ;
        if (value == null)
        {
            deleteKey(path, key);
            return;
        }
        CaseInsensitiveConcurrentMap<String> map = data.computeIfAbsent(path, it -> new CaseInsensitiveConcurrentMap<>());
        map.put(key, value);
    }

    public void set(String path, String key, boolean value)
    {
        while (isSingleKeyLocked(path, key)) ;
        CaseInsensitiveConcurrentMap<String> map = data.computeIfAbsent(path,it -> new CaseInsensitiveConcurrentMap<>());
        if (value)
            map.put(key, "1");
        else
            map.remove(key);
    }


    public boolean keyExists(String path, String key)
    {
        while (isSingleKeyLocked(path, key)) ;
        CaseInsensitiveConcurrentMap map = data.get(path);
        return map != null && map.containsKey(key);
    }

    //Удаляет запись %key% из карты %path%
    //Возвращает true если значение ранее было, false если не было
    public boolean deleteKey(String path, String key)
    {
        while (isSingleKeyLocked(path, key)) ;
        CaseInsensitiveConcurrentMap<String> map = data.get(path);
        return map != null && map.remove(key) != null;
    }

    public Set<String> getKeys(String path)
    {
        while (isAnyKeyAtPathLocked(path)) ;
        CaseInsensitiveConcurrentMap<String> map = data.get(path);
        return map == null ? new HashSet<>() : map.keySet();
    }

    public boolean deletePath(String path)
    {
        while (isAnyKeyAtPathLocked(path)) ;
        return data.remove(path) != null;
    }

    public boolean isAnyKeyAtPathLocked(String path)
    {
        CaseInsensitiveConcurrentMap<LockData> maplocks = locks.get(path);
        return maplocks != null && maplocks.values().stream()
                .anyMatch(it -> it != null && !it.isExpired() && it.pid != Thread.currentThread().getId());
    }

    public boolean isSingleKeyLocked(String path, String key)
    {
        if (isEntirePathLocked(path))
            return true;
        CaseInsensitiveConcurrentMap<LockData> maplocks = locks.get(path);
        if (maplocks == null)
            return false;
        LockData lockData = maplocks.get(key);
        return lockData != null && !lockData.isExpired() && lockData.pid != Thread.currentThread().getId();
    }

    public void lockEntirePath(String path)
    {
        while (isEntirePathLocked(path))
        {
        }
        fullLocks.put(path, new LockData(Thread.currentThread().getId()));
    }

    public void lockSingleKey(String path, String key)
    {
        CaseInsensitiveConcurrentMap<LockData> maplocks = locks.get(path);
        for (; ; )
        {
            if (maplocks == null)
                break;
            LockData lockData = maplocks.get(key);
            if (lockData == null || lockData.isExpired() || lockData.pid == Thread.currentThread().getId())
                break;
        }
        if (maplocks == null)
            locks.put(path, maplocks = new CaseInsensitiveConcurrentMap<>());
        maplocks.put(path, new LockData(Thread.currentThread().getId()));
    }

    public void unlockEntirePath(String path)
    {
        fullLocks.remove(path);
    }

    public void unlockSingleKey(String path, String key)
    {
        CaseInsensitiveConcurrentMap<LockData> maplocks = locks.get(path);
        if (maplocks == null)
            return;
        maplocks.remove(key);
        if (maplocks.isEmpty())
            locks.remove(path);
    }

    public boolean keyExists(String path)
    {
        for (; ; )
        {
            CaseInsensitiveConcurrentMap<LockData> maplocks = locks.get(path);
            if (maplocks == null)
            {
                break;
            }
        }
        return data.containsKey(path);
    }


    public Set<String> getPathes(String pattern)
    {
        Pattern regex = Pattern.compile(pattern);
        return data.keySet().stream().filter(it -> regex.matcher(it).matches())
                .collect(Collectors.toSet());
    }
}