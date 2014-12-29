package me.pauzen.events.event;

import me.pauzen.events.Listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerList {

    private Map<Listener, List<Method>> handlers = new HashMap<>();

    public void addHandler(Listener listener, Method method) {
        checkNull(listener);
        handlers.get(listener).add(method);
    }

    private void checkNull(Listener listener) {
        if (handlers.get(listener) == null) handlers.put(listener, new ArrayList<Method>());
    }

    public List<Method> getMethods(Listener listener) {
        return handlers.get(listener);
    }
}
