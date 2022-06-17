public class PipelineRegister {
    String operation = "";
    int rs = 0;
    int rd = 0;
    int rt = 0;
    int immediate = 0;
    String pipeline_rep = operation;

    public PipelineRegister(String operation, int rs, int rd, 
    int rt, int immediate, String pipeline_rep) {
        this.operation = operation;
        this.rs = rs;
        this.rd = rd;
        this.rt = rt;
        this.immediate = immediate;
        this.pipeline_rep = pipeline_rep;
    }

    public PipelineRegister(String pipeline_rep) {
        this.pipeline_rep = pipeline_rep;
    }

    public void registerRotate(PipelineRegister prev) {
        this.pipeline_rep = prev.pipeline_rep;
    }

    public void squash() {
        pipeline_rep = "squash";
    }

    public void stall() {
        pipeline_rep = "stall";
    }

    public void new_if_id(String pipeline_rep) {
        // this.operation = operation;
        // this.rs = rs;
        // this.rd = rd;
        // this.rt = rt;
        // this.immediate = immediate;
        this.pipeline_rep = pipeline_rep;
    }
}
