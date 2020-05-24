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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import producerconsumer.GUIDesignFrame;
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
    private int waitConsumers, waitProducers, min, max;
    private ArrayList<String> dictionary;
    private GUIDesignFrame gui;
    private ArrayList<ServerProducer> tempProducers;

    public Connection(Socket socket, ConcurrentHashMap<InetAddress, Socket> socketsMap,
            BlockingQueue<ServerProducer> idleProducers, BlockingQueue<ServerConsumer> idleConsumers,
            int consumers, int producers, ServerBuffer buffer, int waitProducers, int waitConsumers,
            int min, int max, ArrayList<String> dictionary, GUIDesignFrame gui) {
        this.socket = socket;
        this.socketsMap = socketsMap;
        this.idleProducers = idleProducers;
        this.idleConsumers = idleConsumers;
        this.consumers = consumers;
        this.producers = producers;
        this.waitProducers = waitProducers;
        this.waitConsumers = waitConsumers;
        this.buffer = buffer;
        this.min = min;
        this.max = max;
        this.dictionary = dictionary;
        this.gui = gui;
        this.tempProducers = new ArrayList<>();
    }

    @Override
    public void run() {
        JsonObject json = new JsonObject();
        System.out.println("consumers: " + ((Integer)this.consumers));
        System.out.println("producers: " + ((Integer)this.producers));
        json.add("action", new JsonPrimitive("CONFIG"));
        json.add("producers", new JsonPrimitive(this.producers));
        json.add("consumers", new JsonPrimitive(this.consumers));
        json.add("waitProducers", new JsonPrimitive(this.consumers));
        json.add("waitConsumers", new JsonPrimitive(this.consumers));
        json.add("min", new JsonPrimitive(this.min));
        json.add("max", new JsonPrimitive(this.max));
        try {
            MessageManager.sendMessage(json, socket);
            while (socket.isConnected() && !socket.isClosed()) {
                try {
                    json = MessageManager.readMessage(socket);
                    String action = json.get("action").getAsString();
                    System.out.println(action);
                    switch (ActionSignals.valueof(action)) {
                        case PRODUCER_OK:
                            tempProducers.add(new ServerProducer(json.get("id").getAsString(), socket, dictionary));
                            if(tempProducers.size() == producers){
                                while(tempProducers.size()>0){
                                    idleProducers.add(tempProducers.get(0));
                                    tempProducers.remove(0);
                                }
                            }
                            
                            break;
                        case CONSUMER_OK:
                            idleConsumers.add(new ServerConsumer(json.get("id").getAsString(), socket));
                            break;
                        case PRODUCED:
                            String product = json.get("produced").getAsString();
                            String idP = json.get("id").getAsString();
                            System.out.println("producer "+ socket.getInetAddress().toString() + " - " + idP + ": produced: " + product);
                            
                            
                            
                            buffer.produce(product);
                            idleProducers.add(new ServerProducer(idP, socket, dictionary));
                            break;
                        case CONSUMED:
                            String idC = json.get("id").getAsString();
                            String answer = json.get("consumed").getAsString();
                            String operation = json.get("operation").getAsString();
                            try{
                                DefaultTableModel model2 = (DefaultTableModel) gui.jTable2.getModel();
                                model2.addRow(new Object[]{operation, answer});
                            } catch(Exception e){
                                System.out.println(e);
                            }
                            //System.out.println("consumer " + socket.getInetAddress().toString() + " - " + idC + ": consumed: " + answer);
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
                    System.out.println(ex);
                }
            }
        } catch (NullPointerException ex) {
            System.out.println(json);
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            this.logOut();
            this.closeSocket();
            
        } catch (IOException ex) {
            System.out.println(json);
            System.out.println(ex);
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
    
    /*public static void main(String[] args) {
        Server s = new Server();
        s.listen();
        Scanner keyboard = new Scanner(System.in);
        String input = keyboard.nextLine();
        s.run(5, 2, 1); 
        
    }*/
}
