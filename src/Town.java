
public class Town implements Comparable<Town> {
	private String name;
	/**
	 * Creates a new town with the given name
	 * @param name the name of the new town
	 */
	public Town(String name) {
		this.name = name;
	}
	/**
	 * Creates a copy of an existing town
	 * @param other town to copy
	 */
	public Town(Town other) {
		this(other.name);
	}
	/**
	 * Gets the name of the town
	 * @return the town's name
	 */
	public String getName() {
		return name;
	}
	@Override
	public int compareTo(Town o) {
		return name.compareTo(o.name);
	}
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		try {
			Town other = (Town) o;
			return this.name.equals(other.name);
		} catch (ClassCastException e) {
			return false;
		}
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public String toString() {
		return name;
	}
}
