package org.sankozi.jlogfilter.log4j;

import com.google.common.base.Joiner;
import org.apache.log4j.spi.LoggingEvent;
import org.sankozi.jlogfilter.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Objects;

import static com.google.common.base.Preconditions.*;

/**
 * Object that produces LogEntry objects for LogConsumer.
 */
public final class SocketHubAppenderLogProducer implements LogProducer {
    private final static Joiner STACKTRACE_JOINER = Joiner.on(" \n").skipNulls();
    private final static  String[] EMPTY_STRING_ARR = new String[]{};

    private final String host;
    private final int port;

    private final String name;

    private final LogEntryFactory lef;

    private Thread producerThread;

    private volatile LogConsumer consumer;

    public SocketHubAppenderLogProducer(String host, int port, LogEntryFactory lef) throws IOException {
        this.host = host;
        this.port = port;
        this.name = "SocketHubAppenderProducer-" + host + ":" + port;
        this.lef = lef;
    }

    public void start(LogConsumer consumer){
        checkState(consumer != null, "this producer has already started");
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
                    while(true){
                        LoggingEvent le = (LoggingEvent) ois.readObject();
                        Level level = Level.valueOf(le.getLevel().toString());
                        LogEntry newEntry = lef.level(level)
                           .category(le.getLoggerName())
                           .message(Objects.toString(le.getMessage()))
                           .stacktrace(le.getThrowableStrRep() == null ? EMPTY_STRING_ARR : le.getThrowableStrRep())
                           .create();
                        consumer.add(newEntry);
                    }
                } catch (IOException e) {
                    consumer.add(lef.level(Level.INFO)
                            .category("jlogfilter.log4j")
                            .message(name + " has encountered io error: " + e.getMessage())
                            .stacktrace(EMPTY_STRING_ARR)
                            .create());
                }
                Thread.sleep(5000);
            }
        } catch (InterruptedException ex){
            consumer.add(lef.level(Level.WARN)
                    .category("jlogfilter.log4j")
                    .message( name + " has been closed")
                    .stacktrace(EMPTY_STRING_ARR)
                    .create());
        } catch (Exception ex) {
            consumer.add(lef.level(Level.WARN)
                    .category("jlogfilter.log4j")
                    .message(name + " has encountered error: " + ex.getMessage())
                    .stacktrace(EMPTY_STRING_ARR)
                    .create());
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void close() throws Exception {
        checkState(producerThread != null, "this producer is not running");
        producerThread.interrupt();
        try {
            producerThread.join(1000);
            if(producerThread.isAlive()){
                consumer.add(lef.level(Level.INFO)
                        .category("jlogfilter.log4j")
                        .message(name + " has not ended in time")
                        .stacktrace(EMPTY_STRING_ARR)
                        .create());
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
