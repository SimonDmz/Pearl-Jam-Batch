package fr.insee.pearljam.batch.service;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Service
public class FolderService {
	@Autowired
	String getFolderIn;
	
	@Autowired
	String getFolderOut;
	
	@Autowired
	String getFolderInQueen;
	
	@Autowired
	@Qualifier("filename")
	String filename;
	
	@Autowired
	String getCampaignName;
	
	public String getFolderIn() {
		return getFolderIn;
	}
	
	public String getFolderOut() {
		return getFolderOut;	
	}
	
	public String getFolderProcessing() {
		return getFolderIn + "/processing";	
	}
	
	public String getFolderInQueen() {
		return getFolderInQueen;
	}
	
	public String getFilename() {
		return filename;	
	}

	public void setFilename(String fileName) {
		this.filename = fileName;
	}
	
	public String getCampaignName() {
		return getCampaignName;
	}
	
	public void setCampaignName(String xmlPath) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File( xmlPath ));
		StringBuilder campaignId = new StringBuilder(document.getElementsByTagName("Campagne").item(0).getAttributes().getNamedItem("idenquete").getNodeValue());
		campaignId.append(document.getElementsByTagName("Campagne").item(0).getAttributes().getNamedItem("millesime").getNodeValue());
		campaignId.append(document.getElementsByTagName("Campagne").item(0).getAttributes().getNamedItem("idperiode").getNodeValue());
		this.getCampaignName = campaignId.toString();
	}
}
