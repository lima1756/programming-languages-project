package producerconsumer.network.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import producerconsumer.network.ActionSignals;
import producerconsumer.network.MessageManager;

public class ServerProducer {
    String id;
    Socket socket;
    ArrayList<String> dictionary;
    

    public ServerProducer(String id, Socket socket, ArrayList<String> dictionary){
        this.id = id;
        this.socket = socket;
        this.dictionary = dictionary;
    }

    public void produce() throws IOException{
        JsonObject json = new JsonObject();
        System.out.println("Consumer "+socket.getInetAddress()+" - "+ id + " TO PRODUCE");
        json.add("action", new JsonPrimitive(ActionSignals.PRODUCE.toString()));
        json.add("id", new JsonPrimitive(this.id));
        json.add("raw_op", new JsonPrimitive(dictionary.get(new Random(System.currentTimeMillis()).nextInt(dictionary.size()))));
        MessageManager.sendMessage(json, socket);
    }
}