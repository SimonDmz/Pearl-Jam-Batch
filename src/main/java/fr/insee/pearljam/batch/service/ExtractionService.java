package fr.insee.pearljam.batch.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.insee.pearljam.batch.utils.BatchErrorCode;
import fr.insee.pearljam.batch.utils.XmlUtils;

@Service
public class ExtractionService {
	
	private static final Logger logger = LogManager.getLogger(ExtractionService.class);
	
	@Autowired
	FolderService folderService;
	
	public BatchErrorCode extractSampleProcessing(String in, String out, String step) {
		BatchErrorCode returnCode = BatchErrorCode.OK;
		NodeList campaignNodes = XmlUtils.getXmlNodeFile(in, "Campagne");
		Document doc = new Document();
		if(campaignNodes.getLength() != 0) {
			for(int i= 0; i < campaignNodes.getLength(); i++) {
				Node node = campaignNodes.item(i);
				Element campaignElement = new Element("Campaign");
				extractCampaignId(node, campaignElement);
				Element surveyUnitsElement = new Element("SurveyUnits");
				NodeList surveyUnitNodes = XmlUtils.getXmlNodeFile(in, "Questionnaire");
				if(surveyUnitNodes.getLength() != 0) {
					extractSurveyUnits(surveyUnitsElement, surveyUnitNodes, step);
					campaignElement.addContent(surveyUnitsElement);
				} else {
					logger.log(Level.INFO, "No survey-units found in file");
					return BatchErrorCode.KO_FONCTIONAL_ERROR;
				}
				if(step.equals("data-collection")) {
					createFileToExtract(doc, campaignElement, 
							folderService.getFolderInQueen + "/sample.xml");
				} else {
					createFileToExtract(doc, campaignElement, 
							folderService.getFolderIn + "/Campaign/campaign.xml");
				}
			}
		} else {
			logger.log(Level.INFO, "No campaigns found in file");
			return BatchErrorCode.KO_FONCTIONAL_ERROR;
		}		
		return returnCode;
	}

