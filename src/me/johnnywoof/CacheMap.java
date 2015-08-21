package me.johnnywoof;

import java.util.HashMap;

public class CacheMap<K, V> extends HashMap<K, V> {

	private final HashMap<Object, Long> timeCreated = new HashMap<>();
	private final long expiresIn;

	public CacheMap(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	@Override
	public V get(Object key) {

		if (this.timeCreated.containsKey(key) && (this.timeCreated.get(key) + this.expiresIn) < System.currentTimeMillis()) {
			this.remove(key);
		}

		return super.get(key);

	}

	@Override
	public boolean containsKey(Object key) {

		if (this.timeCreated.containsKey(key) && (this.timeCreated.get(key) + this.expiresIn) < System.currentTimeMillis()) {
			this.remove(key);
		}

		return super.containsKey(key);

	}

	@Override
	public V put(K key, V value) {

		this.timeCreated.put(key, System.currentTimeMillis());
		return super.put(key, value);

	}

}
