/*
 * 
 * 
 * 
 */
package producerconsumer;
/**
 *
 * @author ODB
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import javax.swing.JOptionPane;
import kawa.standard.Scheme;

public class Evaluator {
    private Scheme scm;
    private boolean schemeWorks;
    private Process scheme;
    private BufferedReader stdInput;
    private BufferedWriter stdOutput;
    
    
    public Evaluator(){
        if(!(this.schemeWorks = this.runScheme())){
            //JOptionPane.showMessageDialog(null, "Scheme no ha podido iniciarse, se usará kawa");
            this.scm = new Scheme();
            //System.out.println("-Evaluator: I'm using Kawa");
        }
        //else System.out.println("-Evaluator: Scheme is running");
        
    }
    public String eval(String s){
        if(this.schemeWorks){
            try{
                stdOutput.write(s);
                stdOutput.flush();
                return stdInput.readLine();
            }
            catch(Exception e){
                e.printStackTrace();
                return "Ocurrió un error durante la evaluación dentro de scheme";
            }
        }
        else{
            try{
                return scm.eval(s).toString();
            }
            catch(Throwable e){
                e.printStackTrace();
                return "Ocurrió un error durante la evaluación con Kawa";    
            } 
        }
    }
    public static void main(String [] args)throws Throwable{       
        Evaluator e = new Evaluator();
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("Esperando input");
            System.out.println(e.eval(sc.nextLine()));   
        }
    }
    private boolean runScheme(){
        try{
            //Checamos que sistema operativo está corriendo. Para mac:
            if(System.getProperty("os.name").startsWith("Mac")){
                //fuerza bruta para probar con todas las variables path de mac y arrancar scheme
                BufferedReader br = new BufferedReader(new FileReader("/etc/paths")); 
                for(String line; (line = br.readLine()) != null; ) {
                    try{
                        System.out.println(line);
                        this.scheme = Runtime.getRuntime().exec(line+"/scheme -q");
                        break;
                    }
                    catch(Exception e){}
                }
                if(this.scheme == null)
                    return false;
            }
            else{
                //para windows
                this.scheme = Runtime.getRuntime().exec("scheme -q");
            }
            //Arrancamos el input y output para el proceso que acabamos de instanciar
            stdInput = new BufferedReader(new InputStreamReader(scheme.getInputStream()));
            stdOutput = new BufferedWriter(new OutputStreamWriter(scheme.getOutputStream()));
        }
        catch(Exception e){
            //e.printStackTrace();
            return false;
        }
        return true;
    }
}
