package net.kleditzsch.SmartHome.util.api.wol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * sendet einen WakeOnLan Befehl über das Netzwerk
 */
public class WakeOnLan {

    /**
     * Broadcast Port
     */
    public static final int PORT = 9;

    /**
     * sendet einen WakeOnLan Befehl über das Netzwerk
     *
     * @param broadcastAddress Broadcastadresse des Subnetzes
     * @param macAddress MAC Adresse
     * @return erfolg
     */
    public static boolean send(String broadcastAddress, String macAddress) {

        try {
            byte[] macBytes = getMacBytes(macAddress);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {

                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {

                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(broadcastAddress);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
            return true;
        }
        catch (Exception e) {

            return false;
        }
    }

    /**
     * wandelt die MAC Adresse aus einem String in ein Byte Array um
     *
     * @param macStr MAC Adresse
     * @return Byte Array der MAC Adresse
     */
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {

        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {

            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {

            for (int i = 0; i < 6; i++) {

                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {

            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
}
