package app.telegram.client;

import it.tdlight.client.TDLibSettings;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SyncTelegramClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncTelegramClient.class);

    @Autowired
    private TelegramClient telegramClient;

    @Autowired
    private TDLibSettings tdLibSettings;

    @Value("5")
    private long timeout;

    @PostConstruct
    public TdApi.Ok setTdlibParameters() throws ExecutionException, InterruptedException, TimeoutException {
        return send(new TdApi.SetTdlibParameters(
                tdLibSettings.isUsingTestDatacenter(),
                tdLibSettings.getDatabaseDirectoryPath().toString(),
                tdLibSettings.getDownloadedFilesDirectoryPath().toString(),
                null,
                tdLibSettings.isFileDatabaseEnabled(),
                tdLibSettings.isChatInfoDatabaseEnabled(),
                tdLibSettings.isMessageDatabaseEnabled(),
                false,
                tdLibSettings.getApiToken().getApiID(),
                tdLibSettings.getApiToken().getApiHash(),
                tdLibSettings.getSystemLanguageCode(),
                tdLibSettings.getDeviceModel(),
                tdLibSettings.getSystemVersion(),
                tdLibSettings.getApplicationVersion(),
                tdLibSettings.isStorageOptimizerEnabled(),
                tdLibSettings.isIgnoreFileNames()
        ));
    }

    @SuppressWarnings("unchecked")
    public <T extends TdApi.Object> T send(TdApi.Function<T> request) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<TdApi.Object> response = new CompletableFuture<>();
        telegramClient.send(request, response::complete, response::completeExceptionally);
        return (T) response.get(timeout, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void close() throws ExecutionException, InterruptedException, TimeoutException {
        send(new TdApi.Close());
    }
}
