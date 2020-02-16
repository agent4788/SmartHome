package net.kleditzsch.SmartHome.utility.string;

public abstract class StringUtil {

    /**
     * limitiert die Länge eines Textes anhand der Wortgrenzen
     *
     * @param content Text
     * @param maxLength Maximale länge
     * @return Text
     */
    public static String limitTextLength(String content, int maxLength) {

        return limitTextLength(content, maxLength, "...");
    }

    /**
     * limitiert die Länge eines Textes anhand der Wortgrenzen
     *
     * @param content Text
     * @param maxLength Maximale länge
     * @param endString Zeichen die am Textende angehängt werden
     * @return Text
     */
    public static String limitTextLength(String content, int maxLength, String endString) {

        if(content.length() > maxLength) {

            if (content.contains(" ")) {

                if (content.substring(0, maxLength + 1).contains(" ")) {

                    return content.substring(0, content.substring(0, maxLength + 1).lastIndexOf(" ")) + " " + endString;
                } else {

                    return content.substring(0, content.indexOf("")) + " " + endString;
                }
            }
        }
        return content;
    }
}
