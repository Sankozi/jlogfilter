package org.sankozi.logfilter.log4j;

import org.sankozi.logfilter.Level;
import org.sankozi.logfilter.LogConsumer;
import org.sankozi.logfilter.LogEntry;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.google.common.base.Preconditions.*;

/**
 * Object that produces LogEntry objects for LogConsumer.
 */
public final class SocketHubAppenderLogProducer implements AutoCloseable{
    private final String host;
    private final int port;

    private final String name;

    private Thread producerThread;

    private volatile LogConsumer consumer;

    public SocketHubAppenderLogProducer(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.name = "SocketHubAppenderProducer-" + host + ":" + port;
    }

    public void start(LogConsumer consumer){
        checkState(consumer == null, "this producer has already started");
        this.consumer = checkNotNull(consumer, "consumer cannot be null");
        this.producerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                producerWork();
            }
        });
        this.producerThread.setName(name);
        this.producerThread.setDaemon(true);
        this.producerThread.start();
    }

    private void producerWork(){
        try {
            while(true){
                try (Socket socket = new Socket(host, port);
                     ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()))){
                    ois.readObject();
                } catch (IOException e) {
                    consumer.add(new LogEntry(Level.INFO, "logfilter.log4j", name + " has encountered io error: " + e.getMessage()));
                }
                Thread.sleep(5000);
            }
        } catch (InterruptedException ex){
            consumer.add(new LogEntry(Level.INFO, "logfilter.log4j", name + " has been closed"));
        } catch (ClassNotFoundException e) {
            consumer.add(new LogEntry(Level.ERROR, "logfilter.log4j", name + " has encountered error: " + e.getMessage()));
        }
    }

    @Override
    public void close() throws Exception {
        checkState(producerThread != null, "this producer is not running");
        producerThread.interrupt();
        try {
            producerThread.join(1000);
            if(producerThread.isAlive()){
                consumer.add(new LogEntry(Level.INFO, "logfilter.log4j", name + " has not ended in time"));
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
