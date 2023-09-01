package app.telegram.configs;

import api.deezer.DeezerApi;
import app.telegram.clients.SyncTelegramClient;
import app.telegram.commands.Command;
import app.telegram.commands.LoginDeezer;
import app.telegram.commands.LoginTelegram;
import app.telegram.commands.LogoutTelegram;
import app.telegram.commands.UpdateProfilePhoto;
import app.telegram.factories.TelegramClientFactory;
import app.telegram.properties.AppProperties;
import app.telegram.services.ImageProcessor;
import app.telegram.services.MakeDarker;
import app.telegram.services.PrintText;
import app.telegram.suppliers.TrackToPrint;
import it.tdlight.ClientFactory;
import it.tdlight.Init;
import it.tdlight.TelegramClient;
import it.tdlight.client.APIToken;
import it.tdlight.client.TDLibSettings;
import it.tdlight.util.UnsupportedNativeLibraryException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.io.File;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

/**
 * Spring context config.
 */
@Configuration
@PropertySource("file:app.properties")
public class AppConfig {
    @Value("${deezer.access_token}")
    private String deezerAccessToken;

    @Value("${telegram.api_id}")
    private int telegramAppId;

    @Value("${telegram.api_hash}")
    private String telegramApiHash;

    @Bean(initMethod = "load", destroyMethod = "store")
    public Properties appProperties() {
        return new AppProperties();
    }

    @Bean
    public Map<String, Command> commandMap() {
        var commandMap = new HashMap<String, Command>();
        commandMap.put("deezer.login", loginDeezer());
        commandMap.put("telegram.login", loginTelegram());
        commandMap.put("telegram.logout", logoutTelegram());
        commandMap.put("telegram.update_photo", updateProfilePhoto());
        return commandMap;
    }

    @Bean
    public Command loginDeezer() {
        return new LoginDeezer();
    }

    @Bean
    public DeezerApi deezerApi() {
        return new DeezerApi(deezerAccessToken);
    }

    @Bean
    public Command loginTelegram() {
        return new LoginTelegram();
    }

    @Bean(initMethod = "setTdlibParameters", destroyMethod = "close")
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
        return new APIToken(telegramAppId, telegramApiHash);
    }

    @Bean
    public LogoutTelegram logoutTelegram() {
        return new LogoutTelegram();
    }

    @Bean
    public UpdateProfilePhoto updateProfilePhoto() {
        return new UpdateProfilePhoto();
    }

    @Bean
    public Queue<ImageProcessor> imageProcessorQueue() {
        var imageProcessorQueue = new ArrayDeque<ImageProcessor>();
        imageProcessorQueue.add(makeDarker());
        imageProcessorQueue.add(printText());
        return imageProcessorQueue;
    }

    @Bean
    public ImageProcessor makeDarker() {
        return new MakeDarker();
    }

    @Bean
    public Color darkerColor() {
        return new Color(0, 0, 0, 128);
    }

    @Bean
    public ImageProcessor printText() {
        return new PrintText();
    }

    @Bean
    public TrackToPrint trackToPrint() {
        return new TrackToPrint();
    }

    @Bean
    public Stroke outlineStroke() {
        return new BasicStroke(20);
    }

    @Bean
    public Color outlineColor() {
        return new Color(38, 38, 38);
    }

    @Bean
    public Color textColor() {
        return new Color(227, 225, 225);
    }

    @Bean
    public Font baseFont() {
        return new Font(Font.SERIF, Font.BOLD, 10);
    }

    @Bean
    public File photo() {
        return new File("photo.png");
    }

    @Bean(destroyMethod = "close")
    public ClientFactory clientFactory() throws UnsupportedNativeLibraryException {
        Init.init();
        return ClientFactory.create();
    }
}
