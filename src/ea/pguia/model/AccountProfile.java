package ea.pguia.model;

public enum AccountProfile {
	None,
	Client,
	Admin;
	
	public static AccountProfile fromString(String acountProfile) {
		switch (acountProfile) {
		case "Client":
			return Client;
		case "Admin":
			return Admin;
		default:
			return None;
		}
	}
}