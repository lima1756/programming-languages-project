/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package producerconsumer;
/**
 *
 * @author ODB
 */

import gnu.math.IntFraction;
import gnu.math.IntNum;
import java.util.Scanner;
import kawa.standard.Scheme;

public class Evaluator {
    private Scheme scm;
    public Evaluator(){
        scm = new Scheme();
    }
    public Double eval(String s) throws Throwable{
        Object o = scm.eval(s);
        if(o.getClass().getName().equals("gnu.math.IntFraction")){
            return ((IntFraction)o).doubleValue();
        }
        else{
            return ((IntNum)o).reValue();
        }
        
    }
    public static void main(String [] args)throws Throwable{
        Evaluator e = new Evaluator();
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println(e.eval(sc.nextLine()));   
        }
    }
    
}
