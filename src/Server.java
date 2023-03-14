import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import groovy.json.JsonToken;

public class Server {


            FileWriter file = new FileWriter("C:\\Users\\solta\\Downloads\\Microblogserver\\storage\\Messages.txt");
            Writer filewrite = new BufferedWriter(file);


    ByteBuffer buffer=ByteBuffer.allocate(1024);

    public Server() throws IOException {
    }

    public void run() throws IOException {
        try(Selector sel = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open()){
            ssc.bind(new InetSocketAddress("localhost",54321 ));
            ssc.configureBlocking(false);
            ssc.register(sel, SelectionKey.OP_ACCEPT);
            while(true){
                sel.select();
                Set<SelectionKey> slctdKeys = sel.selectedKeys();
                Iterator<SelectionKey> keyIterator
                        = slctdKeys.iterator();
                while(keyIterator.hasNext()){
                    SelectionKey key = keyIterator.next();
                    if(key.isAcceptable()) handleConnectionRequest(key);
                    if(key.isReadable()) handleClientRequest(key);
                    keyIterator.remove();


                }}}}

    private void handleConnectionRequest(SelectionKey key) throws IOException {
        SocketChannel csc =
                ((ServerSocketChannel) key.channel()).accept();
        csc.configureBlocking(false);
        SelectionKey cscKey =
                csc.register(key.selector(),SelectionKey.OP_READ);
        cscKey.attach("hello");
        csc.write(ByteBuffer.wrap(
                "hello!".getBytes(StandardCharsets.UTF_8)));
    }

    private void handleClientRequest(SelectionKey key) throws IOException {
        SocketChannel newsc= (SocketChannel) key.channel();
        StringBuffer msg = new StringBuffer();
        newsc.read(buffer);
        buffer.flip();
        while(buffer.hasRemaining()) {
            char c = (char) buffer.get();
            if (c == '\r' || c == '\n') break;
            msg.append(c);
        }
        buffer.clear();

        System.out.println(msg);
        filewrite.write(String.valueOf(msg));
        filewrite.flush();

    }
}