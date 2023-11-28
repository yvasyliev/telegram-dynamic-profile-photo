package com.github.yvasyliev.model;

import java.io.InputStream;

public record QRCode(InputStream inputStream, String filename) {
}
