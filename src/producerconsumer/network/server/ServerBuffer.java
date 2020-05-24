
package producerconsumer.network.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import javax.swing.table.DefaultTableModel;
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
        DefaultTableModel model1 = (DefaultTableModel) gui.jTable1.getModel();
        String toReturn = buffer.take();
        try {
            model1.removeRow(0);
            this.completadas++;
            gui.labelTareasPendientes.setText(this.buffer.size() + "");
            gui.jProgressBar2.setValue(this.buffer.size());
            gui.labelTareasCompletadas.setText(this.completadas + "");
        } catch(Exception e){
            System.out.println(e);
        }
        
        return toReturn;
        
    }
    
    public void produce(String value) throws InterruptedException{
        try{
            DefaultTableModel model = (DefaultTableModel) gui.jTable1.getModel();
            System.out.println("Before producing: " + model.getRowCount());
            model.addRow(new Object[]{value});
            System.out.println("After producing: " + model.getRowCount());
        } catch(Exception ex){
            System.out.println(ex);
        }
        buffer.put(value);
        gui.jProgressBar2.setValue(buffer.size());
        gui.labelTareasPendientes.setText(this.buffer.size() + "");
        
        
    }

    public boolean isEmpty(){
        return this.buffer.isEmpty();
    }
    
}
