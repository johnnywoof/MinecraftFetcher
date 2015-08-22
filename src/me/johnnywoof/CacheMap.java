package me.johnnywoof;

import java.util.concurrent.ConcurrentHashMap;

public class CacheMap<K, V> extends ConcurrentHashMap<K, V> {

	private final ConcurrentHashMap<Object, Long> timeCreated = new ConcurrentHashMap<>();
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