	private void createFileToExtract(Document doc, Element campaignElement, String fileName) {
		File file = new File(fileName);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			doc.setRootElement(campaignElement);
			XMLOutputter outter = new XMLOutputter();
			outter.setFormat(Format.getPrettyFormat());
			outter.output(doc, fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void extractSurveyUnits(Element questionnaires, NodeList questionnairesNodes, String step) {
		for(int j= 0; j < questionnairesNodes.getLength(); j++) {
			Element surveyUnitElement = new Element("SurveyUnit");
			Node surveyUnitNode = questionnairesNodes.item(j);
			if(surveyUnitNode.getNodeType() == Node.ELEMENT_NODE) {
				for(int k=0; k<surveyUnitNode.getChildNodes().getLength(); k++) {
					extractOneHierarchicalElement(surveyUnitElement, "Id", surveyUnitNode, k);
					if(step.equals("data-collection")) {
						extractOneHierarchicalElement(surveyUnitElement, "QuestionnaierModelId", surveyUnitNode, k);
						extractTwoHierarchicalElement(surveyUnitElement, "Data", surveyUnitNode, k);
						extractPersonalization(surveyUnitElement, surveyUnitNode, k);
					}
					if(step.equals("pilotage")) {
						extractOneHierarchicalElement(surveyUnitElement, "Priority", surveyUnitNode, k);
						extractPersons(surveyUnitElement, surveyUnitNode, k);
						extractTwoHierarchicalElement(surveyUnitElement, "InseeAddress", surveyUnitNode, k);
						extractTwoHierarchicalElement(surveyUnitElement, "InseeSampleIdentiers", surveyUnitNode, k);
					}
				}
			}
			questionnaires.addContent(surveyUnitElement);
		}
	}
	
	private void extractOneHierarchicalElement(Element questionnaire, String elementName, Node nodeQuestionnaire, int k) {
		if(nodeQuestionnaire.getChildNodes().item(k).getNodeName().equals(elementName)) {
			questionnaire.addContent(new Element(elementName)
					.addContent(nodeQuestionnaire.getChildNodes().item(k).getFirstChild().getNodeValue()));
		}
	}
	
	private void extractTwoHierarchicalElement(Element questionnaire, String elementName, Node nodeQuestionnaire, int k) {
		if(nodeQuestionnaire.getChildNodes().item(k).getNodeName().equals(elementName)) {
			Node inseeSampleIdentifiersNode = nodeQuestionnaire.getChildNodes().item(k);
			Element inseeSampleIdentifiersElement = new Element(elementName);
			for(int l=0; l<inseeSampleIdentifiersNode.getChildNodes().getLength(); l++) {
				if(inseeSampleIdentifiersNode.getChildNodes().item(l).getNodeType() == Node.ELEMENT_NODE) {
					Element adresseTempElement = new Element(inseeSampleIdentifiersNode.getChildNodes().item(l).getNodeName());
					if(inseeSampleIdentifiersNode.getChildNodes().item(l).getFirstChild() != null &&
							!StringUtils.isBlank(inseeSampleIdentifiersNode.getChildNodes().item(l).getFirstChild().getNodeValue()))
						adresseTempElement.addContent(inseeSampleIdentifiersNode.getChildNodes().item(l).getFirstChild().getNodeValue());
					inseeSampleIdentifiersElement.addContent(adresseTempElement);
				}
			}
			questionnaire.addContent(inseeSampleIdentifiersElement);
		}
	}

	private void extractCampaignId(Node node, Element campaign) {
		StringBuilder idCampaign = new StringBuilder();
		if(node.getAttributes().getNamedItem("idenquete").getNodeValue() != null) 
			idCampaign.append(node.getAttributes().getNamedItem("idenquete").getNodeValue());
		
		if(node.getAttributes().getNamedItem("millesime").getNodeValue() != null) 
			idCampaign.append(node.getAttributes().getNamedItem("millesime").getNodeValue());
		
		if(node.getAttributes().getNamedItem("idperiode").getNodeValue() != null) 
			idCampaign.append(node.getAttributes().getNamedItem("idperiode").getNodeValue());
		
		if(!idCampaign.toString().isBlank())
			campaign.addContent(new Element("Id").addContent(idCampaign.toString()));
		
		if(node.getAttributes().getNamedItem("label").getNodeValue() != null) 
			campaign.addContent(new Element("Label").addContent(node.getAttributes().getNamedItem("label").getNodeValue()));
	}

	private void extractPersonalization(Element questionnaire, Node nodeQuestionnaire, int k) {
		if(nodeQuestionnaire.getChildNodes().item(k).getNodeName().equals("Personalization")) {
			Node personalizationNode = nodeQuestionnaire.getChildNodes().item(k);
			Element personalizationElement = new Element("Personalization");
			for(int l=0; l<personalizationNode.getChildNodes().getLength(); l++) {
				if(personalizationNode.getChildNodes().item(l).getNodeType() == Node.ELEMENT_NODE) {
					Element variableElement = new Element("Variable");
					Node variableNode = personalizationNode.getChildNodes().item(l);
					for(int m= 0; m<variableNode.getChildNodes().getLength(); m++) {
						if(variableNode.getChildNodes().item(m).getNodeType() == Node.ELEMENT_NODE) {
							variableElement.addContent(new Element(variableNode.getChildNodes().item(m).getNodeName())
									.addContent(variableNode.getChildNodes().item(m).getFirstChild().getNodeValue()));
						}
					}
					personalizationElement.addContent(variableElement);
				}
			}
			questionnaire.addContent(personalizationElement);
		}
	}
	
	private void extractPersons(Element questionnaire, Node nodeQuestionnaire, int k) {
		if(nodeQuestionnaire.getChildNodes().item(k).getNodeName().equals("Persons")) {
			Node personsNode = nodeQuestionnaire.getChildNodes().item(k);
			Element personsElement = new Element("Persons");
			for(int l=0; l<personsNode.getChildNodes().getLength(); l++) {
				if(personsNode.getChildNodes().item(l).getNodeType() == Node.ELEMENT_NODE) {
					Element personElement = new Element("Person");
					Node personNode = personsNode.getChildNodes().item(l);
					for(int m= 0; m<personNode.getChildNodes().getLength(); m++) {
						if(personNode.getChildNodes().item(m).getNodeType() == Node.ELEMENT_NODE && 
								!personNode.getChildNodes().item(m).getNodeName().equals("PhoneNumbers")) {
							personElement.addContent(new Element(personNode.getChildNodes().item(m).getNodeName())
									.addContent(personNode.getChildNodes().item(m).getFirstChild().getNodeValue()));
						}
						if(personNode.getChildNodes().item(m).getNodeType() == Node.ELEMENT_NODE && 
								personNode.getChildNodes().item(m).getNodeName().equals("PhoneNumbers")) {
							Element phoneNumbersElement = new Element("PhoneNumbers");
							Node nodePhoneNumbers = personNode.getChildNodes().item(m);
							extractPhoneNumbers(phoneNumbersElement, nodePhoneNumbers, m);
							personElement.addContent(phoneNumbersElement);
						}
					}
					personsElement.addContent(personElement);
				}
			}
			questionnaire.addContent(personsElement);
		}
	}
	
	private void extractPhoneNumbers(Element phoneNumbersElement, Node nodePhoneNumbers, int m) {
		for(int l=0; l<nodePhoneNumbers.getChildNodes().getLength(); l++) {
			if(nodePhoneNumbers.getChildNodes().item(l).getNodeType() == Node.ELEMENT_NODE) {
				Element phoneNumberElement = new Element("PhoneNumber");
				Node phoneNumberNode = nodePhoneNumbers.getChildNodes().item(l);
				for(int n= 0; n<phoneNumberNode.getChildNodes().getLength(); n++) {
					if(phoneNumberNode.getChildNodes().item(n).getNodeType() == Node.ELEMENT_NODE) {
						phoneNumberElement.addContent(new Element(phoneNumberNode.getChildNodes().item(n).getNodeName())
								.addContent(phoneNumberNode.getChildNodes().item(n).getFirstChild().getNodeValue()));
					}
				}
				phoneNumbersElement.addContent(phoneNumberElement);
			}
		}
	}
}
