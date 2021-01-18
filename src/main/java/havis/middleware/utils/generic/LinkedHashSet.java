package havis.middleware.utils.generic;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public class LinkedHashSet<T> extends LinkedHashMap<T, T> {

	private static final long serialVersionUID = 1L;

	public T add(T key, T value) {
		T node = remove(key);
		if (node != null) {
			put(node, node);
			return node;
		} else {
			put(key, value);
			return key;
		}
	}

	public T update(T t) {
		T node = remove(t);
		if (node != null) {
			put(node, node);
			return node;
		} else {
			put(t, t);
			return t;
		}
	}

	public T tryGetValue(T key, Class<T> type) {
		T node = get(key);
		if (node != null) {
			return node;
		} else {
			try {
				return type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * Removes items from the head of the list while predicate returns true.
	 * Aborts once for the first item where the predicate does not match.
	 *
	 * @param predicate
	 *            The predicate
	 */
	public void remove(Predicate<T> predicate) {
		try {
			while (true) {
				Iterator<Entry<T, T>> iterator = entrySet().iterator();
				T value;
				if (iterator.hasNext() && (value = iterator.next().getValue()) != null
						&& predicate.invoke(value)) {
					iterator.remove();
				} else {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean removeFirst() {
		Iterator<Entry<T, T>> iterator = entrySet().iterator();
		if (iterator.hasNext() && iterator.next().getValue() != null) {
			iterator.remove();
			return true;
		} else {
			return false;
		}
	}

	public int getCount() {
		return size();
	}

	public Set<T> toList() {
		return keySet();
	}

	public void set(T key, T value) {
		put(key, value);
	}

	/**
	 * Retrieves if any element matches the condition
	 *
	 * @param predicate
	 *            The predicate
	 * @return True if any element matches, false otherwise
	 */
	public boolean find(Predicate<T> predicate) {
		for (T t : values()) {
			if (predicate.invoke(t))
				return true;
		}
		return false;
	}
}
