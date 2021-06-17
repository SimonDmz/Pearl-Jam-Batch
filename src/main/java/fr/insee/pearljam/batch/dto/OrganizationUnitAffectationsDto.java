package fr.insee.pearljam.batch.dto;

import java.util.ArrayList;
import java.util.List;

public class OrganizationUnitAffectationsDto {
	private String id;
	private List<SimpleIdDto> surveyUnits;

	public OrganizationUnitAffectationsDto() {
		super();
		surveyUnits = new ArrayList<>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<SimpleIdDto> getSurveyUnits() {
		return surveyUnits;
	}

	public void setSurveyUnits(List<SimpleIdDto> surveyUnits) {
		this.surveyUnits = surveyUnits;
	}

}
