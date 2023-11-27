package com.github.yvasyliev.telegram.client;

import it.tdlight.client.SimpleTelegramClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TdLightClient {
    @Autowired
    private SimpleTelegramClient client;

}
