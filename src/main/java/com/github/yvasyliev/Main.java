package com.github.yvasyliev;

import com.google.zxing.Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.APIToken;
import it.tdlight.client.AuthenticationSupplier;
import it.tdlight.client.ClientInteraction;
import it.tdlight.client.SimpleAuthenticationSupplier;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.client.SimpleTelegramClientBuilder;
import it.tdlight.client.SimpleTelegramClientFactory;
import it.tdlight.client.TDLibSettings;
import it.tdlight.jni.TdApi;
import it.tdlight.util.UnsupportedNativeLibraryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Program main class.
 */
@SpringBootApplication
@Import(TelegramBotStarterConfiguration.class)
public class Main {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Main method.
     *
     * @param args console args.
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public SimpleTelegramClientFactory simpleTelegramClientFactory() throws UnsupportedNativeLibraryException {
        Init.init();
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());
        return new SimpleTelegramClientFactory();
    }

    @Bean
    public APIToken apiToken(@Value("${telegram.api.id}") int apiId, @Value("${telegram.api.hash}") String apiHash) {
        return new APIToken(apiId, apiHash);
    }

    @Bean
    public TDLibSettings tdLibSettings(APIToken apiToken, Path sessionPath) {
        var settings = TDLibSettings.create(apiToken);
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));
        return settings;
    }

    @Bean
    public Path sessionPath() {
        return Paths.get("tdlib-session");
    }

    @Bean
    public SimpleTelegramClientBuilder simpleTelegramClientBuilder(SimpleTelegramClientFactory clientFactory, TDLibSettings settings) {
        return clientFactory.builder(settings);
    }

    @Bean
    public SimpleAuthenticationSupplier<?> simpleAuthenticationSupplier() {
        return AuthenticationSupplier.qrCode();
    }

    @Bean(destroyMethod = "sendClose")
    public SimpleTelegramClient simpleTelegramClient(
            SimpleTelegramClientBuilder telegramClientBuilder,
            ClientInteraction clientInteraction,
            SimpleAuthenticationSupplier<?> authenticationSupplier) {
        telegramClientBuilder.setClientInteraction(clientInteraction);
        telegramClientBuilder.addUpdateHandler(TdApi.UpdateAuthorizationState.class, update -> {
            var authorizationState = update.authorizationState;
            if (authorizationState instanceof TdApi.AuthorizationStateReady) {
                System.out.println("Logged in");
            } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
                System.out.println("Closing...");
            } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
                System.out.println("Closed");
            } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
                System.out.println("Logging out...");
            }
        });
        return telegramClientBuilder.build(authenticationSupplier);
    }

    @Bean
    public Writer qrCodeWriter() {
        return new QRCodeWriter();
    }
}
