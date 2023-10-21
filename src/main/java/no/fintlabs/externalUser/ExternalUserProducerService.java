package no.fintlabs.externalUser;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.kafka.entity.EntityProducer;
import no.fintlabs.kafka.entity.EntityProducerFactory;
import no.fintlabs.kafka.entity.EntityProducerRecord;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.kafka.entity.topic.EntityTopicService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExternalUserProducerService {
    private final EntityProducer<ExternalUser> entityProducer;
    private final EntityTopicNameParameters entityTopicNameParameters;

    public ExternalUserProducerService(
            EntityTopicService entityTopicService,
            EntityProducerFactory entityProducerFactory) {

        entityProducer = entityProducerFactory.createProducer(ExternalUser.class);
        this.entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("externaluser")
                .build();
        entityTopicService.ensureTopic(entityTopicNameParameters,0);
    }

    public void publishExternalUser(ExternalUser externalUser){
        String key = String.valueOf(externalUser.getIdentityProviderUserObjectId());
        entityProducer.send(
                EntityProducerRecord.<ExternalUser>builder()
                        .topicNameParameters(entityTopicNameParameters)
                        .key(key)
                        .value(externalUser)
                        .build()
        );
    }
}
