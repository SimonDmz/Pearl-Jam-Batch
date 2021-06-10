package fr.insee.pearljam.batch.dto;

import java.util.ArrayList;
import java.util.List;

public class OrganizationUnitsResponseDto {
	List<OrganizationUnitDto> organizationUnits;

	public OrganizationUnitsResponseDto() {
		super();
		organizationUnits = new ArrayList<>();
	}

	public List<OrganizationUnitDto> getOrganizationUnits() {
		return organizationUnits;
	}

	public void setOrganizationUnits(List<OrganizationUnitDto> organizationUnits) {
		this.organizationUnits = organizationUnits;
	}

}
