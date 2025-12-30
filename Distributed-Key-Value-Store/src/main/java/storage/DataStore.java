/*  4. DataStore.java
Goal: The "Vault." This is where the data actually lives. It must be thread-safe because multiple ClientHandler threads will be calling it at the exact same time.

What needs to be implemented:

Storage: A private ConcurrentHashMap<String, String> variable.

Note: Do not use a regular HashMap. If two threads resize a regular HashMap at the same time, it can cause an infinite loop or data corruption.

Methods:

public String get(String key): Returns the value or null.

public void set(String key, String value): Puts the value in the map.

public boolean delete(String key): Removes the item and returns true if it existed. */


import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private ConcurrentHashMap<String, String> store;
    public DataStore() {
        store = new ConcurrentHashMap<>();
    }
    public String get(String key) {
        if(store.containsKey(key)) {
            return store.get(key);
        }
        return null;
    }
    public void set(String key, String value) {
        store.put(key, value);
    }
    public boolean delete(String key) {
        return store.remove(key) != null;
    }
}
