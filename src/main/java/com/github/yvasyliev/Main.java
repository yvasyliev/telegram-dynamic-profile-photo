package com.github.yvasyliev;

import com.google.zxing.Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import it.tdlight.ClientFactory;
import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.APIToken;
import it.tdlight.client.TDLibSettings;
import it.tdlight.jni.TdApi;
import it.tdlight.util.UnsupportedNativeLibraryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

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
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ClientFactory clientFactory() throws UnsupportedNativeLibraryException {
        Init.init();
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());
        return ClientFactory.create();
    }

    @Bean
    public TdApi.SetTdlibParameters setTdlibParameters(TDLibSettings tdLibSettings) {
        var setTdlibParameters = new TdApi.SetTdlibParameters();
        setTdlibParameters.apiId = tdLibSettings.getApiToken().getApiID();
        setTdlibParameters.apiHash = tdLibSettings.getApiToken().getApiHash();
        setTdlibParameters.databaseDirectory = tdLibSettings.getDatabaseDirectoryPath().toString();
        setTdlibParameters.filesDirectory = tdLibSettings.getDownloadedFilesDirectoryPath().toString();
        setTdlibParameters.applicationVersion = tdLibSettings.getApplicationVersion();
        setTdlibParameters.deviceModel = tdLibSettings.getDeviceModel();
        setTdlibParameters.systemVersion = tdLibSettings.getSystemVersion();
        setTdlibParameters.systemLanguageCode = tdLibSettings.getSystemLanguageCode();
        setTdlibParameters.useTestDc = tdLibSettings.isUsingTestDatacenter();
        setTdlibParameters.useChatInfoDatabase = tdLibSettings.isChatInfoDatabaseEnabled();
        setTdlibParameters.useFileDatabase = tdLibSettings.isFileDatabaseEnabled();
        setTdlibParameters.ignoreFileNames = tdLibSettings.isIgnoreFileNames();
        setTdlibParameters.useMessageDatabase = tdLibSettings.isMessageDatabaseEnabled();
        setTdlibParameters.enableStorageOptimizer = tdLibSettings.isStorageOptimizerEnabled();
        return setTdlibParameters;
    }

//    @Bean
//    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
//    public TelegramClient telegramClient(ClientFactory clientFactory) {
//        return clientFactory.createClient();
//    }

    @Bean
    public APIToken apiToken(@Value("${telegram.api.id}") int apiId, @Value("${telegram.api.hash}") String apiHash) {
        return new APIToken(apiId, apiHash);
    }

    @Bean
    public TDLibSettings tdLibSettings(
            APIToken apiToken,
            @Value("#{T(java.nio.file.Paths).get('${telegram.session.path:tdlib-session}')}") Path sessionPath) {
        var settings = TDLibSettings.create(apiToken);
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));
        return settings;
    }

    @Bean
    public Writer qrCodeWriter() {
        return new QRCodeWriter();
    }

    @Bean
    public Map<Long, String> pendingCommands(@Value("16") int maxSize) {
        return new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, String> eldest) {
                return size() > maxSize;
            }
        };
    }
}
