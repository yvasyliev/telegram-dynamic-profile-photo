package com.github.yvasyliev.service;

import com.github.yvasyliev.model.QRCodeDTO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingFunction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class QRCodeFactory implements ThrowingFunction<String, QRCodeDTO> {
    @Autowired
    private Writer qrCodeWriter;

    @Value("300")
    private int width;

    @Value("300")
    private int height;

    @Value("png")
    private String format;

    @Value("%s.%s")
    private String filenameTemplate;

    @Override
    @NonNull
    public QRCodeDTO applyWithException(@NonNull String url) throws WriterException, IOException {
        var bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
        try (var outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, format, outputStream);
            return new QRCodeDTO(
                    new ByteArrayInputStream(outputStream.toByteArray()),
                    filenameTemplate.formatted(UUID.randomUUID(), format)
            );
        }
    }
}
