package ea.pguia.service.handlers;

import ea.pguia.model.AccountProfile;

public class HandlerInfo {
	private String name;
	private String handlerClass;
	private boolean auth;
	private AccountProfile profile;

	public HandlerInfo(String name, String handlerClass, boolean auth, AccountProfile profile) {
		super();
		this.name = name;
		this.handlerClass = handlerClass;
		this.auth = auth;
		this.profile = profile;
	}

	public String getName() {
		return name;
	}

	public String getHandlerClass() {
		return handlerClass;
	}

	public boolean needAuth() {
		return auth;
	}
	
	public boolean needProfile() {
		return (profile != AccountProfile.None);
	}
	
	public AccountProfile getProfile() {
		return profile;
	}

}
