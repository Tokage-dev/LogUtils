package dev.tokage.Util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogLifecycleManager {
    private static Map<String, LogHandler> INSTANCES = new ConcurrentHashMap<>();

    /**
     * Registers the {@link LogHandler}  to a map of Instances to be called upon later.
     *
     * @param handler {@link LogHandler}  to be added
     */
    public static void register(LogHandler handler) {
        register(handler, false);
    }

    /**
     * Registers the {@link LogHandler}  and starts it's logging.
     *
     * @param handler {@link LogHandler} to be added
     * @param force   forces the instance in the map by the key to close and adds the new instance to the map
     */
    public static void register(LogHandler handler, boolean force) {
        if (INSTANCES.containsKey(handler.getKey())) {
            if (force) {
                //get current handler
                LogHandler oldHandler = INSTANCES.get(handler.getKey());
                //flush and close handler
                oldHandler.shutdown();
                INSTANCES.replace(handler.getKey(), handler);
            } else
                throw new IllegalStateException("Instance for key '" + handler.getKey() + "' is already initialized. Set force=true to overwrite, but don't say I didn't warn you, mortal.");
        } else {
            INSTANCES.put(handler.getKey(),handler);
        }
        handler.startLogger();
    }

    /**
     * returns an {@link LogHandler} instance if the key is present, else returns null.
     * @param key unique identifier of the instance you want.
     * @return {@link LogHandler} or null.
     */
    public static LogHandler getLogHandler(String key) {
        if (INSTANCES.containsKey(key)) {
            return INSTANCES.get(key);
        }
        return null;
    }

    /**
     * Cleans and closes all instances of {@link LogHandler}
     */
    public static void shutdown() {
        for (LogHandler h: INSTANCES.values()) {
            h.shutdown();
        }
        INSTANCES = new ConcurrentHashMap<>();
    }

}
