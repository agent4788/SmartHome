package net.kleditzsch.SmartHome.util.api.avm;

import net.kleditzsch.SmartHome.util.api.avm.Exception.AuthException;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Verwaltet die Sessions zur Fritz Box und sendet HTTP Anfragen
 */
public class FritzBoxHandler {

    /**
     * Sitzungs ID
     */
    private String sessionId;

    /**
     * Zeitstempel für Session Timeout
     */
    private LocalDateTime sessionIdTimeout;

    /**
     * maximale Zeit die ein Login gültig ist
     */
    private LocalDateTime loginTimeout;

    /**
     * Fritzbox Adresse
     */
    private String fritzBoxAddress = "fritz.box";

    /**
     * Benutzername und Passwort
     */
    private String username, password;

    /**
     * Logger
     */
    private Logger logger = LoggerUtil.getLogger(this.getClass());

    public FritzBoxHandler() {}

    /**
     * @param fritzBoxAddress Fritz Box Adresse
     * @param password Passwort
     */
    public FritzBoxHandler(String fritzBoxAddress, String password) {
        this.fritzBoxAddress = fritzBoxAddress;
        this.password = password;
    }

    /**
     * @param fritzBoxAddress Fritz Box Adresse
     * @param username Benutzername
     * @param password Passwort
     */
    public FritzBoxHandler(String fritzBoxAddress, String username, String password) {
        this.fritzBoxAddress = fritzBoxAddress;
        this.username = username;
        this.password = password;
    }

    /**
     * meldet einen Benutzer nur mit Passwort an der Fritz!Box an
     */
    public void login() {

        this.login(fritzBoxAddress, username, password);
    }

    /**
     * meldet einen Benutzer nur mit Passwort an der Fritz!Box an
     *
     * @param fritzBoxAddress Fritz Box Adresse
     * @param password Passwort
     */
    public void login(String fritzBoxAddress, String password) {

        this.login(fritzBoxAddress, "", password);
    }

    /**
     * meldet einen Benutzer mit Benutzername und Passwort an der Fritz!Box an
     *
     * @param fritzBoxAddress Fritz Box Adresse
     * @param username Benutzername
     * @param password Passwort
     */
    public void login(String fritzBoxAddress, String username, String password) {

        this.fritzBoxAddress = fritzBoxAddress;
        this.username = username;
        this.password = password;

        Optional<String> challenge = this.getChallenge();
        if(challenge.isPresent()) {

            Optional<String> sid = sendLogin(challenge.get(), username, password);
            if(sid.isPresent()) {

                this.sessionId = sid.get();
                this.sessionIdTimeout = LocalDateTime.now().plusSeconds(590);
                this.loginTimeout = LocalDateTime.now().plusHours(12);
            } else {

                //Benutzername oder Passwort falsch
                throw new AuthException("Benutzername oder Passwort falsch!");
            }
        } else {

            //Fehlerhafte Fritz!Box Adresse
            throw new AuthException("Fritz!Box Adresse falsch");
        }
    }

    /**
     * gibt die Session ID zurück
     * prüft vorher ob diese Abgelaufen ist und erneuert sie falls nötig
     *
     * @return Sitzungs ID
     */
    private String getSessionId() {

        if(sessionId == null
                || sessionId.equals("0000000000000000")
                || sessionId.equals("")
                || sessionIdTimeout == null
                || sessionIdTimeout.isBefore(LocalDateTime.now())
                || loginTimeout == null
                || loginTimeout.isBefore(LocalDateTime.now())) {

            this.login(fritzBoxAddress, username, password);
        }
        return this.sessionId;
    }

    /**
     * sendet eine HTTP Anfrage an die Fritz!Box
     *
     * @param urlFragment URL Fragment
     * @return HTTP Antwort als String
     */
    public String sendHttpRequest(String urlFragment) throws IOException {

        URL request = new URL("http://" + this.fritzBoxAddress + "/" + urlFragment + "&sid=" + getSessionId());
        this.sessionIdTimeout = LocalDateTime.now().plusSeconds(590);
        return httpGetRequest(request);
    }

    /**
     * fragt die aktuelle Challange ab
     *
     * @return Challenge
     */
    private Optional<String> getChallenge() {

        try {

            URL url = new URL("http://" + this.fritzBoxAddress + "/login_sid.lua ");
            String response = this.httpGetRequest(url);
            return Optional.of(response.substring(response.indexOf("<Challenge>") + 11, response.indexOf("<Challenge>") + 19));

        } catch (IOException e) {

            logger.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * sendet die Login Anfrage an die Fritz!box
     *
     * @param challenge Aufgabe
     * @param username Benutzername
     * @param password Passwort
     * @return Session ID
     */
    private Optional<String> sendLogin(String challenge, String username, String password) {

        String challengeHash = challenge + "-" + password;
        try {

            String challengeHashUtf16 = new String(challengeHash.getBytes("UTF-16LE"), "UTF-8");

            //MD5 Hashen
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(challengeHashUtf16.getBytes());

            final byte byteData[] = digest.digest();
            final BigInteger bigInt = new BigInteger(1, byteData);
            String md5Hash = bigInt.toString(16);
            String challengeResponse = challenge + "-" + md5Hash;

            //HTTP ANfrage schicken
            URL url = new URL("http://" + this.fritzBoxAddress + "/login_sid.lua?user=" + username + "&response=" + challengeResponse);
            String response = this.httpGetRequest(url);

            String sid = response.substring(response.indexOf("<SID>") + 5, response.indexOf("<SID>") + 21);
            if(!sid.equals("0000000000000000")) {

                return Optional.of(sid);
            }
            return Optional.empty();

        } catch (NoSuchAlgorithmException | IOException e) {

            logger.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * sendet eine HTTP Anfrage ab
     *
     * @param url URL
     * @return HTTP Antwort
     */
    private String httpGetRequest(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Java Tools");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String line;
        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while((line = in.readLine()) != null) {

            response.append(line);
        }
        return response.toString();
    }
}
