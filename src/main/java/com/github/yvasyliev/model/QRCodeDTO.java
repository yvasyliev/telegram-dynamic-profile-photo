package com.github.yvasyliev.model;

import java.io.InputStream;

public record QRCodeDTO(InputStream inputStream, String filename) {
}
