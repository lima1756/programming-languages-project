/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package producerconsumer.network.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import producerconsumer.network.ActionSignals;
import producerconsumer.network.MessageManager;


public class Client extends Thread{
    
    private Socket socket;
    private int consumers;
    private int producers;
    private boolean alive;
    private int producerWait;
    private int consumerWait;
    private int min, max;
    private Semaphore semaphore;
    
    public Client(String IP){
        try{
            this.socket = new Socket(IP, 8081);
        } catch(IOException e){
            System.out.println(e);
        }
        this.consumers = 0;
        this.producers = 0;
        this.alive = false;
    }
    
    @Override
    public void run() {
        this.alive = true;
        try{
            JsonObject json = MessageManager.readMessage(socket);
            String action = json.get("action").getAsString();
            if(action.equals(ActionSignals.CONFIG.toString())){
                System.out.println("entered client config");
                consumers = json.get("consumers").getAsInt();
                producers = json.get("producers").getAsInt();
                producerWait = json.get("waitProducers").getAsInt();
                consumerWait = json.get("waitConsumers").getAsInt();
                min = json.get("min").getAsInt();
                max = json.get("max").getAsInt();
                for(Integer i = 0; i < consumers; i++){
                    json = new JsonObject();
                    json.add("action", new JsonPrimitive(ActionSignals.CONSUMER_OK.toString()));
                    json.add("id", new JsonPrimitive(i.toString()));
                    MessageManager.sendMessage(json, socket);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        System.out.println( ex);
                    }
                }
                for(Integer i = 0; i < producers; i++){
                    json = new JsonObject();
                    json.add("action", new JsonPrimitive(ActionSignals.PRODUCER_OK.toString()));
                    json.add("id", new JsonPrimitive(i.toString()));
                    MessageManager.sendMessage(json, socket);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                }
                while(alive) {
                    try{
                        json = MessageManager.readMessage(socket);
                        action = json.get("action").getAsString();
                        switch (ActionSignals.valueof(action)) {
                            case PRODUCE:
                                new ClientProducer(json, producerWait, socket, min, max).start();
                                break;
                            case CONSUME:
                                new ClientConsumer(json, consumerWait, socket).start();
                                break;
                        }
                    } catch(IOException | ClassCastException e) {
                        System.out.println(e);
                    }
                } 
            }
           
        } catch(IOException  ex ) {
                System.out.println("Connection finished");
        }
    }

    public String getIP() {
        return socket.getInetAddress().toString();
    }

    public void kill(){
        this.alive = false;
    }
    
    public static void main(String[] args) {
        Client c = new Client("192.168.1.65");
        c.run();
    }
    
    
    
}