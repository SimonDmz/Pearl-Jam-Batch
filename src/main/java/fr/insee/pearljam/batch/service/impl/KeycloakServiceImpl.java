package fr.insee.pearljam.batch.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import fr.insee.pearljam.batch.dto.KeycloakResponseDto;
import fr.insee.pearljam.batch.exception.SynchronizationException;
import fr.insee.pearljam.batch.service.KeycloakService;

@Service
public class KeycloakServiceImpl implements KeycloakService{
	
	@Value("${keycloak.auth-server-url:#{null}}")
	private String authServerURL;
	
	@Value("${keycloak.realm:#{null}}")
	private String realm;
	
	@Value("${keycloak.client.id:#{null}}")
	private String clientId;
	
	@Value("${keycloak.client.secret:#{null}}")
	private String clientSecret;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Override
	public String getContextReferentialToken() throws SynchronizationException {
		String uri = authServerURL + "/realms/" + realm + "/protocol/openid-connect/token";
	
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "client_credentials");
		map.add("client_secret", clientSecret);
		map.add("client_id", clientId);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

		ResponseEntity<KeycloakResponseDto> response =
		    restTemplate.exchange(uri,
		                          HttpMethod.POST,
		                          entity,
		                          KeycloakResponseDto.class);
		KeycloakResponseDto body = response.getBody();
		if(body == null) {
			throw new SynchronizationException("Could not retreive access token from keycloak");
		}
		
		return body.getAccess_token();
	}

}
