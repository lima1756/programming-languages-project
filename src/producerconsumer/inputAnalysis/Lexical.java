package producerconsumer.inputAnalysis;

import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexical implements Runnable {
    
    private static Lexical INSTANCE;
    
    // Regular expressions
    private final Pattern regexStartSymbol = Pattern.compile("^\\(");
    private final Pattern regexEndSymbol = Pattern.compile("^\\)");
    private final Pattern regexWord = Pattern.compile("^^(\\+|-|\\*|\\/|!|%|\\$|&|\\^|[a-zA-Z])+(?![0-9])");
    private final Pattern regexSubstitution = Pattern.compile("^_N");
    private final Pattern regexNumber = Pattern.compile("^(-)?([0-9]+\\/[0-9]*[1-9][0-9]*|[0-9]+\\.[1-9]+|[0-9]+\\+[1-9]+i|[0-9]+)");
    private final Pattern regexWhiteSpaces = Pattern.compile("^ +");
    private final Pattern regexEscapeError = Pattern.compile("(\\)|\\(| )+");

    private String input;
    private BlockingQueue<Token> tokenStream;

    
    public void prepare(String input, BlockingQueue<Token> stream){
        this.input = input;
        this.tokenStream = stream;
    }
    
    
    private Lexical() {
    }
    
    public static Lexical getInstance() {
        if(INSTANCE == null){
            INSTANCE = new Lexical();
        }
        return INSTANCE;
    }

    @Override
    public void run() {
        int length = 0;
        String data = input.substring(0);
        boolean error = false;
        while(data.length() > 0){
            Matcher matcher;
            if((matcher = regexStartSymbol.matcher(data))!=null && matcher.find()){
                tokenStream.add(new Token(TokenType.START_SYMBOL, 
                        data.substring(matcher.start(), matcher.end()), 
                        length+matcher.start(), 
                        length+matcher.end())
                );
            }
            else if((matcher = regexEndSymbol.matcher(data))!=null && matcher.find()){
                tokenStream.add(new Token(TokenType.END_SYMBOL, 
                        data.substring(matcher.start(), matcher.end()), 
                        length+matcher.start(), 
                        length+matcher.end())
                );
            }
            else if((matcher = regexWord.matcher(data))!=null && matcher.find()){
                tokenStream.add(new Token(TokenType.WORD, 
                        data.substring(matcher.start(), matcher.end()), 
                        length+matcher.start(), 
                        length+matcher.end())
                );
            }
            else if((matcher = regexSubstitution.matcher(data))!=null && matcher.find()){
                tokenStream.add(new Token(TokenType.SUSBTITUTE, 
                        data.substring(matcher.start(), matcher.end()), 
                        length+matcher.start(), 
                        length+matcher.end())
                );
            }
            else if((matcher = regexNumber.matcher(data))!=null && matcher.find()){
                tokenStream.add(new Token(TokenType.NUMBER, 
                        data.substring(matcher.start(), matcher.end()), 
                        length+matcher.start(), 
                        length+matcher.end())
                );
            }
            else if((matcher = regexWhiteSpaces.matcher(data))!=null && matcher.find()){
            }
            else{
                error = true;
                System.out.println("LEX ERROR");
                tokenStream.add(new Token(TokenType.ERROR, 
                    "", 
                    length, 
                    length
                ));
                matcher = regexEscapeError.matcher(data);
                matcher.find();
                length += matcher.start();
                data = data.substring(matcher.start());
                continue;
            }
            
            length += matcher.end();
            data = data.substring(matcher.end());
        }
        if(!error){
            tokenStream.add(new Token(TokenType.EOF, 
                "", 
                length, 
                length
            ));
        }
    }
    
    public static void main(String[] args) {
        Lexical l = Lexical.getInstance();
        Syntactical s = Syntactical.getInstance();
        l.prepare("(mod  _N    (* -95    _N   5 -15 _N   (+ 1 1)))", s.getStream());
        new Thread(s).start();
        new Thread(l).start();
    }
    
}
