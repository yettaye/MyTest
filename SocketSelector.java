package com.io.share;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author:
 */
public class SocketSelector {

    private ServerSocketChannel serverSocketChannel = null;
    private Selector selector = null;
    int port = 9090;

    public void initServer() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));


            // select  poll  *epoll  优先选择：epoll  但是可以 -D修正,如果在epoll模型下，open--》  epoll_create -> fd3
            selector = Selector.open();

            /*
            register
            如果：
            select，poll：jvm里开辟一个数组 fd4 放进去
            epoll：  epoll_ctl(fd3,ADD,fd4,EPOLLIN
             */
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        initServer();
        System.out.println("服务器启动了9090。。。。。");
        try {
            while (true) {

                Set<SelectionKey> keys = selector.keys();
                System.out.println(keys.size()+"   size");
                //1,调用多路复用器(select,poll  or  epoll  (epoll_wait))
                /*
                select()是啥意思：
                1，select，poll  其实  内核的select（fd4）  poll(fd4)
                2，epoll：  其实 内核的 epoll_wait()
                *, 参数可以带时间：没有时间，0  ：  阻塞，有时间设置一个超时
                selector.wakeup()  结果返回0

                懒加载：
                其实再触碰到selector.select()调用的时候触发了epoll_ctl的调用
                 */
                while (selector.select() > 0) {
                    //返回的有状态的fd集合
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectionKeys.iterator();
                    //我前边可以强调过，socket：  listen   通信 R/W
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove(); //set  不移除会重复循环处理
                        if (key.isAcceptable()) {
                            //看代码的时候，这里是重点，如果要去接受一个新的连接
                            //语义上，accept接受连接且返回新连接的FD对吧？
                            //那新的FD怎么办？
                            //select，poll，因为他们内核没有空间，那么在jvm中保存和前边的fd4那个listen的一起
                            //epoll： 我们希望通过epoll_ctl把新的客户端fd注册到内核空间
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            //连read 还有 write都处理了
                            //当前线程，如果处理耗时很久，则可能阻塞，其他的io无法处理。所以出现了io threads
                            //redis使用了多worker
                            readHandler(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptHandler(SelectionKey key) {
        try {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel client = ssc.accept();
            client.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.allocate(8192);
            /*
            select，poll：jvm里开辟一个数组 fd7 放进去
            epoll：  epoll_ctl(fd3,ADD,fd7,EPOLLIN
             */
            client.register(selector, SelectionKey.OP_READ, buffer);
            System.out.println("新客户端：" + client.getRemoteAddress());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readHandler(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();
        int read = 0;
        try {
            while (true) {
                read = client.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < buffer.limit(); i++) {
                        sb.append((char)buffer.get(i));
                    }
                    System.out.println(client.getRemoteAddress()+":"+sb.toString());
                    buffer.clear();
                } else if (read == 0) {
                    break;
                } else {
                    client.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void main(String[] args) {
        SocketSelector service = new SocketSelector();
        service.start();
    }
}
