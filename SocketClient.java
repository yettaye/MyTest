package com.io.share;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * TODO
 *
 * @author yechunying 2020/9/1 23:36
 */
public class SocketClient {
    public static void main(String[] args) throws IOException {
        InetSocketAddress serverAddr = new InetSocketAddress("122.51.32.199", 9090);
        SocketChannel client = SocketChannel.open(serverAddr);
        client.connect(serverAddr);

        System.in.read();
    }
}
