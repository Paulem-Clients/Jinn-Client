package io.github.paulem.launcherupdater;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Main {
    public static void main(String[] args) {
        try {
            downloadFromGithub();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Une erreur est survenue, merci de le dire à paulem !\n" + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void launch() throws IOException {
        try (JarFile jarFile = new JarFile("Launcher.jar")) {
            final Manifest manifest = jarFile.getManifest();
            final Attributes attributes = manifest.getMainAttributes();
            final String javaVersion = attributes.getValue("Version");

            if (javaVersion.isEmpty())
                throw new IllegalStateException("Version isn't specified, ask paulem on Discord!");

            new ProcessBuilder("j" + javaVersion + "\\bin\\java.exe", "-jar", "Launcher.jar").start();
        }
    }

    public static void downloadFromGithub() throws Exception {
        final String apiUrl = "https://api.github.com/repos/Paulem79/Launcher/releases/latest";
        final String jsonString = getJsonStringFromUrl(apiUrl);
        final String downloadUrl = getDownloadUrlFromJsonString(jsonString);
        final URL fileUrl = new URI(downloadUrl).toURL();
        final HttpURLConnection connection = (HttpURLConnection)fileUrl.openConnection();

        compareFile(connection, downloadUrl);
        launch();
    }

    public static void downloadFile(final String url) throws IOException, URISyntaxException {
        final Path localFilePath = Paths.get("Launcher.jar");
        final URL fileUrl = new URI(url).toURL();
        final HttpURLConnection connection = (HttpURLConnection)fileUrl.openConnection();

        try (final InputStream in = connection.getInputStream();
             final OutputStream out = Files.newOutputStream(localFilePath.toFile().toPath())) {
            final byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    public static String getJsonStringFromUrl(final String url) throws IOException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        final URL apiUrl = new URI(url).toURL();
        final HttpsURLConnection connection = (HttpsURLConnection)apiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] x509Certificates, final String s) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] x509Certificates, final String s) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        } }, new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            return responseBuilder.toString();
        }
    }

    public static String getDownloadUrlFromJsonString(final String jsonString) {
        final int startIndex = jsonString.indexOf("\"name\": \"Launcher.jar\"");
        final int endIndex = jsonString.indexOf("\"browser_download_url\":", startIndex) + 24;
        final int endIndex2 = jsonString.indexOf("\"", endIndex);

        return jsonString.substring(endIndex, endIndex2);
    }

    public static byte[] getSha256Hash(final InputStream inputStream) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1)
            digest.update(buffer, 0, bytesRead);

        return digest.digest();
    }

    public static void compareFile(final HttpURLConnection connection, final String downloadUrl) throws Exception {
        final File localFile = new File("Launcher.jar");
        if (localFile.exists() && localFile.isFile() && connection.getResponseCode() == 200) {
            try (final InputStream remoteStream = connection.getInputStream();
                 final InputStream localStream = Files.newInputStream(localFile.toPath())) {
                final byte[] remoteFileHash = getSha256Hash(remoteStream);
                final byte[] localFileHash = getSha256Hash(localStream);
                if (!MessageDigest.isEqual(remoteFileHash, localFileHash))
                    downloadFile(downloadUrl);
            }
        }
        else {
            downloadFile(downloadUrl);
        }
    }
}