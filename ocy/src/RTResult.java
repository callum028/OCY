public class RTResult {
    private Value value = null;
    private Error error = null;
    private boolean output = false;

    public Value register(RTResult res) {
        if(res.getError() != null) {
            this.error = res.getError();
        }

        if(res.getOutput()) {
            this.Output();
        }

        return res.getValue();
    }

    public RTResult success(Value value) {
        this.value = value;
        return this;
    }

    public RTResult failure(Error error) {
        this.error = error;
        return this;
    }

    public Value getValue() {
        return value;
    }

    public Error getError() {
        return error;
    }

    public void Output() {
        this.output = true;
    }

    public boolean getOutput() {
        return this.output;
    }
}
