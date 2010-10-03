package be.vbsteven.quickcopy;

public class Entry implements Comparable<Entry> {

	public int id;
	public String value;
	public boolean hidden = false;
	public String key;
	public int group;

	public Entry(int id, String value, boolean hidden, String key, int group) {
		this.id = id;
		this.value = value;
		this.hidden = hidden;
		if (key != null) {
			this.key = key;
		} else {
			this.key = value;
		}
	}

	@Override
	public int compareTo(Entry o) {
		return (this.key.toLowerCase().compareTo(o.key.toLowerCase()));
	}

	@Override
	public String toString() {
		return value;
	}
}
