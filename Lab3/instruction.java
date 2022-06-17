public class instruction {
    private String instruc_type;
    private String opcode = "000000";
    private String rs = "00000";
    private String rt = "00000";
    private String rd = "00000";
    private String shamt = "00000";
    private String function = "000000";
    private String immediate;
    private String jump_address;

    // private String instruc_type;
    // private String opcode;
    // private String rs ;
    // private String rt;
    // private String rd ;
    // private String shamt;
    // private String function;
    // private String immediate;
    // private String jump_address;

    public String print() {
        //System.out.println(this.instruc_type);
        String final_str = " ";
        if (this.instruc_type == "r") {
            final_str = this.opcode + " " + this.rs + " " + this.rt + " " + this.rd + " " + this.shamt+ " " + this.function;
        }
        else if (this.instruc_type == "i") {
            final_str = this.opcode + " " + this.rs + " " + this.rt + " " + this.immediate;
        }
        else {
            final_str = this.opcode + " " + this.jump_address;
        }
        //System.out.println(final_str);
        return final_str;
    }


    //ALL SETTERS AND GETTERS BELOW


    /**
     * @return String return the instruc_type
     */
    public String getInstruc_type() {
        return instruc_type;
    }

    /**
     * @param instruc_type the instruc_type to set
     */
    public void setInstruc_type(String instruc_type) {
        this.instruc_type = instruc_type;
    }

    /**
     * @return String return the opcode
     */
    public String getOpcode() {
        return opcode;
    }

    /**
     * @param opcode the opcode to set
     */
    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    /**
     * @return String return the rs
     */
    public String getRs() {
        return rs;
    }

    /**
     * @param rs the rs to set
     */
    public void setRs(String rs) {
        this.rs = rs;
    }

    /**
     * @return String return the rt
     */
    public String getRt() {
        return rt;
    }

    /**
     * @param rt the rt to set
     */
    public void setRt(String rt) {
        this.rt = rt;
    }

    /**
     * @return String return the rd
     */
    public String getRd() {
        return rd;
    }

    /**
     * @param rd the rd to set
     */
    public void setRd(String rd) {
        this.rd = rd;
    }

    /**
     * @return String return the shamt
     */
    public String getShamt() {
        return shamt;
    }

    /**
     * @param shamt the shamt to set
     */
    public void setShamt(String shamt) {
        this.shamt = shamt;
    }

    /**
     * @return String return the function
     */
    public String getFunction() {
        return function;
    }

    /**
     * @param function the function to set
     */
    public void setFunction(String function) {
        this.function = function;
    }

    /**
     * @return String return the immediate
     */
    public String getImmediate() {
        return immediate;
    }

    /**
     * @param immediate the immediate to set
     */
    public void setImmediate(String immediate) {
        this.immediate = immediate;
    }

    /**
     * @return String return the jump_address
     */
    public String getJump_address() {
        return jump_address;
    }

    /**
     * @param jump_address the jump_address to set
     */
    public void setJump_address(String jump_address) {
        this.jump_address = jump_address;
    }

}
