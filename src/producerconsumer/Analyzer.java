/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package producerconsumer;

import java.util.ArrayList;
public class Analyzer {
    private boolean result;
    
    public Analyzer(String operation){
        this.result = init(operation.trim());
    }
    
    private static boolean init(String preorder){
        if(preorder.charAt(0) == '(' && 
           preorder.charAt(preorder.length()-1) == ')')
            return parentheses(preorder);
        
        return false;
    }
    
    private static boolean countParentheses(String preorder){
        int opening=0, closing=0;
        
        for(int i=0; i<preorder.length(); i++){
            if(preorder.charAt(i) == '(') opening++;
            else if((preorder.charAt(i) == ')')) closing++;
        }
        
        return opening == closing;
    }
    
    public static boolean checkForMissingOperator(String operation){
        //System.out.println("to check: "+operation);
        String []parts = operation.split("\\s+");
        //System.out.println("operador: "+parts[0]+"\tlength: "+parts[0].length());
        return !operator(parts[0]) && operation.length() > 1;
    }
    
    
    // Prueba: (+ 1 (- 1 (/ 3 2)))
    // Prueba: (+ (- 4 2) 5 7 6 (/ 2 4) 1)
    private static boolean parentheses(String preorder){
        
        if(!countParentheses(preorder)) return false;
        
        String split[] = preorder.split("[\\(\\)]");
        
        ArrayList<Integer> indexesToRemove = new ArrayList<>();

        for(int i=2; i<split.length; i++){
                if(checkForMissingOperator(split[i].trim())){
                    //System.out.println("Antes: "+split[1]);
                    split[1] += split[i].trim()+" ";
                    indexesToRemove.add(i);
                    //System.out.println("Despues: "+split[1]+"\n");
                    //System.out.println();
                }
            
        }

        ArrayList<String> newSplit = new ArrayList<>();
        
        for(int i=1, j=0; i<split.length; i++){
            //System.out.println("previous array: "+split[i]);
            if(indexesToRemove.contains(i) || split[i].equals(" ")) continue;
            newSplit.add(split[i]);
            //System.out.println("new array: "+newSplit.get(j++));
        }
        
        for(int i=0; i<newSplit.size(); i++) 
            if(!operation(newSplit.get(i))) 
                return false;
                        
       
       
        return true;
    }
    
    private static boolean operation(String operation){
        System.out.println("operation: " + operation);
        String []parts = operation.split("\\s+");
        
        
        if(parts.length >= 3){
            if(!operator(parts[0])){
                return false;
            }

            for(int i=1; i<parts.length; i++){
                if(!variable(parts[i])) return false;
            }
            
            return true;
            
        } else if(parts.length == 2){
            return (operator(parts[0]) && variable(parts[1]));
        }        
        
        return false;
    }
    
    private static boolean variable(String v){
        if(v.charAt(0) == '('){
            if(!parentheses(v.substring(0, v.length()))){
                return false;
            }
        } else if(!numType(v)){
            return false;
        }
        
        return true;
    }
    
    private static boolean numType(String v){
        char c = v.charAt(0);
        boolean canBeFractionOrDecimal = true;
        
        if(v.equals("_N")) return true;
        
        if(!isNumber(c) && 
                c != '-') 
            return false;
        
        if(!isNumber(v.charAt(v.length()-1)) && 
                v.charAt(v.length()-1) != 'i') 
            return false;
        
        for(int i=1; i<v.length()-1; i++){
            c = v.charAt(i);
            if(isLetter(c)) return false;
            
            if(!isNumber(c)){
                if(canBeFractionOrDecimal){
                    switch(c){
                        case '/':
                        case '.':
                            canBeFractionOrDecimal = false;
                            break;

                        default:
                            return false;
                    }
                } else {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private static boolean operator(String operator){
        char o;
        if(operator.length() == 0) return false;

        for(int i=0; i<operator.length(); i++){
            o = operator.charAt(i);
            if(!(isLetter(o) || 
                    o == '+' || 
                    o == '-' || 
                    o == '*' || 
                    o == '/' || 
                    o == '%' || 
                    o == '!' || 
                    o == '&' || 
                    o == '$')){
                
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean isLetter(char l){
        return ((l >= 65 && l <= 90) || 
                (l >= 97 && l <= 122));
    }
    
    private static boolean isNumber(char n){
        return (n >= 48 && n <= 57);
    }

    
    
    public boolean getResult() {
        return result;
    }

    public void setResult(String operation) {
        this.result = init(operation.trim());
    }
    
}