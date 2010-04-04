package be.vbsteven.quickcopy;

public class ListItem implements Comparable<ListItem> {
	public static final int ITEM = 0;
	public static final int FOLDER = 1;
	
	public int id;
	public int type;
	public String value;
	
	public ListItem(int id, String value, int type) {
		this.id = id;
		this.value = value;
		this.type = type;
	}

	@Override
	public int compareTo(ListItem o) {
		if (this.type == o.type) {
			return (this.value.toLowerCase().compareTo(o.value.toLowerCase()));
		} else {
			if (type == FOLDER) {
				return -1;
			} else {
				return 1;
			}
		}
	}
	
	public boolean isFolder() {
		return type == FOLDER;
	}
	
	public boolean isItem() {
		return type == ITEM;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
