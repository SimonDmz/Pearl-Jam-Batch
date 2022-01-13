package fr.insee.pearljam.batch.service.impl;

import javax.json.JsonObject;

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

    private static final String NO_RESPONSE_MSG = "Could not get response from habilitation API";

    @Override
    public void addInterviewerHabilitation(String interviewerIdep) throws SynchronizationException {

        String parametrizedUrl = String.format(addUserInGroupInAppFormat, appName, interviewerGroup,
                interviewerIdep);

        LOGGER.warn("habilitationApiRootUrl");
        LOGGER.warn(habilitationApiRootUrl);
        LOGGER.warn("parametrizedUrl");
        LOGGER.warn(parametrizedUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(ldapServiceLogin, ldapServicePassword);

        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<JsonObject> response = restTemplate.exchange(habilitationApiRootUrl + "/" + parametrizedUrl,
                HttpMethod.POST,
                entity, JsonObject.class);
        JsonObject body = response.getBody();

        if (!response.hasBody() || body.get("erreur") != null)
            throw new SynchronizationException("Can't add interviewer habilitation : " + body.get("erreur").toString());

    }

    @Override
    public void isAvailable() throws SynchronizationException {

        String uri = String.join("/", habilitationApiRootUrl, Constants.API_LDAP_HEALTHCHECK);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(ldapServiceLogin, ldapServicePassword);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        HttpStatus returnedCode = response.getStatusCode();
        if (!returnedCode.is2xxSuccessful()) {
            throw new SynchronizationException(NO_RESPONSE_MSG);
        }

    }
}
