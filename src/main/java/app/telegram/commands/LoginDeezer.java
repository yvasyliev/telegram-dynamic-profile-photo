package app.telegram.commands;

import api.deezer.DeezerApi;
import api.deezer.exceptions.DeezerException;
import api.deezer.objects.AccessToken;
import api.deezer.objects.Permission;
import it.tdlight.common.utils.ScannerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Scanner;

public class LoginDeezer implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginDeezer.class);

    @Autowired
    private DeezerApi deezerApi;

    @Value("${deezer.app_id}")
    private long appId;

    @Value("${deezer.redirect_uri}")
    private String redirectUri;

    @Value("${deezer.secret}")
    private String secret;

    @Autowired
    private Permission permission;

    @Autowired
    private Scanner scanner; // TODO: 2/18/2023 remove

    @Override
    public void execute() {
        try {
            String loginUrl = deezerApi.auth().getLoginUrl(appId, redirectUri, permission);

            System.out.println("Please follow the link and login to Deezer:\n" + loginUrl);

            String code = ScannerUtils.askParameter("code", "Please enter code");

            AccessToken accessToken = deezerApi.auth().getAccessToken(appId, secret, code).execute();
            deezerApi.setAccessToken(accessToken);

            System.out.println("Logged in into Deezer.");
        } catch (DeezerException e) {
            LOGGER.error("Failed to execute LoginDeezer command.", e);
        }
    }
}
