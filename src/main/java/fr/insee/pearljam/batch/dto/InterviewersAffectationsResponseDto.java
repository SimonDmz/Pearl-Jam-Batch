package fr.insee.pearljam.batch.dto;

import java.util.ArrayList;
import java.util.List;

public class InterviewersAffectationsResponseDto {
	List<InterviewerAffectationsDto> interviewers;

	public InterviewersAffectationsResponseDto() {
		super();
		interviewers = new ArrayList<>();
	}
	
	public List<InterviewerAffectationsDto> getInterviewers() {
		return interviewers;
	}

	public void setInterviewers(List<InterviewerAffectationsDto> interviewers) {
		this.interviewers = interviewers;
	}

}
