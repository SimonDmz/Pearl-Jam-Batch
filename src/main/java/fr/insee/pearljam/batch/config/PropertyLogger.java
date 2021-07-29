package fr.insee.pearljam.batch.config;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

@Component
public class PropertyLogger  {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyLogger.class);

    private static boolean alreadyDisplayed=false;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final Environment env = event.getApplicationContext().getEnvironment();
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        if (!alreadyDisplayed) {
            if ((new File("pom.xml")).exists()) {
                try {
                    model = reader.read(new FileReader("pom.xml"));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    model = reader.read(
                            new InputStreamReader(
                                    PropertyLogger.class.getResourceAsStream(
                                            "/META-INF/maven/fr.insee/pearljam-batch/pom.xml"
                                    )
                            )
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            List<Dependency> dependencyList = model.getDependencies();
            String queenBatchVersion = null;
            String artifactId = "queen-batch";
            String groupId = "fr.insee.queen";
            for (Dependency d : dependencyList) {
                if (d.getArtifactId().equals(artifactId) && d.getGroupId().equals(groupId)
                ) {
                    queenBatchVersion = d.getVersion();
                    break;
                }
            }

            LOGGER.info("================================ PearlJam-Batch Version:" + model.getVersion() + " ================================");
            LOGGER.info("================================ Using Queen-Batch Version:" + queenBatchVersion + " ================================");

            LOGGER.info("================================ Properties ================================");
            final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
            StreamSupport.stream(sources.spliterator(), false)
                    .filter(ps -> ps instanceof EnumerablePropertySource)
                    .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                    .flatMap(Arrays::stream)
                    .distinct()
                    .filter(prop -> !(prop.contains("credentials") || prop.contains("password")
                            || prop.contains("pw") || prop.contains("Password")))
                    .filter(prop -> prop.startsWith("fr.insee") || prop.startsWith("logging") || prop.startsWith("keycloak") || prop.startsWith("spring"))
                    .sorted()
                    .forEach(prop -> LOGGER.info("{}: {}", prop, env.getProperty(prop)));
            LOGGER.info("===========================================================================");
        }
        alreadyDisplayed=true;
    }
}