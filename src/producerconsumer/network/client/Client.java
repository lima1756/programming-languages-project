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
import java.util.logging.Level;
import java.util.logging.Logger;
import producerconsumer.network.ActionSignals;
import producerconsumer.network.MessageManager;

public class Client extends Thread{
    
    private Socket socket;
    private int consumers;
    private int producers;
    
    public Client(String IP){
        try{
            this.socket = new Socket(IP, 8081);
        } catch(IOException e){
            System.out.println(e);
        }
    }
    
    @Override
    public void run() {
        try{
            JsonObject json = MessageManager.readMessage(socket);
            String action = json.get("action").getAsString();
            if(action.equals(ActionSignals.CONFIG.toString())){
                System.out.println("entered client config");
                consumers = json.get("consumers").getAsInt();
                producers = json.get("producers").getAsInt();
                for(Integer i = 0; i < producers; i++){
                    json = new JsonObject();
                    json.add("action", new JsonPrimitive(ActionSignals.PRODUCER_OK.toString()));
                    json.add("id", new JsonPrimitive(i.toString()));
                    MessageManager.sendMessage(json, socket);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                for(Integer i = 0; i < consumers; i++){
                    json = new JsonObject();
                    json.add("action", new JsonPrimitive(ActionSignals.CONSUMER_OK.toString()));
                    json.add("id", new JsonPrimitive(i.toString()));
                    MessageManager.sendMessage(json, socket);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            while(true) {
                json = MessageManager.readMessage(socket);
                action = json.get("action").getAsString();
                switch (ActionSignals.valueof(action)) {
                    case PRODUCE:
                        String id = json.get("id").getAsString();
                        json = new JsonObject();
                        json.add("action", new JsonPrimitive(ActionSignals.PRODUCED.toString()));
                        // TODO: change to real operation
                        json.add("produced", new JsonPrimitive("(+ "+ new Random().nextInt()+" 1)"));
                        json.add("id", new JsonPrimitive(id));
                        MessageManager.sendMessage(json, socket);
                        break;
                    case CONSUME:
                        String idC = json.get("id").getAsString();
                        String operation = json.get("consume").getAsString();
                        json = new JsonObject();
                        json.add("action", new JsonPrimitive(ActionSignals.CONSUMED.toString()));
                        // TODO: change use operation to obtain the real answer
                        json.add("consumed", new JsonPrimitive(((Integer) new Random().nextInt()).toString()));
                        json.add("id", new JsonPrimitive(idC));
                        MessageManager.sendMessage(json, socket);
                        break;
                }
            }
        } catch(IOException ex) {
                System.out.println("Connection finished");
        }
    }
    
    public static void main(String[] args) {
        Client c = new Client("192.168.1.65");
        c.run();
    }
    
    
    
}