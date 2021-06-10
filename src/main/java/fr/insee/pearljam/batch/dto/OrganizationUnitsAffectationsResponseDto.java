package fr.insee.pearljam.batch.dto;

import java.util.ArrayList;
import java.util.List;

public class OrganizationUnitsAffectationsResponseDto {
	List<OrganizationUnitAffectationsDto> organizationUnits;

	public OrganizationUnitsAffectationsResponseDto() {
		super();
		organizationUnits = new ArrayList<>();
	}
	
	public List<OrganizationUnitAffectationsDto> getOrganizationUnits() {
		return organizationUnits;
	}

	public void setOrganizationUnits(List<OrganizationUnitAffectationsDto> organizationUnits) {
		this.organizationUnits = organizationUnits;
	}

}
