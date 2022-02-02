package fr.insee.pearljam.batch.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.insee.pearljam.batch.Constants;
import fr.insee.pearljam.batch.dto.HabilitatedUsers;
import fr.insee.pearljam.batch.dto.HabilitationActionResponseDto;
import fr.insee.pearljam.batch.exception.SynchronizationException;
import fr.insee.pearljam.batch.service.HabilitationService;

@Service
public class HabilitationServiceImpl implements HabilitationService {
    private static final Logger LOGGER = LogManager.getLogger(HabilitationServiceImpl.class);
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    @Qualifier("habilitationApiBaseUrl")
    private String habilitationApiRootUrl;

    @Value("${fr.insee.pearljam.ldap.service.app.name:#{null}}")
    private String appName;

    @Value("${fr.insee.pearljam.ldap.service.group.interviewer:#{null}}")
    private String interviewerGroup;

    @Value("${fr.insee.pearljam.ldap.service.login:#{null}}")
    private String ldapServiceLogin;

    @Value("${fr.insee.pearljam.ldap.service.pw:#{null}}")
    private String ldapServicePassword;

    String addUserInGroupInAppFormat = Constants.API_LDAP_ADD_APP_GROUP_USERID;
    String getUsersInGroupInAppFormat = Constants.API_LDAP_GET_APP_GROUP_USERS;

    private static final String NO_RESPONSE_MSG = "Could not get response from habilitation API";

    private HttpHeaders getHabilitationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(ldapServiceLogin, ldapServicePassword);
        return headers;
    }

    @Override
    public void addInterviewerHabilitation(String interviewerIdep) throws SynchronizationException {

        String parametrizedUrl = String.format(addUserInGroupInAppFormat, appName, interviewerGroup,
                interviewerIdep);

        HttpHeaders headers = getHabilitationHeaders();

        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<HabilitationActionResponseDto> response = restTemplate.exchange(
                habilitationApiRootUrl +  parametrizedUrl,
                HttpMethod.POST,
                entity, HabilitationActionResponseDto.class);
        LOGGER.info("Calling {}", parametrizedUrl);
        LOGGER.info("Response {}", response.getStatusCode().toString());

        HabilitationActionResponseDto body = response.getBody();

        if (!response.hasBody() || body.getErreur() != null)
            throw new SynchronizationException("Can't add interviewer habilitation : " + body.getErreur());

    }

    @Override
    public void isAvailable() throws SynchronizationException {

        String uri = String.join("", habilitationApiRootUrl, Constants.API_LDAP_HEALTHCHECK);

        HttpHeaders headers = getHabilitationHeaders();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        HttpStatus returnedCode = response.getStatusCode();
        LOGGER.info("Calling {}", uri);
        LOGGER.info("Response {}", response.getStatusCode().toString());

        if (!returnedCode.is2xxSuccessful()) {
            throw new SynchronizationException(NO_RESPONSE_MSG);
        }

    }

    @Override
    public List<String> getHabilitatedInterviewers() throws SynchronizationException {
        String parametrizedUrl = String.format(getUsersInGroupInAppFormat, appName, interviewerGroup);

        HttpHeaders headers = getHabilitationHeaders();

        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<HabilitatedUsers[]> response = restTemplate.exchange(
                habilitationApiRootUrl + "/" + parametrizedUrl,
                HttpMethod.GET,
                entity, HabilitatedUsers[].class);

        LOGGER.info("Calling {}", parametrizedUrl);
        LOGGER.info("Response {}", response.getStatusCode().toString());
        if (!response.hasBody())
            throw new SynchronizationException("Can't get habilitated interviewers.");

        HabilitatedUsers habilitatedUsers = response.getBody()[0];
        return habilitatedUsers.getPersonnes().stream().map(personne -> personne.getUid()).collect(Collectors.toList());
    }
}
