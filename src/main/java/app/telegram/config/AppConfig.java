package app.telegram.config;

import api.deezer.DeezerApi;
import api.deezer.objects.Permission;
import app.telegram.properties.AppProperties;
import it.tdlight.client.APIToken;
import it.tdlight.client.AuthenticationData;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.client.TDLibSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

@Configuration
@PropertySource("app.properties")
public class AppConfig {
    @Value("${telegram.api_id}")
    private int appId;

    @Value("${telegram.api_hash}")
    private String apiHash;

    @Bean
    public DeezerApi deezerApi() {
        return new DeezerApi();
    }

    @Bean
    public Permission permission() {
        return Permission.LISTENING_HISTORY;
    }
    @Bean
    public SimpleTelegramClient simpleTelegramClient() {
        return new SimpleTelegramClient(tdLibSettings());
    }

    @Bean
    public TDLibSettings tdLibSettings() {
        return TDLibSettings.create(apiToken());
    }

    @Bean
    public APIToken apiToken() {
        return new APIToken(appId, apiHash);
    }

    @Bean
    public AuthenticationData authenticationData() {
        return AuthenticationData.consoleLogin();
    }

    @Bean
    public Properties appProperties() {
        return new AppProperties();
    }
}
