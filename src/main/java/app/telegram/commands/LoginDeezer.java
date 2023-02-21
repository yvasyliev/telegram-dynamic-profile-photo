package app.telegram.commands;

import api.deezer.DeezerApi;
import api.deezer.objects.AccessToken;
import api.deezer.objects.Permission;
import it.tdlight.common.utils.ScannerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

public class LoginDeezer implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginDeezer.class);

    @Autowired
    private DeezerApi deezerApi;

    @Autowired
    @Qualifier("appProperties")
    private Properties appProperties;

    @Value("#{appProperties.getProperty('deezer.app_id')}")
    private long appId;

    @Value("#{appProperties.getProperty('deezer.redirect_uri')}")
    private String redirectUri;

    @Value("#{appProperties.getProperty('deezer.secret')}")
    private String secret;

    @Override
    public void execute() throws Exception {
        String loginUrl = deezerApi.auth().getLoginUrl(appId, redirectUri, Permission.LISTENING_HISTORY);

        System.out.println("Please follow the link and login to Deezer:\n" + loginUrl);

        String code = ScannerUtils.askParameter("code", "Please enter code");

        AccessToken accessToken = deezerApi.auth().getAccessToken(appId, secret, code).execute();
        deezerApi.setAccessToken(accessToken);
        appProperties.setProperty("deezer.access_token", accessToken.getAccessToken());

        LOGGER.info("Logged in into Deezer.");

    }
}
