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

/**
 *
 * @author luisi
 */
public class ClientProducer extends Thread{
    
    JsonObject json;
    int wait;
    Socket socket;
    int min, max;
    
    public ClientProducer(JsonObject json, int wait, Socket socket, int min, int max){
        this.json = json;
        this.wait = wait;
        this.socket = socket;
        this.min = min;
        this.max = max;
    }
    
    @Override
    public void run(){
        String id = json.get("id").getAsString();
        System.out.println("producer produced: " + json);
        String operation = json.get("raw_op").getAsString();
        while(operation.contains("_N")){
            operation = operation.replaceFirst("_N", ((Double)(min + new Random().nextDouble()*(max-min))).toString());
        }
        json = new JsonObject();
        json.add("action", new JsonPrimitive(ActionSignals.PRODUCED.toString()));
        json.add("produced", new JsonPrimitive(operation));
        json.add("id", new JsonPrimitive(id));
        System.out.println("producer produced: " + json);
        try{
            Thread.sleep(wait);
        } catch(InterruptedException e){
            System.out.println(e);
        }
        try {
            MessageManager.sendMessage(json, socket);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
