
package UDP;

/**
 *
 *
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server {

    static class ClienteInformacion {
        InetAddress ip;
        int puerto;

        ClienteInformacion(InetAddress ip, int puerto) {
            this.ip = ip;
            this.puerto = puerto;
        }
    }

    private static final int PUERTO = 6000;
    private static final int MAX = 3;

    private static final ArrayList<ClienteInformacion> clientes = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket(PUERTO);
        System.out.println("Servidor UDP chat en puerto " + PUERTO);

        byte[] buffer = new byte[1024];

        while (true) {
            DatagramPacket recibido = new DatagramPacket(buffer, buffer.length);
            socket.receive(recibido);

            InetAddress ipEmisor = recibido.getAddress();
            int puertoEmisor = recibido.getPort();

            String texto = new String(recibido.getData(), 0, recibido.getLength()).trim();

            // 1) Registrar al cliente si es nuevo
            boolean existe = false;
            for (ClienteInformacion c : clientes) {
                if (c.ip.equals(ipEmisor) && c.puerto == puertoEmisor) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                if (clientes.size() >= MAX) {
                    enviar(socket, ipEmisor, puertoEmisor, "SERVER#SALA_LLENA");
                    continue;
                }
                clientes.add(new ClienteInformacion(ipEmisor, puertoEmisor));
                enviar(socket, ipEmisor, puertoEmisor, "SERVER#OK");
                broadcast(socket, "SERVER#Se ha unido un usuario (" + clientes.size() + "/" + MAX + ")", ipEmisor, puertoEmisor);
            }


            broadcast(socket, texto, ipEmisor, puertoEmisor);
        }
    }

    private static void broadcast(DatagramSocket socket, String msg, InetAddress ipEmisor, int puertoEmisor) throws Exception {
        byte[] data = msg.getBytes();

        for (ClienteInformacion c : clientes) {
            if (!(c.ip.equals(ipEmisor) && c.puerto == puertoEmisor)) {
                DatagramPacket p = new DatagramPacket(data, data.length, c.ip, c.puerto);
                socket.send(p);
            }
        }
    }

    private static void enviar(DatagramSocket socket, InetAddress ip, int puerto, String msg) throws Exception {
        byte[] data = msg.getBytes();
        DatagramPacket p = new DatagramPacket(data, data.length, ip, puerto);
        socket.send(p);
    }
}