
package producerconsumer;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Producer extends Thread {
    Buffer buffer;
    private int productorEspera;
    private boolean alive;
    
    Producer(Buffer buffer, int productorEspera) {
        this.buffer = buffer;
        this.productorEspera = productorEspera;
        this.alive = true;
    }
    
    @Override
    public void run() {
        System.out.println("Running Producer...");
        //TODO: Ivan, aqui esta "la materia prima" para producir
        String products = "AEIOU";
        Random r = new Random(System.currentTimeMillis());
        char product;
        
        while(alive) {
            //TODO: Ivan, aqui se genera el producto en base a la materia prima0
            product = products.charAt(r.nextInt(5));
            this.buffer.produce(product);
            //System.out.println("Producer produced: " + product);
            Buffer.print("Producer produced: " + product);
            
            try {
                Thread.sleep(this.productorEspera);
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void kill() {
        this.alive = false;
    }
    
}
