package producerconsumer.network;

public enum ActionSignals {
    CLOSE("CLOSE"), 
    PRODUCER_OK("PRODUCER_OK"), 
    CONSUMER_OK("CONSUMER_OK"), 
    CONFIG("CONFIG"), 
    PRODUCED("PRODUCED"), 
    CONSUMED("CONSUMED"),
    CONSUME("CONSUME"),
    PRODUCE("PRODUCE");
    
    private String value;
    
    private ActionSignals(String value){
        this.value = value;
    }

    public static ActionSignals valueof(String action) {
        for (ActionSignals b : ActionSignals.values()) {
            if (b.toString().equals(action)) {
                return b;
            }
    	}
        return null;
    }
    
    @Override
    public String toString(){
        return this.value;
    }
}
