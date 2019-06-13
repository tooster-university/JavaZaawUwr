package tooster.java.UDP;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.PriorityQueue;

public class AudioClient {
    static JLabel label = new JLabel("placeholder");

    private static class Receiver implements Runnable {

        private static final int wsize = 1000; // window size,


        // FIXME: what if the packet belongs to different audio file ???

        InetAddress ia;
        int port;

        Receiver(InetAddress ia, int port) {
            this.ia = ia;
            this.port = port;
        }

        @Override
        public void run() {
            try (MulticastSocket msock = new MulticastSocket(port)) {

                msock.joinGroup(ia);
                msock.setSoTimeout(5 * 1000); // 5 second wait, if nothing is received - write to file

                for (; ; ) { // receiving files
                    PriorityQueue<ByteBuffer> partsQueue = new PriorityQueue<>((bb1, bb2) -> {
                        int bb1nr = bb1.getInt(AudioServer.packetHashSize);
                        int bb2nr = bb2.getInt(AudioServer.packetHashSize);
                        return Integer.compare(bb1nr, bb2nr);
                    });
                    try {
                        for (; ; ) {
                            ByteBuffer bb = ByteBuffer.allocate(AudioServer.maxPacketSize);
                            DatagramPacket dp = new DatagramPacket(bb.array(), bb.array().length);
                            msock.receive(dp);
                            partsQueue.add(bb);
                        }
                    } catch (SocketTimeoutException ste) {
                        ByteBuffer inbb;
                        inbb = partsQueue.poll();
                        if (inbb != null) {
                            int hashcode = inbb.getInt(0);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            do {
                                if(inbb.getInt(0) == hashcode)
                                    baos.write(inbb.array(), AudioServer.preambleSize, inbb.array().length - AudioServer.preambleSize);
                            } while ((inbb = partsQueue.poll()) != null);

                            baos.flush();
                            byte[] bytes = baos.toByteArray();
                            baos.close();
                            InputStream bais = new ByteArrayInputStream(bytes);
                            BufferedImage img = ImageIO.read(bais);

                            System.out.println("Received image.");

                            if (img != null)
                                label.setIcon(new ImageIcon(img));
                            else System.err.println("Corrupted image file.");

                        }
                    }


                }

            } catch (IOException e) {
                System.err.println("Audio client socket error.");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    // default arguments: multicast group: '224.0.0.1'   port: '2268'   folder: '/music'
    public static void main(String[] _args) throws UnknownHostException {


        // argument defaulting
        String[] args = Arrays.copyOf(_args, 2);
        switch (_args.length) {
            case 0:
                args[0] = "224.0.0.1";
            case 1:
                args[1] = "2468";
        }
        InetAddress ia = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);

        new Thread(new Receiver(ia, port)).start();

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);
//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if you want the X button to close the app

    }
}
