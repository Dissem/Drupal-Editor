/**
 * Copyright (C) 2010 Christian Meyer
 * This file is part of Drupal Editor.
 *
 * Drupal Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Drupal Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Drupal Editor. If not, see <http://www.gnu.org/licenses/>.
 */
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
