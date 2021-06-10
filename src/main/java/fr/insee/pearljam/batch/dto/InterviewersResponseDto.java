package fr.insee.pearljam.batch.dto;

import java.util.ArrayList;
import java.util.List;

public class InterviewersResponseDto {
	List<InterviewerDto> enqueteurs;

	public InterviewersResponseDto() {
		super();
		enqueteurs = new ArrayList<>();
	}

	public List<InterviewerDto> getEnqueteurs() {
		return enqueteurs;
	}

	public void setEnqueteurs(List<InterviewerDto> enqueteurs) {
		this.enqueteurs = enqueteurs;
	}
}
