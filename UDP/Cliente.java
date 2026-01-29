
package UDP;



import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Cliente
{

    private static final int PUERTO_SERVIDOR = 6000;
    private static final String HOST = "localhost";

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();

        DatagramSocket socket = new DatagramSocket();
        InetAddress ipServidor = InetAddress.getByName(HOST);

        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket recibido = new DatagramPacket(buffer, buffer.length);
                    socket.receive(recibido);
                    String msg = new String(recibido.getData(), 0, recibido.getLength()).trim();

                    if ("SERVER#SALA_LLENA".equals(msg)) {
                        System.out.println("Servidor: sala llena, no puedes entrar.");
                        socket.close();
                        break;
                    } else {
                        System.out.println(msg);
                    }
                }
            } catch (Exception ignored) {}
        }).start();

        System.out.println("(Escribe mensajes. Para salir: /salir)");
        while (!socket.isClosed()) {
            String texto = sc.nextLine();
            if ("/salir".equalsIgnoreCase(texto.trim())) {
                socket.close();
                break;
            }

            String mipaquete = nombre + ":" + texto;
            byte[] data = mipaquete.getBytes();
            DatagramPacket dp = new DatagramPacket(data, data.length, ipServidor, PUERTO_SERVIDOR);
            socket.send(dp);
        }
    }
}