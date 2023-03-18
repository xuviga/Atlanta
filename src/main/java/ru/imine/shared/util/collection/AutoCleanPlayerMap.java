package ru.imine.shared.util.collection;

import com.google.common.base.Preconditions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.imine.server.core.player.AiMinePlayerEvent;
import ru.imine.server.core.player.AiMinePlayerMP;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Карта с ключем, который является игроком, в данный момент находящимся на сервере
 * Как только игрок выходит с сервера, он автоматически вычищается из всех таких карт
 * В карту можно добавить как ключи только игроков, находящихся сейчас на сервере
 *
 * @param <T> тип значений карты
 */
public class AutoCleanPlayerMap<T> extends HashMap<AiMinePlayerMP, T>
{
    public static final class CleanupListener
    {
        public static final CleanupListener INSTANCE;

        static
        {
            INSTANCE = new CleanupListener();
            MinecraftForge.EVENT_BUS.register(INSTANCE);
        }

        private CleanupListener()
        {
        }

        private final Set<WeakReference<AutoCleanPlayerMap<?>>> activeMaps = Collections.synchronizedSet(new HashSet<>());

        private void addTrackedMap(AutoCleanPlayerMap<?> autoCleanPlayerMap)
        {
            activeMaps.add(new WeakReference<>(autoCleanPlayerMap));
        }

        @SubscribeEvent
        public void onEvent(AiMinePlayerEvent.LeaveEvent event)
        {
            synchronized (activeMaps)
            {
                Iterator<WeakReference<AutoCleanPlayerMap<?>>> iterator = activeMaps.iterator();
                while (iterator.hasNext())
                {
                    WeakReference<AutoCleanPlayerMap<?>> reference = iterator.next();
                    AutoCleanPlayerMap<?> autoCleanPlayerMap = reference.get();
                    if (autoCleanPlayerMap == null)
                    {
                        iterator.remove();
                    }
                    else if (autoCleanPlayerMap.containsKey(event.player))
                    {
                        autoCleanPlayerMap.remove(event.player);
                    }
                }
            }
        }
    }

    public AutoCleanPlayerMap()
    {
        CleanupListener.INSTANCE.addTrackedMap(this);
    }

    public AutoCleanPlayerMap(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
        CleanupListener.INSTANCE.addTrackedMap(this);
    }

    public AutoCleanPlayerMap(int initialCapacity)
    {
        super(initialCapacity);
        CleanupListener.INSTANCE.addTrackedMap(this);
    }

    public AutoCleanPlayerMap(Map<? extends AiMinePlayerMP, ? extends T> m)
    {
        super(m);
        CleanupListener.INSTANCE.addTrackedMap(this);
    }

    @Override
    public T put(AiMinePlayerMP key, T value)
    {
        Preconditions.checkArgument(key.isOnline(), "Couldn't add disconnected player to player map");
        return super.put(key, value);
    }

    @Override
    public T putIfAbsent(AiMinePlayerMP key, T value)
    {
        Preconditions.checkArgument(key.isOnline(), "Couldn't add disconnected player to player map");
        return super.putIfAbsent(key, value);
    }

    @Override
    public void putAll(Map<? extends AiMinePlayerMP, ? extends T> m)
    {
        for (AiMinePlayerMP key : m.keySet())
        {
            Preconditions.checkArgument(key.isOnline(), "Couldn't add disconnected player to player map");
        }
        super.putAll(m);
    }

    @Override
    public boolean replace(AiMinePlayerMP key, T oldValue, T newValue)
    {
        Preconditions.checkArgument(key.isOnline(), "Couldn't add disconnected player to player map");
        return super.replace(key, oldValue, newValue);
    }

    @Override
    public T replace(AiMinePlayerMP key, T value)
    {
        Preconditions.checkArgument(key.isOnline(), "Couldn't add disconnected player to player map");
        return super.replace(key, value);
    }

    @Override
    public T computeIfAbsent(AiMinePlayerMP key, Function<? super AiMinePlayerMP, ? extends T> mappingFunction)
    {
        Preconditions.checkArgument(key.isOnline(), "Couldn't add disconnected player to player map");
        return super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public T computeIfPresent(AiMinePlayerMP key, BiFunction<? super AiMinePlayerMP, ? super T, ? extends T> remappingFunction)
    {
        Preconditions.checkArgument(key.isOnline(), "Couldn't add disconnected player to player map");
        return super.computeIfPresent(key, remappingFunction);
    }

    @Override
    public T compute(AiMinePlayerMP key, BiFunction<? super AiMinePlayerMP, ? super T, ? extends T> remappingFunction)
    {
        Preconditions.checkArgument(key.isOnline(), "Couldn't add disconnected player to player map");
        return super.compute(key, remappingFunction);
    }

    @Override
    public T merge(AiMinePlayerMP key, T value, BiFunction<? super T, ? super T, ? extends T> remappingFunction)
    {
        Preconditions.checkArgument(key.isOnline(), "Couldn't add disconnected player to player map");
        return super.merge(key, value, remappingFunction);
    }

}