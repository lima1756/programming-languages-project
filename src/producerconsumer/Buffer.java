
package producerconsumer;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;

public class Buffer {
    
    //private char buffer = 0;
    public Queue<String> theBuffer;
    private int size, completadas;
    private int productorEspera, consumidorEspera;
    private GUIDesignFrame gui;
    Evaluator ev = new Evaluator();
    
    Buffer(int size, int productorEspera, int consumidorEspera, GUIDesignFrame gui) {
        this.size = size;
        this.completadas = 0;
        this.productorEspera = productorEspera;
        this.consumidorEspera = consumidorEspera;
        this.gui = gui;
        this.theBuffer = new LinkedList<>();
    }
    
    synchronized String consume() {
        String product = "";
        
        while(this.theBuffer.isEmpty()) {
            try {
                wait(this.productorEspera);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
        
        product = this.retrieveProduct();
        this.completadas++;
        try{
            DefaultTableModel model1 = (DefaultTableModel) gui.jTable1.getModel();
            model1.removeRow(0);
        } catch (Exception e){
            System.out.println(e);
        }
        gui.labelTareasPendientes.setText(this.theBuffer.size() + "");
        
        String output = ev.eval(product);
        try{
            DefaultTableModel model2 = (DefaultTableModel) gui.jTable2.getModel();
            model2.addRow(new Object[]{product, output});
        } catch (Exception e){
            System.out.println(e);
        }
        gui.jProgressBar2.setValue(this.theBuffer.size());
        gui.labelTareasCompletadas.setText(this.completadas + "");
        this.printBuffer();
        notify();
        
        return product+" = "+output;
    }
    
    synchronized void produce(String product) {
        
        while(bufferIsFull()) {
            try {
                wait(this.consumidorEspera);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
        
        this.addProduct(product);
        DefaultTableModel model = (DefaultTableModel) gui.jTable1.getModel();
        model.addRow(new Object[]{product});
        gui.jProgressBar2.setValue(this.theBuffer.size());
        gui.labelTareasPendientes.setText(this.theBuffer.size() + "");
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
    
    String addProduct(String product) {
        if(this.theBuffer.size() < this.size) {
            
            this.theBuffer.add(product);
            return product;
        }
        return null;
    }
    
    String retrieveProduct() {
        if(!this.theBuffer.isEmpty())
            return this.theBuffer.remove();
        
        return null;
    }
    
    void printBuffer() {
        String output = "[";
        for(String c : this.theBuffer) {
            output += c + ", ";
        }
        output += "]";
        System.out.println(output);
        
    }
    
}
