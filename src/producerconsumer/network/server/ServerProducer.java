package producerconsumer.network.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.net.Socket;
import producerconsumer.network.ActionSignals;
import producerconsumer.network.MessageManager;

public class ServerProducer {
    String id;
    Socket socket;

    public ServerProducer(String id, Socket socket){
        this.id = id;
        this.socket = socket;
    }

    public void produce() throws IOException{
        JsonObject json = new JsonObject();
        json.add("action", new JsonPrimitive(ActionSignals.PRODUCE.toString()));
        json.add("id", new JsonPrimitive(this.id));
        MessageManager.sendMessage(json, socket);
    }
    
}