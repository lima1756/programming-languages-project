package producerconsumer.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class MessageManager {
    
    private int message;
    
    public static boolean sendMessage(JsonObject message, Socket connection) throws IOException {
        try {
            Writer out;
            out = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream(), "UTF8"));
            out.append(message.toString());
            out.append((char) 0);
            out.flush();
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static JsonObject readMessage(Socket conection) throws IOException {
        try {
            Reader in = new BufferedReader(new InputStreamReader(
                    conection.getInputStream(), "UTF8"
            ));

            int c;
            StringBuilder response = new StringBuilder();
            while ((c = in.read()) != 0) {
                response.append((char) c);
            }
            
            //System.out.println("Message: " + response.toString());

            JsonParser parser = new JsonParser();
            return (JsonObject) parser.parse(response.toString());

        } catch (JsonParseException ex) {
            System.out.println(ex);
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
        }
        return new JsonObject();
    }
}
