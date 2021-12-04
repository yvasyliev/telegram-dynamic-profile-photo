package app.telegram.tg;

import it.tdlight.common.Init;
import it.tdlight.common.TelegramClient;
import it.tdlight.common.UpdatesHandler;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import it.tdlight.tdlight.ClientManager;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Telegram client wrapper.
 */
public class TgClient {
    /**
     * Telegram client instance.
     */
    private TelegramClient telegramClient;

    /**
     * Initializes Telegram client.
     *
     * @param apiId   API ID.
     * @param apiHash API HASH
     * @throws CantLoadLibrary      if errors occur.
     * @throws InterruptedException if errors occur.
     * @throws ExecutionException   if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    public void init(int apiId, String apiHash) throws CantLoadLibrary, InterruptedException, ExecutionException, TimeoutException {
        Init.start();
        telegramClient = ClientManager.create();
        telegramClient.initialize((UpdatesHandler) null, null, null);
        telegramClient.execute(new TdApi.SetLogVerbosityLevel(0));
        if (telegramClient.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false))) instanceof TdApi.Error) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }

        TdApi.TdlibParameters tdlibParameters = new TdApi.TdlibParameters();
        tdlibParameters.apiHash = apiHash;
        tdlibParameters.apiId = apiId;
        tdlibParameters.systemLanguageCode = "en";
        tdlibParameters.deviceModel = "Desktop";
        tdlibParameters.applicationVersion = "0.5";

        sendSynchronously(new TdApi.SetTdlibParameters(tdlibParameters));
        sendSynchronously(new TdApi.CheckDatabaseEncryptionKey());
    }

    /**
     * Closes Telegram client.
     *
     * @throws InterruptedException if errors occur.
     * @throws ExecutionException   if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    public void close() throws InterruptedException, ExecutionException, TimeoutException {
        sendSynchronously(new TdApi.Close());
    }

    /**
     * Logins to Telegram client by phone number.
     *
     * @param phoneNumber user phone number.
     * @throws InterruptedException if errors occur.
     * @throws ExecutionException   if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    public void login(String phoneNumber) throws InterruptedException, ExecutionException, TimeoutException {
        sendAuthenticationCode(phoneNumber);
        String code = promptCode();
        checkCode(code);
    }

    /**
     * Logouts from Telegram client.
     *
     * @throws InterruptedException if errors occur.
     * @throws ExecutionException   if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    public void logout() throws InterruptedException, ExecutionException, TimeoutException {
        sendSynchronously(new TdApi.LogOut());
    }

    /**
     * Updates Telegram photo.
     *
     * @param photo new photo.
     * @throws InterruptedException if errors occur.
     * @throws ExecutionException   if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    public void updateProfilePhoto(File photo) throws InterruptedException, ExecutionException, TimeoutException {
        sendSynchronously(new TdApi.SetProfilePhoto(new TdApi.InputChatPhotoStatic(new TdApi.InputFileLocal(photo.getPath()))));
    }

    /**
     * Deletes previous Telegram photo.
     *
     * @throws ExecutionException   if errors occur.
     * @throws InterruptedException if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    public void deleteLastProfilePhoto() throws ExecutionException, InterruptedException, TimeoutException {
        TdApi.ChatPhoto[] profilePhotos = getProfilePhotos();
        if (profilePhotos != null && profilePhotos.length > 0) {
            sendSynchronously(new TdApi.DeleteProfilePhoto(profilePhotos[0].id));
        }
    }

    /**
     * Gets Telegram photos.
     *
     * @return Telegram photos.
     * @throws ExecutionException   if errors occur.
     * @throws InterruptedException if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    private TdApi.ChatPhoto[] getProfilePhotos() throws ExecutionException, InterruptedException, TimeoutException {
        TdApi.User user = (TdApi.User) sendSynchronously(new TdApi.GetMe());
        TdApi.GetUserProfilePhotos getUserProfilePhotos = new TdApi.GetUserProfilePhotos();
        getUserProfilePhotos.limit = 1;
        getUserProfilePhotos.userId = user.id;
        TdApi.ChatPhotos chatPhotos = (TdApi.ChatPhotos) sendSynchronously(getUserProfilePhotos);
        return chatPhotos.photos;
    }

    /**
     * Sends authentication code by phone number.
     *
     * @param phoneNumber user phone number.
     * @throws ExecutionException   if errors occur.
     * @throws InterruptedException if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    private void sendAuthenticationCode(String phoneNumber) throws ExecutionException, InterruptedException, TimeoutException {
        TdApi.SetAuthenticationPhoneNumber setAuthenticationPhoneNumber = new TdApi.SetAuthenticationPhoneNumber();
        setAuthenticationPhoneNumber.phoneNumber = phoneNumber;
        sendSynchronously(setAuthenticationPhoneNumber);
    }

    /**
     * Asks user to type the code into console.
     *
     * @return code.
     */
    private String promptCode() {
        System.out.print("Please enter code: ");
        return new Scanner(System.in).next();
    }

    /**
     * Validates code.
     *
     * @param code code to validate.
     * @throws ExecutionException   if errors occur.
     * @throws InterruptedException if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    private void checkCode(String code) throws ExecutionException, InterruptedException, TimeoutException {
        sendSynchronously(new TdApi.CheckAuthenticationCode(code));
    }

    /**
     * Executes Telegram request synchronously.
     *
     * @param request Telegram Request.
     * @param <T>     Telegram response type.
     * @return Telegram response.
     * @throws InterruptedException if errors occur.
     * @throws ExecutionException   if errors occur.
     * @throws TimeoutException     if errors occur.
     */
    private <T extends TdApi.Object> TdApi.Object sendSynchronously(TdApi.Function<T> request) throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<TdApi.Object> response = new CompletableFuture<>();
        telegramClient.send(request, response::complete, response::completeExceptionally);
        return response.get(5, TimeUnit.SECONDS);
    }
}
