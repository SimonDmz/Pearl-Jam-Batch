package fr.insee.pearljam.batch.service.impl;

import javax.json.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    @Value("${fr.insee.pearljam.ldap.service.url.scheme:#{null}}")
    private String scheme;

    @Value("${fr.insee.pearljam.ldap.service.url.host:#{null}}")
    private String host;

    @Value("${fr.insee.pearljam.ldap.service.url.port:#{null}}")
    private String port;

    @Value("${fr.insee.pearljam.ldap.service.url.path:#{null}}")
    private String path;

    @Value("${fr.insee.pearljam.ldap.service.url.login:#{null}}")
    private String login;

    @Value("${fr.insee.pearljam.ldap.service.url.pw:#{null}}")
    private String password;

    String addUserInGroupInAppFormat = Constants.API_LDAP_ADD_APP_GROUP_USERID;
    String interviewerGroup = Constants.LDAP_APP_GROUP_INTERVIEWER;
    String appName = Constants.LDAP_APP_NAME;

    @Override
    public void addInterviewerHabilitation(String interviewerIdep) throws SynchronizationException {

        String rootUrl = String.format("%s://%s:%s/%s", scheme, host, port, path);
        String parametrizedUrl = String.format(addUserInGroupInAppFormat, appName, interviewerGroup, interviewerIdep);

        LOGGER.debug("rootUrl");
        LOGGER.debug(rootUrl);
        LOGGER.debug("parametrizedUrl");
        LOGGER.debug(parametrizedUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(login, password);

        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<JsonObject> response = restTemplate.exchange(rootUrl + "/" + parametrizedUrl,
                HttpMethod.POST,
                entity, JsonObject.class);
        JsonObject body = response.getBody();

        if (!response.hasBody() || body.get("erreur") != null)
            throw new SynchronizationException("Can't add interviewer habilitation : " + body.get("erreur").toString());

    }
}
