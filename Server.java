import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

class ServerToClient extends Thread{
    public ServerToClient(){
        this.start();
    }

    public void run(){
        ServerTCP.main(null);
    }
}

public class Server{
    private static final int[] ports = new int[] {7000,6789};
    private static final int bufsize = 4096;
    static boolean primary = false;
    private static final int maxfailedrounds = 5;
    private static final int timeout = 2000;
    private static final int period = 2000;

    public static void main(String[] args) throws IOException {
        //args: hostname
        if(args.length == 0) {
            primary = true;
        }

        if(primary){
            primary_server();
        }
        else    second_server();
    }

    private static void primary_server() {
        new ServerToClient();
        try (DatagramSocket ds = new DatagramSocket(ports[0])) {
            System.out.println("Socket Datagram listening at " + ports[0]);
            while (true) {
                byte[] buf = new byte[bufsize];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                ds.receive(dp);
                ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
                DataInputStream dis = new DataInputStream(bais);
                int count = dis.readInt();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeInt(count);
                byte[] resp = baos.toByteArray();
                DatagramPacket dpresp = new DatagramPacket(resp, resp.length, dp.getAddress(), dp.getPort());
                ds.send(dpresp);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }

    private static void second_server() throws UnknownHostException {
        int count = 1;

        InetAddress ia = InetAddress.getByName("localhost");
        try (DatagramSocket ds = new DatagramSocket(ports[1])) {
            System.out.println("Socket Datagram listening at " + ports[1]);
            ds.setSoTimeout(timeout);
            int failedheartbeats = 0;
            while (failedheartbeats < maxfailedrounds) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);
                    dos.writeInt(count++);
                    byte [] buf = baos.toByteArray();

                    DatagramPacket dp = new DatagramPacket(buf, buf.length, ia, ports[0]);
                    ds.send(dp);

                    byte [] rbuf = new byte[bufsize];
                    DatagramPacket dr = new DatagramPacket(rbuf, rbuf.length);

                    ds.receive(dr);
                    failedheartbeats = 0;
                    ByteArrayInputStream bais = new ByteArrayInputStream(rbuf, 0, dr.getLength());
                    DataInputStream dis = new DataInputStream(bais);
                    int n = dis.readInt();
                    System.out.println("Got: " + n + ".");
                }
                catch (SocketTimeoutException ste) {
                    failedheartbeats++;
                    System.out.println("Failed heartbeats: " + failedheartbeats);
                }
                Thread.sleep(period);
            }
            ds.close();
            primary_server();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e){
            System.out.println("IO: " + e.getMessage());
        }
    }
}


