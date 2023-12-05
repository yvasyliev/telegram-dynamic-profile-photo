package com.github.yvasyliev.model;

import java.io.InputStream;

public record QrCode(InputStream inputStream, String filename) {
}
