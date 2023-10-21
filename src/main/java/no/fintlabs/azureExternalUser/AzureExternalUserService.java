package no.fintlabs.azureExternalUser;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.cache.FintCache;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AzureExternalUserService {
    private final FintCache<String,AzureExternalUser> azureExternalUserCache;

    public AzureExternalUserService(FintCache<String, AzureExternalUser> azureExternalUserCache) {
        this.azureExternalUserCache = azureExternalUserCache;
    }

    public List<AzureExternalUser> getAllAzureExternalUsersFromCache(){
        return azureExternalUserCache.getAllDistinct();

    }


}
