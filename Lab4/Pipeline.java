public class Pipeline {
    int pc = 0;
    PipelineRegister if_id;
    PipelineRegister id_exe;
    PipelineRegister exe_mem;
    PipelineRegister mem_wb;

    public Pipeline (int pc, PipelineRegister if_id, PipelineRegister id_exe,
    PipelineRegister exe_mem, PipelineRegister mem_wb) {
        this.pc = pc;
        this.if_id = if_id;
        this.id_exe = id_exe;
        this.exe_mem = exe_mem;
        this.mem_wb = mem_wb;
    }

    public void set_if_id(PipelineRegister p) {
        if_id.operation = p.operation;
        if_id.rs = p.rs;
        if_id.rd = p.rd;
        if_id.rt = p.rt;
        if_id.immediate = p.immediate;
        if_id.pipeline_rep = p.pipeline_rep;
    }

    public void print_pipeline() {
        System.out.println("pc\tif/id\tid/exe\texe/mem\tmem/wb");
        System.out.println(pc + "\t" + if_id.pipeline_rep + "\t" + id_exe.pipeline_rep + "\t" + exe_mem.pipeline_rep + "\t" + mem_wb.pipeline_rep);
    }

    public void step(String operation, boolean branch_taken) {
        mem_wb.registerRotate(exe_mem);
        exe_mem.registerRotate(id_exe);
        id_exe.registerRotate(if_id);
        if_id.new_if_id(operation);
        if (!(branch_taken || operation.equals("j") ||operation.equals("jr") || operation.equals("jal"))) {
            //this.pc ++;
        }
    }

    public void use_after_load() {
        mem_wb.registerRotate(exe_mem);
        exe_mem.registerRotate(id_exe);
        id_exe.stall();
    }

    public void conditional_branch(String operation) {
        mem_wb.registerRotate(exe_mem);
        exe_mem.registerRotate(id_exe);
        id_exe.registerRotate(if_id);
        if_id.new_if_id(operation);
        exe_mem.squash();
        id_exe.squash();
        if_id.squash();
    }

    public void unconditional_jump(String operation) {
        mem_wb.registerRotate(exe_mem);
        exe_mem.registerRotate(id_exe);
        id_exe.registerRotate(if_id);
        if_id.new_if_id(operation);
        if_id.squash();
    }
}
