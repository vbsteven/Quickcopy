package be.vbsteven.qccommon;

public class Group implements Comparable<Group> {
	public int id;
	public String name;
	
	public Group(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Group another) {
		return this.name.toLowerCase().compareTo(another.name.toLowerCase());
	}
	
	
}
