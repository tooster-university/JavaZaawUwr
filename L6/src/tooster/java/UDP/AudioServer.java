package tooster.java.UDP;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class AudioServer {
    static final int packetHashSize = 4;
    static final int packetIndexSize = 4;
    static final int preambleSize = packetHashSize + packetIndexSize;
    static final int partSize = 512;
    static final int maxPacketSize = preambleSize + partSize;

    private static class AudioServerThread implements Runnable {

        InetAddress ia;
        int port;
        URL musicDir;

        AudioServerThread(InetAddress ia, int port, URL musicDir) {
            this.ia = ia;
            this.port = port;
            this.musicDir = musicDir;
        }

        @Override
        public void run() {

            try (
                    MulticastSocket msock = new MulticastSocket(port)) {

                msock.joinGroup(ia);
                msock.setTimeToLive(100);


                for (; ; ) {
                    ByteBuffer buf = ByteBuffer.allocate(maxPacketSize);
                    Stream<Path> paths = Files.walk(Paths.get(musicDir.toURI()));

                    paths.filter(Files::isRegularFile).forEach((f) -> { // send each file
                        try (FileInputStream in = new FileInputStream(f.toFile())) {

                            BufferedImage img = ImageIO.read(in); // read image
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(img, "jpg", baos);
                            baos.flush();
                            byte[] data = baos.toByteArray();
                            baos.close();

                            for (int i = 0; i * partSize < data.length; i++) {
                                int len = Math.min(partSize, data.length - partSize*i);
                                buf.putInt(data.hashCode());
                                buf.putInt(i); // to identify part
                                buf.put(data, i * partSize, len);
                                DatagramPacket dp = new DatagramPacket(buf.array(), preambleSize + len, ia, port);
                                msock.send(dp);
                                buf.clear();
                            }

                            System.err.println("Sent '" + f.getFileName() + "' of size " + data.length);

                            Thread.sleep(5 * 1000); // wait 5 seconds and send another file

                        } catch (FileNotFoundException e) {
                            System.err.println("File not found");
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();

                        } catch (InterruptedException e) {
                            System.err.println("Server interrupted.");
                            e.printStackTrace();
                            System.exit(0);
                        }

                    });

                }
            } catch (IOException e) {
                System.err.println("Socket error.");
                e.printStackTrace();
            } catch (URISyntaxException e) {
                System.err.println("Error opening a file.");
                e.printStackTrace();
            }

        }

    }

    // default arguments: multicast group: '224.0.0.1'   port: '2268'   folder: '/music'
    public static void main(String[] _args) throws UnknownHostException, MalformedURLException {


        // argument defaulting
        String[] args = Arrays.copyOf(_args, 3);
        switch (_args.length) {
            case 0:
                args[0] = "224.0.0.1";
            case 1:
                args[1] = "2468";
            case 2:
                args[2] = "/jpg";
        }
        InetAddress ia = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        URL musicDir = AudioServer.class.getResource(args[2]);
        if (musicDir == null)
            System.err.println("invalid folder.");
        else
            new Thread(new AudioServerThread(ia, port, musicDir)).start();

    }

}
