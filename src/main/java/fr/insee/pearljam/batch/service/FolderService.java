package fr.insee.pearljam.batch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FolderService {
	@Autowired
	String getFolderIn;
	
	@Autowired
	String getFolderOut;
	
	@Autowired
	@Qualifier("filename")
	String filename;
	
	public String getFolderIn() {
		return getFolderIn;
	}
	
	public String getFolderOut() {
		return getFolderOut;	
	}
	
	public String getFolderProcessing() {
		return getFolderIn + "/processing";	
	}
	
	public String getFilename() {
		return filename;	
	}


	public void setFilename(String fileName) {
		this.filename = fileName;
		
	}
}
