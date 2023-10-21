package no.fintlabs;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.azureExternalUser.AzureExternalUser;
import no.fintlabs.cache.FintCache;
import no.fintlabs.externalUser.ExternalUser;
import no.fintlabs.kafka.common.ListenerContainerFactory;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.kafka.entity.topic.EntityTopicNamePatternParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
@Slf4j
public class EntityConsumersConfiguration {

    private final EntityConsumerFactoryService entityConsumerFactoryService;

    public EntityConsumersConfiguration(EntityConsumerFactoryService entityConsumerFactoryService) {
        this.entityConsumerFactoryService = entityConsumerFactoryService;
    }



    @Bean
    ConcurrentMessageListenerContainer<String, AzureExternalUser> externalAzureUserResourceEntityConsumer(
            FintCache<String,AzureExternalUser> azureExternalUserCache
    ){
        ListenerContainerFactory<AzureExternalUser,EntityTopicNameParameters,EntityTopicNamePatternParameters> externalUserConsumerFactory = entityConsumerFactoryService.createFactory(
                AzureExternalUser.class,
                consumerRecord -> {
                    AzureExternalUser externalAzureUser = consumerRecord.value();
                    log.debug("Trying to save: " + externalAzureUser.getUserName());
                    if (externalAzureUser.isValid()) {
                        azureExternalUserCache.put(externalAzureUser.getIdpUserObjectId().toString(),externalAzureUser);
                        log.debug("Saved to cache: " + externalAzureUser.getIdpUserObjectId());
                    }
                    else {
                        log.debug("Failed to save: " + externalAzureUser.getIdpUserObjectId());
                    }
                }
        );
        if (externalUserConsumerFactory != null){
            return externalUserConsumerFactory.createContainer(EntityTopicNameParameters.builder().resource("azureuserexternal").build());
        }
        else { return null;}

    }


    @Bean
    ConcurrentMessageListenerContainer<String, ExternalUser> userEntityConsumer(
            FintCache<String, Integer> publishedUserHashCache
    ) {
        return entityConsumerFactoryService.createFactory(
                ExternalUser.class,
                consumerRecord -> publishedUserHashCache.put(
                        consumerRecord.value().getIdentityProviderUserObjectId().toString(),
                        consumerRecord.value().hashCode()
                )
        ).createContainer(EntityTopicNameParameters.builder().resource("externaluser").build());
    }

}
