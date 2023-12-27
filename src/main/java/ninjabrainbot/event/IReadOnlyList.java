package ninjabrainbot.event;

public interface IReadOnlyList<T> extends Iterable<T> {

	T get(int index);

	int size();

	default T getLast() {
		return get(size() - 1);
	}

}
