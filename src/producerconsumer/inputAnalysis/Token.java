package producerconsumer.inputAnalysis;

public class Token {
        private final TokenType type;
        private final String data;
        private final int start;
        private final int end;
        
        public Token(TokenType type, String data, int start, int end){
            this.type = type;
            this.data = data;
            this.start = start;
            this.end = end;
        }

    public TokenType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
        
        
        
}
