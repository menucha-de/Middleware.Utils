package havis.middleware.utils.generic;

public interface Predicate<E> {
	boolean invoke(E e);
}