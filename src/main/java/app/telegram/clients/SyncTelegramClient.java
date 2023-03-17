package app.telegram.clients;

import it.tdlight.client.TDLibSettings;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

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
     * Sends Telegram API request synchronously.
     *
     * @param request Telegram API request.
     * @param <T>     Telegram API response type.
     * @return Telegram API response.
     */
    @SuppressWarnings("unchecked")
    public <T extends TdApi.Object> T send(TdApi.Function<T> request) {
        var response = new CompletableFuture<>();
        telegramClient.send(request, response::complete, response::completeExceptionally);
        return (T) response.join();
    }

    /**
     * Sets TDLib settings.
     *
     * @return {@link TdApi.Ok} response.
     */
    public TdApi.Ok setTdlibParameters() {
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
     */
    public TdApi.Ok close() {
        return send(new TdApi.Close());
    }
}
