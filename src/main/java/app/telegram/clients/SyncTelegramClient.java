package app.telegram.clients;

import it.tdlight.client.TDLibSettings;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Synchronized Telegram client wrapper.
 */
public class SyncTelegramClient {
    /**
     * TDLib client wrapper.
     */
    @Autowired
    private TelegramClient telegramClient;

    /**
     * TDLib settings.
     */
    @Autowired
    private TDLibSettings tdLibSettings;

    /**
     * Response reading timeout.
     */
    @Value("5")
    private long timeout;

    /**
     * Sends Telegram API request synchronously.
     *
     * @param request Telegram API request.
     * @param <T>     Telegram API response type.
     * @return Telegram API response.
     * @throws ExecutionException   if errors occur.
     * @throws InterruptedException if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    @SuppressWarnings("unchecked")
    public <T extends TdApi.Object> T send(TdApi.Function<T> request) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<TdApi.Object> response = new CompletableFuture<>();
        telegramClient.send(request, response::complete, response::completeExceptionally);
        return (T) response.get(timeout, TimeUnit.SECONDS);
    }

    /**
     * Sets TDLib settings.
     *
     * @return {@link TdApi.Ok} response.
     * @throws ExecutionException   if errors occur.
     * @throws InterruptedException if errors occur.
     * @throws TimeoutException     if errors occur.
     */
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

    /**
     * Sends {@link TdApi.Close} request.
     *
     * @return {@link TdApi.Ok} response.
     * @throws ExecutionException   if errors occur.
     * @throws InterruptedException if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    @PreDestroy
    public TdApi.Ok close() throws ExecutionException, InterruptedException, TimeoutException {
        return send(new TdApi.Close());
    }
}
