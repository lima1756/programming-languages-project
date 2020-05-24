
package producerconsumer.network.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerBuffer {
    
    private BlockingQueue<String> buffer;
    
    public ServerBuffer(int size) {
        this.buffer = new LinkedBlockingDeque<>(size);
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
