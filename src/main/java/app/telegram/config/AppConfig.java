package app.telegram.config;

import api.deezer.DeezerApi;
import app.telegram.client.SyncTelegramClient;
import app.telegram.commands.Command;
import app.telegram.commands.LoginDeezer;
import app.telegram.commands.LoginTelegram;
import app.telegram.factories.TelegramClientFactory;
import app.telegram.properties.AppProperties;
import it.tdlight.client.APIToken;
import it.tdlight.client.TDLibSettings;
import it.tdlight.common.TelegramClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class AppConfig {
    @Autowired
    @Qualifier("appProperties")
    private Properties appProperties;

    @Bean
    public Properties appProperties() {
        return new AppProperties();
    }

    @Bean
    public Map<String, Command> commandMap() {
        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put("deezer.login", loginDeezer());
        commandMap.put("telegram.login", loginTelegram());
        return commandMap;
    }

    @Bean
    public Command loginDeezer() {
        return new LoginDeezer();
    }

    @Bean
    public DeezerApi deezerApi() {
        return new DeezerApi();
    }

    @Bean
    public Command loginTelegram() {
        return new LoginTelegram();
    }

    @Bean
    public SyncTelegramClient syncTelegramClient() {
        return new SyncTelegramClient();
    }

    @Bean
    public TelegramClient telegramClient() throws Exception {
        return telegramClientFactory().getObject();
    }

    @Bean
    public FactoryBean<TelegramClient> telegramClientFactory() {
        return new TelegramClientFactory();
    }

    @Bean
    public TDLibSettings tdLibSettings() {
        return TDLibSettings.create(apiToken());
    }

    @Bean
    public APIToken apiToken() {
        return new APIToken(
                Integer.parseInt(appProperties.getProperty("telegram.api_id")),
                appProperties.getProperty("telegram.api_hash")
        );
    }
}
