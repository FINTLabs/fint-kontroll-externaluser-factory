package no.fintlabs.externalUser;

import no.fintlabs.cache.FintCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExternalUserPublishingComponentTest {

    private ExternalUserPublishingComponent externalUserPublishingComponent;
    private ExternalUserProducerService externalUserProducerService;
    private FintCache<String,Integer> publishedExternalUserHashCache;

    @BeforeEach
    public void init(){
        publishedExternalUserHashCache = mock(FintCache.class);
        externalUserProducerService = mock(ExternalUserProducerService.class);
        externalUserPublishingComponent = new ExternalUserPublishingComponent(
                externalUserProducerService,
                null,
                publishedExternalUserHashCache);
    }
    @Test
    public void isExternalUserChangedShouldReturnTrueIfDifferent(){
        ExternalUser externalUserFromKafka = ExternalUser.builder()
                .firstName("Titten")
                .lastName("Tei")
                .identityProviderUserObjectId(UUID.fromString("4c965774-717c-11ee-b962-0242ac120002"))
                .userName("tutten@tei.no")
                .build();

        ExternalUser externalUserFromCache = ExternalUser.builder()
                .firstName("Titten")
                .lastName("Tei")
                .identityProviderUserObjectId(UUID.fromString("4c965774-717c-11ee-b962-0242ac120002"))
                .userName("titten@tei.no")
                .build();

        when(publishedExternalUserHashCache.getOptional(externalUserFromKafka.getIdentityProviderUserObjectId().toString()))
                .thenReturn(Optional.of(externalUserFromCache.hashCode()));

        boolean changed = externalUserPublishingComponent.isExternalUserChanged(externalUserFromKafka);
        assertTrue(changed,"External user should be different from cache");

    }

    @Test
    public void isExternalUserChangedShouldReturnFalseIfSimilar(){
        ExternalUser externalUserFromKafka = ExternalUser.builder()
                .firstName("Titten")
                .lastName("Tei")
                .identityProviderUserObjectId(UUID.fromString("4c965774-717c-11ee-b962-0242ac120002"))
                .userName("titten@tei.no")
                .build();

        ExternalUser externalUserFromCache = ExternalUser.builder()
                .firstName("Titten")
                .lastName("Tei")
                .identityProviderUserObjectId(UUID.fromString("4c965774-717c-11ee-b962-0242ac120002"))
                .userName("titten@tei.no")
                .build();

        when(publishedExternalUserHashCache.getOptional(externalUserFromKafka.getIdentityProviderUserObjectId().toString()))
                .thenReturn(Optional.of(externalUserFromCache.hashCode()));

        boolean changed = externalUserPublishingComponent.isExternalUserChanged(externalUserFromKafka);
        assertFalse(changed,"External user should be similar from cache");

    }



}