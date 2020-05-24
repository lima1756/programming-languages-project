package producerconsumer.network.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.net.Socket;
import producerconsumer.network.ActionSignals;
import producerconsumer.network.MessageManager;

public class ServerConsumer {
    String id;
    Socket socket;

    public ServerConsumer(String id, Socket socket){
        this.id = id;
        this.socket = socket;
    }

    public void consume(String data) throws IOException {
        JsonObject json = new JsonObject();
        System.out.println("Consumer "+socket.getInetAddress()+" - "+ id + " CONSUMED");
        json.add("action", new JsonPrimitive(ActionSignals.CONSUME.toString()));
        json.add("consume", new JsonPrimitive(data));
        json.add("id", new JsonPrimitive(this.id));
        MessageManager.sendMessage(json, socket);
    }


}