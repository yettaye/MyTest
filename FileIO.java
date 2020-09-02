package com.io.share;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * TODO
 *
 * @author yechunying 2020/9/1 16:31
 */
public class FileIO {
    @Test
    public  void whatByteBuffer(){

//        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);


        System.out.println("postition: " + buffer.position());
        System.out.println("limit: " +  buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer);

        buffer.put("123".getBytes());

        System.out.println("-------------put:123......");
        System.out.println("mark: " + buffer);

        buffer.flip();   //读写交替

        System.out.println("-------------flip......");
        System.out.println("mark: " + buffer);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buffer.limit(); i++) {
            sb.append((char)buffer.get(i));
        }
        System.out.println("-------------filp+read......");
        System.out.println(sb.toString());

        buffer.get();

        System.out.println("-------------get......");
        System.out.println("mark: " + buffer);

        buffer.compact();

        System.out.println("-------------compact......");
        System.out.println("mark: " + buffer);

        buffer.clear();

        System.out.println("-------------clear......");
        System.out.println("mark: " + buffer);

    }
}
