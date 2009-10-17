package ch.dissem.android.drupal.model;

public class NamedObject<T> {
	private String name;
	private T value;

	public NamedObject(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NamedObject<?>) {
			NamedObject<?> no = (NamedObject<?>) o;
			return name.equals(no.name) && value.equals(no.value);
		}
		if (value == null)
			return o == null;
		return value.equals(o);
	}
}
