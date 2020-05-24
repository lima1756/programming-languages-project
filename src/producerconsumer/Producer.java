
package producerconsumer;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Producer extends Thread {
    Buffer buffer;
    private int productorEspera;
    private boolean alive;
    private ArrayList<String> dictionary;
    Integer max, min;
    
    Producer(Buffer buffer, int productorEspera, ArrayList<String> dictionary, int min, int max) {
        this.buffer = buffer;
        this.productorEspera = productorEspera;
        this.alive = false;
        this.dictionary = dictionary;
        this.max = max;
        this.min = min;
    }
    
    @Override
    public void run() {
        this.alive = true;
        System.out.println("Running Producer...");
        Random r = new Random(System.currentTimeMillis());
        String product;
        
        while(alive) {
            product = dictionary.get(r.nextInt(dictionary.size()));
            while(product.contains("_N")){
                product = product.replaceFirst("_N", ((Integer)(min + r.nextInt(max-min))).toString());
            }
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
