package me.pauzen.events;

import me.pauzen.events.event.Event;
import me.pauzen.events.event.EventHandler;
import me.pauzen.events.event.EventPriority;
import me.pauzen.events.event.HandlerList;
import me.pauzen.reflection.ReflectionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Caller {

    private static Caller caller;

    public static Caller getCaller() {
        if (caller == null) caller = new Caller();
        return caller;
    }

    public Caller() {
        caller = this;
    }

    private Map<Class<? extends Event>, List<Listener>> eventListeners = new HashMap<>();

    public void registerEventListener(Class<? extends Event> eventClass, Listener listener) {
        checkNull(eventClass);
        addListener(eventClass, listener);
    }

    private EnumMap<EventPriority, List<Method>> eventPriorityListEnumMap = new EnumMap<>(EventPriority.class);

    public void registerListener(Listener listener) {
        resetValues();
        for (Method method : ReflectionFactory.getMethodsHierarchic(listener.getClass())) {
            EventHandler annotation;
            if ((annotation = method.getAnnotation(EventHandler.class)) != null)
                eventPriorityListEnumMap.get(annotation.priority()).add(method);
        }
        for (List<Method> methods : eventPriorityListEnumMap.values()) {
            for (Method method : methods) {
                Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
                try {
                    Field handlerListField = ReflectionFactory.getField(eventClass, "handlerList");
                    handlerListField.setAccessible(true);
                    HandlerList handlers = (HandlerList) handlerListField.get(null);
                    handlers.addHandler(listener, method);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                registerEventListener(eventClass, listener);
            }
        }
    }

    private void resetValues() {
        for (EventPriority eventPriority : EventPriority.values()) {
            eventPriorityListEnumMap.put(eventPriority, new ArrayList<>());
        }
    }

    public void createListenerList(Class<? extends Event> eventClass) {
        eventListeners.put(eventClass, new ArrayList<Listener>());
    }

    public List<Listener> getListenerList(Class<? extends Event> eventClass) {
        return eventListeners.get(eventClass);
    }

    public void addListener(Class<? extends Event> eventClass, Listener listener) {
        if (!eventListeners.get(eventClass).contains(listener)) eventListeners.get(eventClass).add(listener);
    }

    private void checkNull(Class<? extends Event> eventClass) {
        if (getListenerList(eventClass) == null) createListenerList(eventClass);
    }

    public void callEvent(Event event) {
        List<Listener> listeners = eventListeners.get(event.getClass());
        if (listeners == null) return;
        for (Listener listener : listeners) {
            List<Method> methods = Event.getHandlers().getMethods(listener);
            methods.stream().filter(method -> method.getParameterTypes()[0] == event.getClass()).forEach(method -> {
                try {
                    method.invoke(listener, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
