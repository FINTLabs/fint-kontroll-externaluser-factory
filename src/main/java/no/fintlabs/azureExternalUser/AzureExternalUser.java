package no.fintlabs.azureExternalUser;

import lombok.*;
import no.fintlabs.externalUser.ExternalUser;
import no.fintlabs.externalUser.Utils;


import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class AzureExternalUser {
    String firstName;
    String lastName;
    String mobilePhone;
    String email;
    String mainOrganisationUnitName;
    String mainOrganisationUnitId;
    String userName;
    String idpUserObjectId;
    String userPrincipalName;

    public boolean isValid() {
        return this.idpUserObjectId != null;
    }

    public ExternalUser toExternalUser(){
        return
                ExternalUser.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .userType(String.valueOf(Utils.UserType.EXTERNAL))
                        .userName(userName)
                        .identityProviderUserObjectId(UUID.fromString(idpUserObjectId))
                        .mainOrganisationUnitId(mainOrganisationUnitId)
                        .mainOrganisationUnitName(mainOrganisationUnitName)
                        .mobilePhone(mobilePhone)
                        .email(email)
                        .build();
    }



}
