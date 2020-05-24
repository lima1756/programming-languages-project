package producerconsumer.inputAnalysis;

import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Syntactical implements Runnable {
    
    private static Syntactical INSTANCE;
    
    
    private BlockingQueue<Token> tokenStream;
    private Stack<Integer> stack;
    private static AnalyzerListener listener;
    
    
    private Syntactical(){
        tokenStream = new LinkedBlockingQueue<>();
        stack = new Stack<>();
    }
    
    public void addToken(Token token){
        tokenStream.add(token);
    }
    
    public static Syntactical getInstance(AnalyzerListener listener){
        INSTANCE = new Syntactical();
        Syntactical.listener = listener;
        return INSTANCE;
    }
    
    public static Syntactical getInstance(){
        if(INSTANCE==null){
            INSTANCE = new Syntactical();
        }
        Syntactical.listener = new AnalyzerListener() {
            @Override
            public void passedAnalyzer() {
                System.out.println("PASSED LEX AND SYNTAX");
            }

            @Override
            public void errorAnalyzer(Token token) {
                System.out.println("ERROR on token: " + token.getData());
            }
        };
        return INSTANCE;
    }

    @Override
    public void run() {
        Boolean error = false;
        try {
            Token token = tokenStream.take();
            if(token.getType() == TokenType.START_SYMBOL){
                startTransition();
            }
            else{
                sendError(token);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Syntactical.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(error){
            System.out.println("Error");
            
        }
    }
    
    private void startTransition() throws InterruptedException{
        stack.push(1);
        Token token = tokenStream.take();
        if(null == token.getType()){
            sendError(token);
        } else switch (token.getType()) {
            case WORD:
                wordTransition();
                break;
            case ERROR:
                break;
            default:
                sendError(token);
                break;
        }
    }
    
    private void wordTransition() throws InterruptedException{
        Token token = tokenStream.take();
        if(null == token.getType()){
            sendError(token);
        } else switch (token.getType()) {
            case NUMBER:
                numberTransition();
                break;
            case SUSBTITUTE:
                substitutionTransition();
                break;
            case START_SYMBOL:
                startTransition();
                break;
            case ERROR:
                break;
            default:
                sendError(token);
                break;
        }
    }
    
    private void numberTransition() throws InterruptedException{
        Token token = tokenStream.take();
        if(null == token.getType()){
            sendError(token);
        } else switch (token.getType()) {
            case NUMBER:
                numberTransition();
                break;
            case SUSBTITUTE:
                substitutionTransition();
                break;
            case START_SYMBOL:
                startTransition();
                break;
            case END_SYMBOL:
                endSymbolTransition();
                break;
            case ERROR:
                break;
            default:
                sendError(token);
                break;
        }
    }
    
    private void substitutionTransition() throws InterruptedException{
        Token token = tokenStream.take();
        if(null == token.getType()){
            sendError(token);
        } else switch (token.getType()) {
            case NUMBER:
                numberTransition();
                break;
            case SUSBTITUTE:
                substitutionTransition();
                break;
            case END_SYMBOL:
                endSymbolTransition();
                break;
            case START_SYMBOL:
                startTransition();
                break;
            case ERROR:
                break;
            default:
                sendError(token);
                break;
        }
    }
    
    private void endSymbolTransition() throws InterruptedException{
        stack.pop();
        Token token = tokenStream.take();
        if(null == token.getType() || (stack.empty() && token.getType()!= TokenType.EOF)){
            sendError(token);
        } else switch (token.getType()) {
            case NUMBER:
                numberTransition();
                break;
            case SUSBTITUTE:
                substitutionTransition();
                break;
            case END_SYMBOL:
                endSymbolTransition();
                break;
            case START_SYMBOL:
                startTransition();
                break;
            case EOF:
                if(stack.empty())
                {
                    listener.passedAnalyzer();
                }
                else{
                    sendError(token);
                }
                break;
            case ERROR:
                break;
            default:
                sendError(token);
                break;
        }
    }
    
    private void sendError(Token token){
        listener.errorAnalyzer(token);
        
    }
    
    public BlockingQueue getStream(){
        return this.tokenStream;
    }
    
    public static void main(String[] args) {
        Lexical l = Lexical.getInstance();
        Syntactical s = Syntactical.getInstance();
        l.prepare("(+ 1 (asdf 8 5 9 _N (+ 1 1)))", s.getStream());
        new Thread(s).start();
        new Thread(l).start();
    }
    
}
