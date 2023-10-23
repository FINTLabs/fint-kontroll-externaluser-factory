package no.fintlabs.azureExternalUser;

import no.fintlabs.externalUser.ExternalUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AzureExternalUserTest {

    @Test
    public void externalUserTransformedFromAzureExternalUser(){
        AzureExternalUser azureExternalUser = AzureExternalUser.builder()
                .firstName("Titten")
                .lastName("Tei")
                .userName("titten@tei.no")
                .idpUserObjectId("4c965774-717c-11ee-b962-0242ac120002")
                .userPrincipalName("titten@tei.no")
                .build();

        ExternalUser externalUser = azureExternalUser.toExternalUser();

        assertEquals(azureExternalUser.getIdpUserObjectId(), String.valueOf(externalUser.getIdentityProviderUserObjectId()) );
    }
}