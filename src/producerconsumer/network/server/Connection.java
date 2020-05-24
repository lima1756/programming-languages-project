package producerconsumer.network.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParseException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import producerconsumer.network.ActionSignals;
import producerconsumer.network.MessageManager;

public class Connection extends Thread {

    private final Socket socket;
    private final ConcurrentHashMap<InetAddress, Socket> socketsMap;
    private final Queue<ServerProducer> idleProducers;
    private final Queue<ServerConsumer> idleConsumers;
    private int consumers;
    private int producers;
    private ServerBuffer buffer;

    public Connection(Socket socket, ConcurrentHashMap<InetAddress, Socket> socketsMap,
            BlockingQueue<ServerProducer> idleProducers, BlockingQueue<ServerConsumer> idleConsumers,
            int consumers, int producers, ServerBuffer buffer) {
        this.socket = socket;
        this.socketsMap = socketsMap;
        this.idleProducers = idleProducers;
        this.idleConsumers = idleConsumers;
        this.consumers = consumers;
        this.producers = producers;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        JsonObject json = new JsonObject();
        System.out.println("consumers: " + ((Integer)this.consumers));
        System.out.println("producers: " + ((Integer)this.producers));
        json.add("action", new JsonPrimitive("CONFIG"));
        json.add("producers", new JsonPrimitive(this.producers));
        json.add("consumers", new JsonPrimitive(this.consumers));
        try {
            MessageManager.sendMessage(json, socket);
            while (socket.isConnected() && !socket.isClosed()) {
                try {
                    json = MessageManager.readMessage(socket);
                    String action = json.get("action").getAsString();
                    System.out.println(action);
                    switch (ActionSignals.valueof(action)) {
                        case PRODUCER_OK:
                            idleProducers.add(new ServerProducer(json.get("id").getAsString(), socket));
                            break;
                        case CONSUMER_OK:
                            idleConsumers.add(new ServerConsumer(json.get("id").getAsString(), socket));
                            break;
                        case PRODUCED:
                            String product = json.get("produced").getAsString();
                            String idP = json.get("id").getAsString();
                            System.out.println("producer "+ socket.getInetAddress().toString() + " - " + idP + ": produced: " + product);
                            // TODO: add to produced data
                            buffer.produce(product);
                            idleProducers.add(new ServerProducer(idP, socket));
                            break;
                        case CONSUMED:
                            String idC = json.get("id").getAsString();
                            String answer = json.get("consumed").getAsString();
                            System.out.println("consumer " + socket.getInetAddress().toString() + " - " + idC + ": consumed: " + answer);
                            // TODO: add to consumed data
                            idleConsumers.add(new ServerConsumer(idC, socket));
                            break;
                        case CLOSE:
                            logOut();
                            break;
                        default:
                            break;
                    }
                } catch (JsonParseException ex) {
                    System.out.println(ex.getMessage());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (NullPointerException ex) {
            this.logOut();
            this.closeSocket();
        } catch (IOException ex) {
            this.logOut();
            this.closeSocket();
        }

    }

    private void logOut() {
        this.closeSocket();
    }

    private void closeSocket() {
        try {
            socketsMap.remove(this.socket.getInetAddress());
            this.socket.close();
            this.finalize();
            System.out.println("Socket cerrado: " + this.socket.getInetAddress());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        Server s = new Server();
        s.listen();
        Scanner keyboard = new Scanner(System.in);
        String input = keyboard.nextLine();
        s.run(5, 2, 1); 
        
    }
}
