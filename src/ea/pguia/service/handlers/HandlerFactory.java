package ea.pguia.service.handlers;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ea.pguia.model.AccountProfile;
import ea.pguia.service.ServiceServlet;

public class HandlerFactory {
	private static HandlerFactory instance = null;
	private Hashtable<String, HandlerInfo> handlerList = null;
	private DataSource dataSource;

	private HandlerFactory() {
		try {
			Context initContext = new InitialContext();
			Context context = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) context.lookup("jdbc/PguiaDB");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
		handlerList = new Hashtable<String, HandlerInfo>();
		try {
			loadHandlers();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HandlerFactory newInstance() {
		if (instance == null)
			instance = new HandlerFactory();
		return instance;
	}
	
	public Handler createHandler(String action) throws HandlerException, InstantiationException, IllegalAccessException{
		HandlerInfo hInfo = handlerList.get(action);
		if(hInfo == null) 
			throw new HandlerException(404, "Action not found: '" +  action + "'");
		
		Handler handler;
		try {
			handler = (Handler) Class.forName(hInfo.getHandlerClass()).newInstance();
		} catch (ClassNotFoundException e) {
			throw new HandlerException(404, "Handler class not found: " + action);
		}
		
		handler.setDataSource(dataSource);
		return handler;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public HandlerInfo getInfo(String action) throws HandlerException {
		HandlerInfo hInfo = handlerList.get(action);
		if(hInfo == null ) throw new HandlerException(404, "Action not found: '" +  action + "'");
		
		return hInfo;
	}

	private void loadHandlers() throws ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(ServiceServlet.class
				.getResourceAsStream("/handlers.xml"));
		
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getDocumentElement().getChildNodes();
		
		Node node = nList.item(0);
		while((node = node.getNextSibling()) != null)
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element nElement = (Element) node;
				String hAction = nElement.getElementsByTagName("action").item(0).getTextContent();
				HandlerInfo hInfo = new HandlerInfo(
						hAction,
						"ea.pguia.service.handlers.actions." + nElement.getElementsByTagName("handler-class").item(0).getTextContent(),
						nElement.getAttribute("auth").equals("true"),
						AccountProfile.fromString(nElement.getAttribute("profile"))
				);
				handlerList.put(hAction, hInfo);
			}
	}
}
