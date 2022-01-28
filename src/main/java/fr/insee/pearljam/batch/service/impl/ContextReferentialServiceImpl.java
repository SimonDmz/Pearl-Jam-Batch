package fr.insee.pearljam.batch.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.insee.pearljam.batch.Constants;
import fr.insee.pearljam.batch.dto.InterviewerAffectationsDto;
import fr.insee.pearljam.batch.dto.InterviewerDto;
import fr.insee.pearljam.batch.dto.InterviewersAffectationsResponseDto;
import fr.insee.pearljam.batch.dto.InterviewersResponseDto;
import fr.insee.pearljam.batch.dto.OrganizationUnitAffectationsDto;
import fr.insee.pearljam.batch.dto.OrganizationUnitDto;
import fr.insee.pearljam.batch.dto.OrganizationUnitsAffectationsResponseDto;
import fr.insee.pearljam.batch.dto.OrganizationUnitsResponseDto;
import fr.insee.pearljam.batch.dto.SimpleIdDto;
import fr.insee.pearljam.batch.exception.SynchronizationException;
import fr.insee.pearljam.batch.service.ContextReferentialService;
import fr.insee.pearljam.batch.service.KeycloakService;


// Class to call Context referential endpoints

@Service
public class ContextReferentialServiceImpl implements ContextReferentialService {
	
	@Autowired
	@Qualifier("contextReferentialBaseUrl")
	String getContextReferentialBaseUrl ;
	
	@Autowired
	KeycloakService keycloakService;
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final String NO_RESPONSE_MSG = "Could not get response from contextReferential";
		
	public List<InterviewerDto> getInterviewersFromOpale() throws SynchronizationException {
		String uri = getContextReferentialBaseUrl + Constants.API_OPALE_INTERVIEWERS;
	
	 	HttpHeaders headers = getHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		ResponseEntity<InterviewersResponseDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, InterviewersResponseDto.class);
		InterviewersResponseDto body = response.getBody();
		
		if(body==null) {
			throw new SynchronizationException(NO_RESPONSE_MSG);
		}
		return body.getEnqueteurs();
	}
	
	public List<OrganizationUnitDto> getOrganizationUnitsFromOpale() throws SynchronizationException{		
		String uri = getContextReferentialBaseUrl + Constants.API_OPALE_ORGANIZATION_UNITS;
		
	 	HttpHeaders headers = getHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		ResponseEntity<OrganizationUnitsResponseDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, OrganizationUnitsResponseDto.class);
		OrganizationUnitsResponseDto body = response.getBody();
		if(body==null) {
			throw new SynchronizationException(NO_RESPONSE_MSG);
		}
		
		return body.getOrganizationUnits();
		
	}
	
	public List<InterviewerAffectationsDto> getInterviewersAffectationsFromOpale() throws SynchronizationException {
		String uri = getContextReferentialBaseUrl + Constants.API_OPALE_INTERVIEWERS_AFFECTATIONS;
		
	 	HttpHeaders headers = getHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		ResponseEntity<InterviewersAffectationsResponseDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, InterviewersAffectationsResponseDto.class);
		InterviewersAffectationsResponseDto body = response.getBody();
		if(body==null) {
			throw new SynchronizationException(NO_RESPONSE_MSG);
		}
		
		return body.getInterviewers();
	}
	
	public List<OrganizationUnitAffectationsDto> getOrganizationUnitsAffectationsFromOpale() throws SynchronizationException{		
		String uri = getContextReferentialBaseUrl + Constants.API_OPALE_ORGANIZATION_UNITS_AFFECTATIONS;
		
	 	HttpHeaders headers = getHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		ResponseEntity<OrganizationUnitsAffectationsResponseDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, OrganizationUnitsAffectationsResponseDto.class);
		OrganizationUnitsAffectationsResponseDto body = response.getBody();
		if(body==null) {
			throw new SynchronizationException(NO_RESPONSE_MSG);
		}
		
		return body.getOrganizationUnits();
	}
	
	@Override
	public SimpleIdDto getSurveyUnitOUAffectation(String suId) throws SynchronizationException{		
		String uri = getContextReferentialBaseUrl + Constants.API_OPALE_SURVEY_UNIT_OU_AFFECTATION;
		
	 	HttpHeaders headers = getHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		ResponseEntity<SimpleIdDto> response = restTemplate.exchange(String.format(uri, suId), HttpMethod.GET, entity, SimpleIdDto.class);
		SimpleIdDto body = response.getBody();
		if(body==null) {
			throw new SynchronizationException(NO_RESPONSE_MSG);
		}
		
		return body;
	}
	
	@Override
	public InterviewerDto getSurveyUnitInterviewerAffectation(String suId) throws SynchronizationException{		
		String uri = getContextReferentialBaseUrl + Constants.API_OPALE_SURVEY_UNIT_INTERVIEWER_AFFECTATION;
		
	 	HttpHeaders headers = getHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		ResponseEntity<InterviewerDto> response = restTemplate.exchange(String.format(uri, suId), HttpMethod.GET, entity, InterviewerDto.class);
		InterviewerDto body = response.getBody();
		if(body==null) {
			throw new SynchronizationException(NO_RESPONSE_MSG);
		}
		
		return body;
		
	}
	
	@Override
	public void contextReferentialServiceIsAvailable() throws SynchronizationException {
		
		String uri = getContextReferentialBaseUrl + Constants.API_OPALE_HEALTHCHECK;
		
	 	HttpHeaders headers = getHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		HttpStatus returnedCode = response.getStatusCode();
		if(!returnedCode.is2xxSuccessful()) {
			throw new SynchronizationException(NO_RESPONSE_MSG);
		}
		
	}

	private HttpHeaders getHeaders() throws SynchronizationException {
		String token = keycloakService.getContextReferentialToken();
		List<MediaType> accepted = new ArrayList<>();
		accepted.add(MediaType.APPLICATION_JSON);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(accepted);
		
		headers.setBearerAuth(token);
		
		return headers;
	}
}
