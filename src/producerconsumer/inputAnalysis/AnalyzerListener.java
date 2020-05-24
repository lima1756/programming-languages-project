package producerconsumer.inputAnalysis;


public interface AnalyzerListener {
    void passedAnalyzer();
    void errorAnalyzer(Token token);
}
