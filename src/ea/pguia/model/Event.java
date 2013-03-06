package ea.pguia.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.google.gson.Gson;


public class Event {
	private int id;
	private String enterprise;
	private String text;
	private String inidate;
	private String enddate;
	private boolean promo;
		
	public Event(int id, String enterprise, String text, String inidate,
			String enddate, boolean promo) {
		super();
		this.id = id;
		this.enterprise = enterprise;
		this.text = text;
		this.inidate = inidate;
		this.enddate = enddate;
		this.promo = promo;
	}
	
	public int getId() {
		return id;
	}
	public String getEnterprise() {
		return enterprise;
	}
	public String getText() {
		return text;
	}
	public String getInidate() {
		return inidate;
	}
	public String getEnddate() {
		return enddate;
	}
	public boolean isPromo() {
		return promo;
	}
	
	public String toJSON() {
		return "{\"id\":\"" + id + "\",\"enterprise\":\"" + enterprise + "\"," +
				"\"text\":\"" + text + "\",\"inidate\":\"" + inidate + "\"," +
				"\"enddate\":\"" + enddate + "\",\"promo\":" + promo + "}";
	}
	
	public static Event fromJSON(String json) {
		return new Gson().fromJson(json, Event.class);
	}
	
	public static String toJSONVector(Event[] events) {
		return new Gson().toJson(events);
	}
	
	public static Event[] fromJSONVector(String json) {
		return new Gson().fromJson(json, Event[].class);
	}
	
	public static Event[] fromDB(ResultSet resultSet) throws SQLException {
		Vector<Event> events = new Vector<>();
		while(resultSet.next()) {
			Event event = new Event(
					resultSet.getInt("id"), 
					resultSet.getString("username"), 
					resultSet.getString("text"), 
					resultSet.getString("inidate"),
					resultSet.getString("enddate"),
					resultSet.getBoolean("promo")
			);
			events.add(event);
		}		
		return events.toArray(new Event[events.size()]);
	}
	
	@Override
	public String toString() {
		return "Event [id=" + id + ", enterprise=" + enterprise + ", text="
				+ text + ", inidate=" + inidate + ", enddate=" + enddate
				+ ", promo=" + promo + "]";
	}
}
