package fr.insee.pearljam.batch.dto;

import java.util.List;

public class InterviewerAffectationsDto {
	private String idep;
	private List<SimpleIdDto> surveyUnits;

	public InterviewerAffectationsDto() {
		super();
	}
	
	public String getIdep() {
		return idep;
	}

	public void setIdep(String idep) {
		this.idep = idep;
	}

	public List<SimpleIdDto> getSurveyUnits() {
		return surveyUnits;
	}

	public void setSurveyUnits(List<SimpleIdDto> surveyUnits) {
		this.surveyUnits = surveyUnits;
	}

}
