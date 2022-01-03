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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import fr.insee.pearljam.batch.config.ApplicationContext;

@Service
public class HabilitationServiceImpl implements HabilitationService {
    private static final Logger LOGGER = LogManager.getLogger(HabilitationServiceImpl.class);
    @Autowired
    RestTemplate restTemplate;

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationContext.class);

    private String habilitationApiRootUrl = (String) context.getBean("habilitationApiBaseUrl");

    @Value("${fr.insee.pearljam.ldap.service.url.port:#{null}}")
    private String appName;
    
    @Value("${fr.insee.pearljam.ldap.service.group.interviewer:#{null}}")
    private String interviewerGroup;

    @Value("${fr.insee.pearljam.ldap.service.login:#{null}}")
    private String ldapServiceLogin;

    @Value("${fr.insee.pearljam.ldap.service.pw:#{null}}")
    private String ldapServicePassword;

    String addUserInGroupInAppFormat = Constants.API_LDAP_ADD_APP_GROUP_USERID;

    @Override
    public void addInterviewerHabilitation(String interviewerIdep) throws SynchronizationException {

        String parametrizedUrl = String.format(addUserInGroupInAppFormat, appName, interviewerGroup, interviewerIdep);

        LOGGER.debug("habilitationApiRootUrl");
        LOGGER.debug(habilitationApiRootUrl);
        LOGGER.debug("parametrizedUrl");
        LOGGER.debug(parametrizedUrl);

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
}
