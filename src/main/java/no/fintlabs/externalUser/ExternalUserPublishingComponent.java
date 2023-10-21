package no.fintlabs.externalUser;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.azureExternalUser.AzureExternalUser;
import no.fintlabs.azureExternalUser.AzureExternalUserService;
import no.fintlabs.cache.FintCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ExternalUserPublishingComponent {
    private final ExternalUserProducerService externalUserProducerService;
    private final AzureExternalUserService azureExternalUserService;

    private final FintCache<String, Integer> publishedExternalUserHashCache;

    public ExternalUserPublishingComponent(ExternalUserProducerService externalUserProducerService, AzureExternalUserService azureExternalUserService, FintCache<String, Integer> publishedExternalUserHashCache) {
        this.externalUserProducerService = externalUserProducerService;
        this.azureExternalUserService = azureExternalUserService;
        this.publishedExternalUserHashCache = publishedExternalUserHashCache;
    }


    @Scheduled(
            initialDelayString = "10000",
            fixedDelayString = "50000"
    )
    public void prepareForPublishingExternalUsers() {
        List<AzureExternalUser> azureExternalUsers = azureExternalUserService.getAllAzureExternalUsersFromCache();

        log.info("{} external users from Azure in list", azureExternalUsers.size());

        List<ExternalUser> publishExternalUsers = azureExternalUsers.stream()
                .map(AzureExternalUser::toExternalUser)
                .filter(this::isExternalUserChanged)
                .peek(externalUserProducerService::publishExternalUser)
                .toList();

        log.info("Published {} external users to kafka", publishExternalUsers.size());

    }
    private boolean isExternalUserChanged(ExternalUser externalUser) {
        return publishedExternalUserHashCache
                .getOptional(externalUser.getIdentityProviderUserObjectId().toString())
                .map(publishedExternalUserHash -> publishedExternalUserHash != externalUser.hashCode())
                .orElse(true);
    }
}
