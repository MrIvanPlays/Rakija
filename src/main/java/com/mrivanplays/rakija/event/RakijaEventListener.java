/*
    Copyright (c) 2019 Ivan Pekov
    Copyright (c) 2019 Contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/
package com.mrivanplays.rakija.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class RakijaEventListener implements EventListener {

  private final List<ListenerEntry> listeners;

  public RakijaEventListener() {
    listeners = new ArrayList<>();
  }

  public <T extends GenericEvent> void addEventListener(Class<T> eventClass, Consumer<T> executor) {
    ListenerEntry<T> entry = new ListenerEntry<>(eventClass, executor);
    if (!listeners.contains(entry)) {
      listeners.add(entry);
    }
  }

  @Override
  public void onEvent(@Nonnull GenericEvent event) {
    for (ListenerEntry entry : listeners) {
      if (entry.getEventClass().isAssignableFrom(event.getClass())) {
        entry.getExecutor().accept(event);
      }
    }
  }

  private static class ListenerEntry<T extends GenericEvent> {

    private final Class<T> eventClass;
    private final Consumer<T> executor;

    ListenerEntry(Class<T> eventClass, Consumer<T> executor) {
      this.eventClass = eventClass;
      this.executor = executor;
    }

    Class<T> getEventClass() {
      return eventClass;
    }

    Consumer<T> getExecutor() {
      return executor;
    }
  }
}
