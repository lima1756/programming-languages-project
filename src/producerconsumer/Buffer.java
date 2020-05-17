
package producerconsumer;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.Queue;

public class Buffer {
    
    //private char buffer = 0;
    public Queue<Character> theBuffer;
    private int size;
    
    Buffer(int size) {
        this.size = size;
        this.theBuffer = new LinkedList<>();
    }
    
    synchronized char consume() {
        char product = 0;
        
        if(this.theBuffer.isEmpty()) {
            try {
                wait(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        product = this.retrieveProduct();
        this.printBuffer();
        notify();
        
        return product;
    }
    
    synchronized void produce(char product) {
        
        if(bufferIsFull()) { //if buffer is full, then wait a second
            try {
                wait(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        this.addProduct(product);
        this.printBuffer();
        
        notify();
    }
    
    static int count = 1;
    synchronized static void print(String string) {
        System.out.print(count++ + " ");
        System.out.println(string);
    }
    
    boolean bufferIsFull() {
        return this.theBuffer.size() >= this.size;
    }
    
    Character addProduct(char product) {
        if(this.theBuffer.size() < this.size) {
            
            this.theBuffer.add(product);
            return product;
        }
        return null;
    }
    
    Character retrieveProduct() {
        if(!this.theBuffer.isEmpty())
            return this.theBuffer.remove();
        
        return null;
    }
    
    void printBuffer() {
        String output = "[";
        for(Character c : this.theBuffer) {
            output += c + ", ";
        }
        output += "]";
        System.out.println(output);
        
    }
    
}
