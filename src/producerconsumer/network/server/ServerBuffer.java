
package producerconsumer.network.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import producerconsumer.GUIDesignFrame;

public class ServerBuffer {
    
    private BlockingQueue<String> buffer;
    private GUIDesignFrame gui;
    private int completadas;
    
    
    public ServerBuffer(int size, GUIDesignFrame gui) {
        this.buffer = new LinkedBlockingDeque<>(size);
        this.gui = gui;
    }
    
    public String consume()  throws InterruptedException{
        return buffer.take();
        
    }
    
    public void produce(String value) throws InterruptedException{
        
        buffer.put(value);
        
    }

    public boolean isEmpty(){
        return this.buffer.isEmpty();
    }
    
}
