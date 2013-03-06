package ea.pguia.model;

import com.google.gson.Gson;

public class Subject {
	private int id;
	private String name;
	private int credits;

	public Subject(int id, String name, int credits) {
		super();
		this.id = id;
		this.name = name;
		this.credits = credits;
	}

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public int getCredits() {
		return credits;
	}
	
	public String toJSON() {
		return "{\"id\":" + id + ",\"name\":\"" + name + "\"," +
				"\"credits\":" + credits + "}";
	}

	@Override
	public String toString() {
		return "Subject [id=" + id + ", name=" + name + ", credits=" + credits
				+ "]";
	}

	public static void main(String[] args) {
		Subject s1 = new Subject(0, "mates", 6);
		System.out.println(new Gson().fromJson(s1.toJSON(), Subject.class));		
	}
	
}
