package app.telegram.commands;

import api.deezer.DeezerApi;
import api.deezer.objects.Permission;
import it.tdlight.common.utils.ScannerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

/**
 * Logs in into Deezer.
 */
public class LoginDeezer implements Command {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginDeezer.class);

    /**
     * Deezer client wrapper.
     */
    @Autowired
    private DeezerApi deezerApi;

    /**
     * Application properties.
     */
    @Autowired
    @Qualifier("appProperties")
    private Properties appProperties;

    /**
     * Deezer {@code app_id}.
     */
    @Value("#{appProperties.getProperty('deezer.app_id')}")
    private long appId;

    /**
     * Deezer {@code redirect_uri}.
     */
    @Value("#{appProperties.getProperty('deezer.redirect_uri')}")
    private String redirectUri;

    /**
     * Deezer {@code secret}.
     */
    @Value("#{appProperties.getProperty('deezer.secret')}")
    private String secret;

    @Override
    public void execute() throws Exception {
        var loginUrl = deezerApi.auth().getLoginUrl(appId, redirectUri, Permission.LISTENING_HISTORY);

        System.out.println("Please follow the link and login to Deezer:\n" + loginUrl);

        var code = ScannerUtils.askParameter("code", "Please enter code");

        var accessToken = deezerApi.auth().getAccessToken(appId, secret, code).execute();
        deezerApi.setAccessToken(accessToken);
        appProperties.setProperty("deezer.access_token", accessToken.getAccessToken());

        LOGGER.info("Logged in into Deezer.");
    }
}
