package com.mrivanplays.rakija.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

@SuppressWarnings("unchecked")
public class RakijaEventListener implements EventListener
{

    private final List<ListenerEntry> listeners;

    public RakijaEventListener()
    {
        listeners = new ArrayList<>();
    }

    public <T extends GenericEvent> void addEventListener(Class<T> eventClass, Consumer<T> executor)
    {
        ListenerEntry<T> entry = new ListenerEntry<>(eventClass, executor);
        if (!listeners.contains(entry))
        {
            listeners.add(entry);
        }
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event)
    {
        for (ListenerEntry entry : listeners)
        {
            if (entry.getEventClass().isAssignableFrom(event.getClass()))
            {
                entry.getExecutor().accept(event);
            }
        }
    }

    private static class ListenerEntry<T extends GenericEvent>
    {

        private final Class<T> eventClass;
        private final Consumer<T> executor;

        ListenerEntry(Class<T> eventClass, Consumer<T> executor)
        {
            this.eventClass = eventClass;
            this.executor = executor;
        }

        Class<T> getEventClass()
        {
            return eventClass;
        }

        Consumer<T> getExecutor()
        {
            return executor;
        }
    }
}
