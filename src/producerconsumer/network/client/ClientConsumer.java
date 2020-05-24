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
public class ClientConsumer extends Thread{
    
    JsonObject json;
    int wait;
    Socket socket;

    public ClientConsumer(JsonObject json, int wait, Socket socket){
        this.json = json;
        this.wait = wait;
        this.socket = socket;
    }
    
    @Override
    public void run(){
        String idC = json.get("id").getAsString();
        String operation = json.get("consume").getAsString();
        json = new JsonObject();
        json.add("action", new JsonPrimitive(ActionSignals.CONSUMED.toString()));
        // TODO: change use operation to obtain the real answer
        json.add("consumed", new JsonPrimitive(((Integer) new Random().nextInt()).toString()));
        json.add("id", new JsonPrimitive(idC));
        try{
            Thread.sleep(wait);
        } catch(InterruptedException e){
            System.out.println(e);
        }
        try {
            MessageManager.sendMessage(json, socket);
        } catch (IOException ex) {
            Logger.getLogger(ClientConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
