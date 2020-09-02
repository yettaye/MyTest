package com.io.share;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author:
 * @create:
 * BIO  多线程的方式
 */
public class SocketBIO {


    public static void main(String[] args) {
        ServerSocket server = null;
        ExecutorService executor = new ThreadPoolExecutor(2, 2,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        try {
            server = new ServerSocket(9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("server up use 9090!");
        try {
            while (true) {

                 System.in.read();  //分水岭：
                //阻塞的，没有 -1  一直卡着不动  accept(4,
                Socket client = server.accept();
                System.out.println("client port: " + client.getPort());

                //client.read   //阻塞   没有  -1 0
                executor.submit(() -> {
                    try {
                        //等待输入
                        System.in.read();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        while (true) {
                            String dataline = reader.readLine();
                            if (dataline!=null) {
                                System.out.println("client read some data is :" + dataline);
                            } else {
                                System.out.println("client readed nothing...");
                                client.close();
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
