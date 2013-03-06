package ea.pguia.service.handlers;

public class HandlerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -615294457032682926L;
	String message;
	int code;

	public HandlerException(int code, String message) {
		super();
		this.message = message;
		this.code = code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}
}
