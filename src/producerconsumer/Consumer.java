
package producerconsumer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer extends Thread {
    Buffer buffer;
    private int consumidorEspera;
    private boolean alive;
    
    Consumer(Buffer buffer, int consumidorEspera) {
        this.buffer = buffer;
        this.consumidorEspera = consumidorEspera;
        this.alive = false;
    }
    
    @Override
    public void run() {
        this.alive = true;
        System.out.println("Running Consumer...");
        String product;
        
        while(alive) {
            product = this.buffer.consume();
            
            //System.out.println("Consumer consumed: " + product);
            Buffer.print("Consumer consumed: " + product);
            
            try {
                Thread.sleep(this.consumidorEspera);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }
    
    public void kill() {
        this.alive = false;
    }
}
