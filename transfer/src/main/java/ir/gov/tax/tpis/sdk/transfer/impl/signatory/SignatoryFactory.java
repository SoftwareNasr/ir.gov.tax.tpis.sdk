package ir.gov.tax.tpis.sdk.transfer.impl.signatory;

import ir.gov.tax.tpis.sdk.transfer.interfaces.Signatory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SignatoryFactory {
    private static final SignatoryFactory INSTANCE = new SignatoryFactory();

    private SignatoryFactory() {
    }

    public static SignatoryFactory getInstance() {
        return INSTANCE;
    }

    public Signatory createPKCS8Signatory(File pkcs8PrivateKey, String keyId) throws IOException {
        String fileContent = Files.readString(pkcs8PrivateKey.toPath(), StandardCharsets.US_ASCII);
        fileContent = fileContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("\n", "")
                .replace("\r", "")
                .replace("-----END PRIVATE KEY-----", "");
        return new InMemorySignatory(fileContent, keyId);
    }

    public Signatory createPKCS8Signatory(InputStream inputStream, String keyId) throws IOException {
        String inputStreamData = new BufferedReader(new InputStreamReader(inputStream))
                .lines().parallel().collect(Collectors.joining(""))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("\n", "")
                .replace("\r", "")
                .replace("-----END PRIVATE KEY-----", "");
        return new InMemorySignatory(inputStreamData, keyId);
    }

    public Signatory createInMemorySignatory(String base64PrivateKey, String keyId) {
        return new InMemorySignatory(base64PrivateKey, keyId);
    }
}
