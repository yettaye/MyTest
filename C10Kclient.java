package com.io.share;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * @author:
 * @create:
 */
public class C10Kclient {

    public static void main(String[] args) {
        LinkedList<SocketChannel> clients = new LinkedList<>();
        InetSocketAddress serverAddr = new InetSocketAddress("122.51.32.199", 9090);

        //端口号的问题：65535
        //  windows
        for (int i = 10000; i < 65000; i++) {
            try {
                SocketChannel client1 = SocketChannel.open();
                client1.bind(new InetSocketAddress(i));
                client1.connect(serverAddr);
                clients.add(client1);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        System.out.println("clients "+ clients.size());

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
