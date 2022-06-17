import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.io.*;
import java.lang.*;
 
public class lab4 {
    public static void main(String[] args)
    {
        //lab 2 data structures
        ArrayList<String> binary_array = new ArrayList<String>();

        HashMap <String, String> opcodes = new HashMap<String,String>();
        HashMap <String, String> functions = new HashMap<String,String>();
        HashMap <String, String> instruc_types = new HashMap<String,String>();
        HashMap <String, String> registers = new HashMap<String,String>();
        HashMap <String, String> label_lines = new HashMap<String,String>();

        //lab3 data structures
        int[] register_array = new int[32];
        String[] register_names = new String[32];
        HashMap <String, Integer> register_nums = new HashMap <String, Integer>();
        int[] pc = new int[1];
        fillOutRegisterNamesArray(register_names);
        int[] data_memory = new int[8192];
        pc[0] = 0;

        ArrayList<String> script_commands = new ArrayList<String>();

        //lab4 data structures

        ArrayList<String> pipeline_state = new ArrayList<String>();
        for (int i = 0; i < 4; i++) {
            pipeline_state.add("empty");
        }
        Pipeline curr_pipeline = new Pipeline(pc[0], new PipelineRegister("empty"),
        new PipelineRegister("empty"),new PipelineRegister("empty"),new PipelineRegister("empty"));
        int[] cycle_counter = new int[1];
        cycle_counter[0] = 0;
        int[] instruc_counter = new int[1];
        instruc_counter[0] = 0;
        boolean j_flag = false;

        // LAB 2
        fillOutMaps(opcodes, functions, instruc_types, registers, register_nums);
        ArrayList<String> array_of_all_file_lines = entireFileArray(args[0]);
        firstPass(array_of_all_file_lines, label_lines);
        removeLabels(array_of_all_file_lines);
        ArrayList<ArrayList<String>> nested_arrays = new ArrayList<ArrayList<String>>();
        nested_arrays = fullyParse(array_of_all_file_lines);
        secondPass(nested_arrays, opcodes, functions, instruc_types, registers, label_lines, binary_array);

        //lab 4
        ArrayList<String> nested_arrays_copy = new ArrayList<String>();
        for (int i = 0; i < nested_arrays.size(); i++) {
            nested_arrays_copy.add(nested_arrays.get(i).get(0));
        }
        // for (int i = 0; i < pipeline_state.size(); i++) {
        //     System.out.println(pipeline_state.get(i));
        // }

        //LAB 3
        if (args.length > 1) { //aka, there IS a script command
            make_script_array(args[1], script_commands);
            for (int i = 0; i < script_commands.size(); i++) {
                String command = script_commands.get(i);
                System.out.println("mips> " + command);
                run_commands(command, register_array, register_names, data_memory, 
                pc, nested_arrays, registers, label_lines, register_nums, pipeline_state, 
                cycle_counter, instruc_counter, nested_arrays_copy, curr_pipeline);
            }
        }
        else { //no script file
            String command = "";
            try {
                Scanner sc = new Scanner(System.in);
                while (!(command.equals("q"))) {
                    System.out.print("mips> ");
                    command = sc.nextLine().trim();
                    run_commands(command, register_array, register_names, data_memory, 
                    pc, nested_arrays, registers, label_lines, register_nums, 
                    pipeline_state, cycle_counter, instruc_counter, nested_arrays_copy,
                    curr_pipeline);
                }
            }
            catch (Exception e) {
                Throwable e_throw = new Throwable(e);
                e_throw.printStackTrace();
                System.out.println("reading in commands error");
            }
        }

        
    }
    // LAB 4

    public static String next_op_rs(HashMap <String, Integer> register_nums,
    ArrayList<ArrayList<String>> nested_arrays,
    int[] pc,
    ArrayList<String> next_op) {
        String rs = "";
        String operation = next_op.get(0);
        if (operation.equals("and") || operation.equals("or") || operation.equals("add") || operation.equals("sub") || operation.equals("slt") || operation.equals("addi")) {
            rs = nested_arrays.get(pc[0]+1).get(2);
            //System.out.println(nested_arrays.get(pc[0]));
            //System.out.println(nested_arrays.get(pc[0]).get(2));
        }
        else if (operation.equals("beq") || operation.equals("bne") || operation.equals("jr")) {
            rs = nested_arrays.get(pc[0]+1).get(1);
        }
        else if (operation.equals("lw") || operation.equals("sw")) {
            rs = nested_arrays.get(pc[0]+1).get(3);
        }
        //System.out.println("next rs = " + rs);
        return rs;
    }

    public static String next_op_rt(HashMap <String, Integer> register_nums,
    ArrayList<ArrayList<String>> nested_arrays,
    int[] pc,
    ArrayList<String> next_op) {
        String rt = "";
        String operation = next_op.get(0);
        if (operation.equals("and") ||  operation.equals("or") || operation.equals("add") || operation.equals("sub") || operation.equals("slt")) {
            rt = nested_arrays.get(pc[0]+1).get(3);
        }
        else if (operation.equals("beq") || operation.equals("bne") || operation.equals("sll")) {
            rt = nested_arrays.get(pc[0]+1).get(2);
        }
        else if (operation.equals("sw")) { // || operation.equals("lw") || operation.equals("addi")) {
            rt = nested_arrays.get(pc[0]+1).get(1);
        }
        //System.out.println("next rt = " + rt);
        return rt;
    }

    // public static void p_command(ArrayList<String> pipeline_state, int[] pc) {
    //     // System.out.println("pc\tif/if\tid/exe\texe/mem\tmem/wb");
    //     // // if (jump_flag == true) {
            
    //     // // }
    //     // String if_id = pipeline_state.get(0);
    //     // String id_exe = pipeline_state.get(1);
    //     // String exe_mem = pipeline_state.get(2);
    //     // String mem_wb = pipeline_state.get(3);
    //     // System.out.println(pc[0] + "\t" + if_id + "\t" + id_exe + "\t" + exe_mem + "\t" + mem_wb);
    //     // pipeline_state.remove(pipeline_state.size()-1);
    //     curr_pipeline.print_pipeline();
    // }   


    //LAB 3

    //instrucs to support: and, or, add, addi, sll, sub, slt, beq, bne, lw, sw, j, jr, and jal

    public static void s_command(int[] register_array, 
    String[] register_names,
    int[] data_memory,
    int[] pc,
    ArrayList<ArrayList<String>> nested_arrays,
    HashMap <String, String> registers,
    HashMap <String, String> label_lines,
    HashMap <String, Integer> register_nums,
    ArrayList<String> pipeline_state,
    int[] cycle_counter,
    int[] instruc_counter,
    ArrayList<String> nested_arrays_copy,
    Pipeline curr_pipeline) {
        cycle_counter[0]++;
        instruc_counter[0]++;
        String operation = new String(nested_arrays.get(pc[0]).get(0));
        pipeline_state.add(0, nested_arrays_copy.get(pc[0]));
        //System.out.println(operation);
        // curr_pipeline.pc = pc[0]+1;
        curr_pipeline.pc ++;
        if (operation.equals("and")) {
            int source1_register_num = register_nums.get(nested_arrays.get(pc[0]).get(2)); //rt
            int source2_register_num = register_nums.get(nested_arrays.get(pc[0]).get(3));
            int dest_register_num = register_nums.get(nested_arrays.get(pc[0]).get(1));
            int source1_reg_value = register_array[source1_register_num];
            int source2_reg_value = register_array[source2_register_num];
            int answer = source1_reg_value & source2_reg_value;
            register_array[dest_register_num] = answer;
            //xx?? = new PipelineRegister(operation, source2_register_num, dest_register_num, source1_register_num, 0, operation);
            pc[0]++;
            curr_pipeline.step(operation, false);
        }
        else if (operation.equals("or")) {
            int source1_register_num = register_nums.get(nested_arrays.get(pc[0]).get(2));
            int source2_register_num = register_nums.get(nested_arrays.get(pc[0]).get(3));
            int dest_register_num = register_nums.get(nested_arrays.get(pc[0]).get(1));
            int source1_reg_value = register_array[source1_register_num];
            int source2_reg_value = register_array[source2_register_num];
            int answer = source1_reg_value | source2_reg_value;
            register_array[dest_register_num] = answer;
            //xx?? = new PipelineRegister(operation, source2_register_num, dest_register_num, source1_register_num, 0, operation);
            pc[0]++;
            curr_pipeline.step(operation, false);
        }
        else if (operation.equals("add")) {
            int source1_register_num = register_nums.get(nested_arrays.get(pc[0]).get(2));
            int source2_register_num = register_nums.get(nested_arrays.get(pc[0]).get(3));
            int dest_register_num = register_nums.get(nested_arrays.get(pc[0]).get(1));
            int source1_reg_value = register_array[source1_register_num];
            int source2_reg_value = register_array[source2_register_num];
            int answer = source1_reg_value + source2_reg_value;
            register_array[dest_register_num] = answer;
            //xx?? = new PipelineRegister(operation, source2_register_num, dest_register_num, source1_register_num, 0, operation);
            pc[0]++;
            curr_pipeline.step(operation, false);
        }
        else if (operation.equals("addi")) {
            int source_register_num = register_nums.get(nested_arrays.get(pc[0]).get(2)); //rs
            int dest_register_num = register_nums.get(nested_arrays.get(pc[0]).get(1)); //rt
            int reg_value = register_array[source_register_num];
            int answer = reg_value + Integer.parseInt(nested_arrays.get(pc[0]).get(3));
            register_array[dest_register_num] = answer;
            //xx?? = new PipelineRegister(operation, source_register_num, 0, dest_register_num, Integer.parseInt(nested_arrays.get(pc[0]).get(3)), operation);
            pc[0]++;
            curr_pipeline.step(operation, false);
        }
        else if (operation.equals("sll")) {
            int source_register_num = register_nums.get(nested_arrays.get(pc[0]).get(2));
            int dest_register_num = register_nums.get(nested_arrays.get(pc[0]).get(1));
            int shamt = (int) Math.pow(2, register_nums.get(nested_arrays.get(pc[0]).get(2)));
            int reg_value = register_array[source_register_num];
            int answer = reg_value * shamt;
            register_array[dest_register_num] = answer;
            //xx?? = new PipelineRegister(operation, 0, dest_register_num, source_register_num, shamt, operation)
            pc[0]++;
            curr_pipeline.step(operation, false);
        }
        else if (operation.equals("sub")) {
            int source1_register_num = register_nums.get(nested_arrays.get(pc[0]).get(2));
            int source2_register_num = register_nums.get(nested_arrays.get(pc[0]).get(3));
            int dest_register_num = register_nums.get(nested_arrays.get(pc[0]).get(1));
            int source1_reg_value = register_array[source1_register_num];
            int source2_reg_value = register_array[source2_register_num];
            int answer = source1_reg_value - source2_reg_value;
            register_array[dest_register_num] = (int)answer;
            //xx?? = new PipelineRegister(operation, source2_register_num, dest_register_num, source1_register_num, 0, operation);
            pc[0]++;
            curr_pipeline.step(operation, false);
        }
        else if (operation.equals("slt")) {
            int source1_register_num = register_nums.get(nested_arrays.get(pc[0]).get(2));
            int source2_register_num = register_nums.get(nested_arrays.get(pc[0]).get(3));
            int dest_register_num = register_nums.get(nested_arrays.get(pc[0]).get(1));
            int source1_reg_value = register_array[source1_register_num];
            int source2_reg_value = register_array[source2_register_num];
            int answer = 0;
            if (source1_reg_value < source2_reg_value) {
                answer = 1;
            }
            register_array[dest_register_num] = (int)answer;
            //xx?? = new PipelineRegister(operation, source2_register_num, dest_register_num, source1_register_num, 0, operation);
            pc[0]++;
            curr_pipeline.step(operation, false);
        }
        else if (operation.equals("beq")) {
            int source1_register_num = register_nums.get(nested_arrays.get(pc[0]).get(1)); //rs
            int source2_register_num = register_nums.get(nested_arrays.get(pc[0]).get(2)); //rt
            int jump_label = Integer.parseInt(label_lines.get(nested_arrays.get(pc[0]).get(3)));
            int source1_reg_value = register_array[source1_register_num];
            int source2_reg_value = register_array[source2_register_num];
            pc[0]++;
            //xx?? = new PipelineRegister(operation, source1_register_num, 0, source2_register_num, jump_label, operation);
            curr_pipeline.step(operation, source1_reg_value != source2_reg_value);
            if (source1_reg_value == source2_reg_value) { //aka branch is taken
                pc[0] = pc[0] + 1 + branch_calculate(pc[0], jump_label);
                cycle_counter[0] = cycle_counter[0] + 3;
                //curr_pipeline.pc = pc[0];
                curr_pipeline.conditional_branch(operation);
            }
        }
        else if (operation.equals("bne")) {
            int source1_register_num = register_nums.get(nested_arrays.get(pc[0]).get(1));
            int source2_register_num = register_nums.get(nested_arrays.get(pc[0]).get(2));
            int jump_label = Integer.parseInt(label_lines.get(nested_arrays.get(pc[0]).get(3)));
            int source1_reg_value = register_array[source1_register_num];
            int source2_reg_value = register_array[source2_register_num];
            pc[0]++;
            //xx?? = new PipelineRegister(operation, source1_register_num, 0, source2_register_num, jump_label, operation);
            curr_pipeline.step(operation, source1_reg_value != source2_reg_value);
            if (source1_reg_value != source2_reg_value) { //aka branch is taken
                pc[0] = pc[0] + 1 + branch_calculate(pc[0], jump_label);
                cycle_counter[0] = cycle_counter[0] + 3;
                //curr_pipeline.pc = pc[0];
                curr_pipeline.conditional_branch(operation);
            }
        }
        else if (operation.equals("lw")) {
            int rt = register_nums.get(nested_arrays.get(pc[0]).get(1));
            int imm = Integer.parseInt(nested_arrays.get(pc[0]).get(2));
            int rs = register_nums.get(nested_arrays.get(pc[0]).get(3));
            //xx?? = new PipelineRegister(operation, rs, 0, rt, imm, operation);

            //USE AFTER LOAD
            ArrayList<String> next_op = nested_arrays.get(pc[0]+1);
            String next_op_rs = next_op_rs(register_nums, nested_arrays, pc, next_op);
            String next_op_rt = next_op_rt(register_nums, nested_arrays, pc, next_op);
            int rs_value = register_array[rs];
            register_array[rt] = data_memory[rs_value+imm];
            
            if (!(nested_arrays.get(pc[0]).get(1).equals("0")) && (
            nested_arrays.get(pc[0]).get(1).equals(next_op_rs) || 
            nested_arrays.get(pc[0]).get(1).equals(next_op_rt))) {
                // pipeline_state.add(0, nested_arrays_copy.get(pc[0]));
                // nested_arrays_copy.add(pc[0]+1, "stall");
                pc[0]++;
                curr_pipeline.use_after_load();
                cycle_counter[0]++;
                //System.out.println("stall");
            }
            else{
                pc[0]++;
                curr_pipeline.step(operation, false);
            }
        }
        else if (operation.equals("sw")) {
            int rt = register_nums.get(nested_arrays.get(pc[0]).get(1));
            int imm = Integer.parseInt(nested_arrays.get(pc[0]).get(2));
            int rs = register_nums.get(nested_arrays.get(pc[0]).get(3));
            int rs_value = register_array[rs];
            data_memory[rs_value+imm] = register_array[rt];
            //xx?? = new PipelineRegister(operation, rs, 0, rt, imm, operation);
            pc[0]++;
            curr_pipeline.step(operation, false);
        }
        else if (operation.equals("j")) {
            int jump_label = Integer.parseInt(label_lines.get(nested_arrays.get(pc[0]).get(1))); 
            pc[0] = jump_label;
            //curr_pipeline.pc = pc[0];
            //xx?? = new PipelineRegister(operation, 0,0,0, jump_label, operation);
            cycle_counter[0]++;
            curr_pipeline.step(operation, false);
            curr_pipeline.unconditional_jump(operation);
        }
        else if (operation.equals("jr")) {
            int rs = register_nums.get(nested_arrays.get(pc[0]).get(1));
            int rs_value = register_array[rs];
            pc[0] = rs_value;
            //curr_pipeline.pc = pc[0];
            //xx?? = new PipelineRegister(operation, rs,0,0, jump_label, operation);
            cycle_counter[0]++;
            curr_pipeline.step(operation, false);
            curr_pipeline.unconditional_jump(operation);
        }
        else if (operation.equals("jal")) {
            int jump_label = Integer.parseInt(label_lines.get(nested_arrays.get(pc[0]).get(1))); 
            pc[0]++;
            register_array[26] = pc[0];
            pc[0] = jump_label;
            //curr_pipeline.pc = pc[0];
            //curr_pipeline.set_if_id(new PipelineRegister(operation, 0,0,0, jump_label, operation));
            cycle_counter[0]++;
            curr_pipeline.step(operation, false);
            curr_pipeline.unconditional_jump(operation);
        }
        //System.out.println("outside s if/id: " + curr_pipeline.if_id.pipeline_rep);
    }

    public static void s_num_command(int[] register_array, 
    String[] register_names,
    int[] data_memory,
    int[] pc,
    ArrayList<ArrayList<String>> nested_arrays,
    HashMap <String, String> registers,
    HashMap <String, String> label_lines,
    HashMap <String, Integer> register_nums, 
    int num,
    ArrayList<String> pipeline_state,
    int[] cycle_counter,
    int[] instruc_counter,
    ArrayList<String> nested_arrays_copy,
    Pipeline curr_pipeline) {
        for (int i = 0; i < num; i++) {
            s_command(register_array, register_names, data_memory, pc, nested_arrays, registers, 
            label_lines, register_nums, pipeline_state, cycle_counter, instruc_counter, nested_arrays_copy,
            curr_pipeline);
        }
    }

    public static void c_command(int[] register_array, int[] data_memory, int[] pc) {
        Arrays.fill(register_array, 0);
        Arrays.fill(data_memory, 0);
        Arrays.fill(pc, 0);


    }

    public static void m_command(String num1, String num2, int[] data_memory) {
        int num1_int = Integer.parseInt(num1);
        int num2_int = Integer.parseInt(num2);
        for (int i = num1_int; i < num2_int + 1; i++) {
            System.out.println("[" + i + "]" + " = " + data_memory[i]);
        }

    }

    public static void d_command(int[] register_array, String[] register_names, int[] pc) {
        System.out.println("pc = " + pc[0]);
        System.out.println(
            register_names[0] + " = " +register_array[0] + "        "
            + register_names[1] + " = " +register_array[1] + "       "
            + register_names[2] + " = " +register_array[2] + "       "
            + register_names[3] + " = " +register_array[3]);
        for (int i = 4; i<24; i = i+4) {
            System.out.println(
            register_names[i] + " = " +register_array[i] + "       "
            + register_names[i+1] + " = " +register_array[i+1] + "       "
            + register_names[i+2] + " = " +register_array[i+2] + "       "
            + register_names[i+3] + " = " +register_array[i+3]);
        }
        System.out.println(
            register_names[24] + " = " +register_array[24] + "       "
            + register_names[25] + " = " +register_array[25] + "       "
            + register_names[26] + " = " +register_array[26]);
    }

    public static void h_command() {
        System.out.println("h = show help");
        System.out.println("d = dump register state");
        System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
        System.out.println("s num = step through num instructions of the program");
        System.out.println("r = run until the program ends");
        System.out.println("m num1 num2 = display data memory from location num1 to num2");
        System.out.println("c = clear all registers, memory, and the program counter to 0");
        System.out.println("q = exit the program");
    }

    public static void run_commands(String command, 
    int[] register_array, 
    String[] register_names,
    int[] data_memory,
    int[] pc,
    ArrayList<ArrayList<String>> nested_arrays,
    HashMap <String, String> registers,
    HashMap <String, String> label_lines,
    HashMap <String, Integer> register_nums,
    ArrayList<String> pipeline_state,
    int[] cycle_counter,
    int[] instruc_counter,
    ArrayList<String> nested_arrays_copy,
    Pipeline curr_pipeline) {
        System.out.println();
        String[] command_array = command.split(" ");
        if (command_array[0].equals("h")) {
            h_command();
        }
        else if (command_array[0].equals("d")) {
            d_command(register_array, register_names, pc);
        }
        else if (command_array[0].equals("p")) {
            // p_command(pipeline_state, pc);
            System.out.println(curr_pipeline.if_id.pipeline_rep);
            curr_pipeline.print_pipeline();
        }
        else if (command_array.length == 2) { // s num command
            //String[] s_command_array = command.split(" ");
            //System.out.println("     " + command_array[1] + " instruction(s) executed");
            s_num_command(register_array, register_names, data_memory, 
            pc, nested_arrays, registers, label_lines, 
            register_nums, Integer.parseInt(command_array[1]), pipeline_state, 
            cycle_counter, instruc_counter, nested_arrays_copy,
            curr_pipeline);
            //lab 4 addtional pipeline info and printing
            // p_command(pipeline_state, pc);
            curr_pipeline.print_pipeline();
        }
        else if (command_array[0].equals("s")) {
            //System.out.println("     1 instruction(s) executed");
            s_command(register_array, register_names, data_memory, pc, nested_arrays, registers, 
            label_lines, register_nums, pipeline_state, cycle_counter, instruc_counter, nested_arrays_copy,
            curr_pipeline);
            //lab 4 addtional pipeline info and printing
            // p_command(pipeline_state, pc);
            curr_pipeline.print_pipeline();
        }
        else if (command.equals("r")) {
            while (pc[0] < nested_arrays.size()) {
                s_command(register_array, register_names, data_memory, pc, nested_arrays, registers, 
                label_lines, register_nums, pipeline_state, cycle_counter, instruc_counter, nested_arrays_copy,
                curr_pipeline);
            }
            //lab 4
            System.out.println("Program complete");
            //System.out.println((double)cycle_counter[0]/(double)instruc_counter[0]);
            // INSERT RUNTIME CALCULATIONS
            cycle_counter[0] = cycle_counter[0]+4;
            double CPI = (double)cycle_counter[0]/(double)instruc_counter[0];
            System.out.println( "CPI = " + truncateTo(CPI, 3) + 
            " Cycles = " + cycle_counter[0] + " Instructions = " + instruc_counter[0]);
        }
        else if (command_array[0].startsWith("m")) { // m num1 num2 command
            String[] m_command_array = command.split(" ");
            m_command(m_command_array[1], m_command_array[2], data_memory);
        }
        else if (command_array[0].equals("c")) { 
            System.out.println("    Simulator reset");
            c_command(register_array, data_memory, pc);
        }
        System.out.println();
    }
    public static double truncateTo( double unroundedNumber, int decimalPlaces ){
        int truncatedNumberInt = (int)( unroundedNumber * Math.pow( 10, decimalPlaces ) );
        double truncatedNumber = (double)( truncatedNumberInt / Math.pow( 10, decimalPlaces ) );
        return truncatedNumber;
    }
    public static void make_script_array(String filename, ArrayList<String> script_commands){
        try {
            Scanner script_sc = new Scanner(new File (filename));
            while(script_sc.hasNextLine()) {
                script_commands.add(script_sc.nextLine().trim());
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("script file not found");
        }
    }

    //LAB 2

    public static int branch_calculate(int curr_line, int label_line) {
        return label_line - (curr_line + 1);
    }
    public static String sign_extension(String binary, int num) {
        while (binary.length()<num) {
            binary = "0" + binary;
        }
        return binary;
    }

    public static void j_func(instruction instruc, ArrayList<String> instruc_array,
    HashMap <String, String> opcodes,
    HashMap <String, String> functions,
    HashMap <String, String> instruc_types,
    HashMap <String, String> registers,
    HashMap <String, String> label_lines) {
        instruc.setOpcode(opcodes.get(instruc_array.get(0)));
        String label = instruc_array.get(1);
        label = label_lines.get(label);
        int label_as_int = Integer.parseInt(label);
        String label_as_binary = Integer.toBinaryString(label_as_int);
        label_as_binary = sign_extension(label_as_binary, 26);
        instruc.setJump_address(label_as_binary);
    }

    public static void i_func(instruction instruc, ArrayList<String> instruc_array,
    HashMap <String, String> opcodes,
    HashMap <String, String> functions,
    HashMap <String, String> instruc_types,
    HashMap <String, String> registers,
    HashMap <String, String> label_lines,
    int curr_line) {
        instruc.setOpcode(opcodes.get(instruc_array.get(0)));
        if (instruc_array.get(0).equals("bne") || instruc_array.get(0).equals("beq")) {
            instruc.setRs(registers.get(instruc_array.get(1)));
            instruc.setRt(registers.get(instruc_array.get(2)));
            int label_as_immediate = Integer.parseInt(label_lines.get(instruc_array.get(3)));
            int jump_to = branch_calculate(curr_line, label_as_immediate);
            String jump_binary = Integer.toBinaryString(jump_to);
            jump_binary = sign_extension(jump_binary, 16);
            if (jump_binary.startsWith("1")) {
                jump_binary = jump_binary.substring(16);
            }
            instruc.setImmediate(jump_binary);
        }
        else if (instruc_array.get(0).equals("lw") || instruc_array.get(0).equals("sw")) {
            instruc.setRt(registers.get(instruc_array.get(1)));
            instruc.setRs(registers.get(instruc_array.get(3)));
            int imm_as_int = Integer.parseInt(instruc_array.get(2));
            String imm_binary = Integer.toBinaryString(imm_as_int);
            imm_binary = sign_extension(imm_binary, 16);
            instruc.setImmediate(imm_binary);
        }
        else { //addi
            int immediate_int = Integer.parseInt(instruc_array.get(3));
            String immediate_binary = Integer.toBinaryString(immediate_int);
            immediate_binary = sign_extension(immediate_binary, 16);
            if (immediate_binary.startsWith("1")) {
                immediate_binary = immediate_binary.substring(16);
            }
            instruc.setImmediate(immediate_binary);
            instruc.setRs(registers.get(instruc_array.get(2)));
            instruc.setRt(registers.get(instruc_array.get(1)));
        }
    }

    public static void r_func(instruction instruc, ArrayList<String> instruc_array,
    HashMap <String, String> opcodes,
    HashMap <String, String> functions,
    HashMap <String, String> instruc_types,
    HashMap <String, String> registers,
    HashMap <String, String> label_lines) {

        instruc.setOpcode(opcodes.get(instruc_array.get(0)));
        instruc.setFunction(functions.get(instruc_array.get(0)));
        if (instruc_array.get(0).equals("jr")) {
            instruc.setRs(registers.get(instruc_array.get(1))); 
        }
        else if (instruc_array.get(0).equals("sll")) {
            instruc.setRd(registers.get(instruc_array.get(1)));
            instruc.setRt(registers.get(instruc_array.get(2)));
            int shamt_int = Integer.parseInt(instruc_array.get(3));
            String shamt_binary = Integer.toBinaryString(shamt_int);
            shamt_binary = sign_extension(shamt_binary, 5);
            instruc.setShamt(shamt_binary);
        }
        else if (instruc_array.get(0).equals("sub")) {
            instruc.setRd(registers.get(instruc_array.get(1)));
            instruc.setRs(registers.get(instruc_array.get(2)));
            instruc.setRt(registers.get(instruc_array.get(3)));
        }
        else { //add
            instruc.setRd(registers.get(instruc_array.get(1)));
            instruc.setRs(registers.get(instruc_array.get(2)));
            instruc.setRt(registers.get(instruc_array.get(3)));
        }
    }

    public static void secondPass(ArrayList<ArrayList<String>> nested_arrays,
    HashMap <String, String> opcodes,
    HashMap <String, String> functions,
    HashMap <String, String> instruc_types,
    HashMap <String, String> registers,
    HashMap <String, String> label_lines,
    ArrayList<String> binary_array) {
        for(int i = 0; i<nested_arrays.size(); i++) {
            //error check
            if (!(opcodes.containsKey(nested_arrays.get(i).get(0)))) {
                System.out.println("invalid instruction: " + nested_arrays.get(i).get(0));
                System.exit(0);
            }
            String instruc_type = instruc_types.get(nested_arrays.get(i).get(0));
            instruction instruc = new instruction();
            instruc.setInstruc_type(instruc_type);
            if (instruc_type == "r") {
                r_func(instruc, nested_arrays.get(i), opcodes, functions, instruc_types, registers, label_lines);
            } 
            else if (instruc_type == "i") {
                i_func(instruc, nested_arrays.get(i), opcodes, functions, instruc_types, registers, label_lines, i);
            } 
            else { //j type instruct
                j_func(instruc, nested_arrays.get(i), opcodes, functions, instruc_types, registers, label_lines);
            }
            binary_array.add(instruc.print());
        }
    }

    public static ArrayList<ArrayList<String>> fullyParse(ArrayList<String> array_of_all_file_lines) {
        ArrayList<ArrayList<String>> fully_parsed = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < array_of_all_file_lines.size(); i++) {
            ArrayList<String> intermediate = new ArrayList<String>();
            String[] indiv_instrucs = array_of_all_file_lines.get(i).split("[\\$(), ]");
            for (int j = 0; j < indiv_instrucs.length; j++) {
                indiv_instrucs[j] = indiv_instrucs[j].trim();
                if (!(indiv_instrucs[j].isEmpty())) {
                    intermediate.add(indiv_instrucs[j]);
                }
            }
            fully_parsed.add(intermediate);
        }
        return fully_parsed;
    }

    public static void removeLabels(ArrayList<String> array_of_all_file_lines) {
        for (int i = 0; i < array_of_all_file_lines.size(); i++) {
            if (array_of_all_file_lines.get(i).contains(":")) {
                int colon_index = array_of_all_file_lines.get(i).indexOf(":");
                String without_label = array_of_all_file_lines.get(i).substring(colon_index + 1, array_of_all_file_lines.get(i).length());
                array_of_all_file_lines.set(i, without_label);
            }
        }
        for (int j = 0; j < array_of_all_file_lines.size(); j++) {
            if (array_of_all_file_lines.get(j).isEmpty()) {
                array_of_all_file_lines.remove(j);
            }
            array_of_all_file_lines.set(j, array_of_all_file_lines.get(j).trim());
        }
    }

    public static int firstPass(ArrayList<String> array_of_all_file_lines, HashMap<String, String> label_lines) {
        int counter = 0;
        for (int i = 0; i<array_of_all_file_lines.size(); i++) {
            if (array_of_all_file_lines.get(i).contains(":")) {
                String[] line_with_labels = array_of_all_file_lines.get(i).split(":");
                label_lines.put(line_with_labels[0], Integer.toString(i));
            }
        }
        return counter;
    }

    public static ArrayList<String> entireFileArray(String filename) {
        ArrayList<String> final_arr = new ArrayList<String>();
        try {
            Scanner sc = new Scanner(new File (filename));
            while (sc.hasNextLine()) {
                String full_line = sc.nextLine();
                full_line = full_line.trim();
                String[]removed_comments = full_line.split("#");
                String aLine;
                if (removed_comments.length != 0) {
                    aLine = removed_comments[0];
                }
                else {
                    aLine = full_line;
                }
                aLine.replaceAll("\\s+", "");
                aLine.replaceAll("\t", "");
                aLine.replaceAll(" ", "");
                aLine.trim();
                if(aLine.isEmpty() == false)
                {                    
                    final_arr.add(aLine);
                }
                for (int j = 0; j<final_arr.size(); j++) {
                    if(final_arr.get(j).startsWith("#")) {
                        final_arr.remove(j);
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("Can't open input file");
        }
        return final_arr;
    }
 
    public static void fillOutRegisterNamesArray(String[] register_names) {
        register_names[0] = "$0";
        register_names[1] = "$v0";
        register_names[2] = "$v1";
        register_names[3] = "$a0";
        register_names[4] = "$a1";
        register_names[5] = "$a2";
        register_names[6] = "$a3";
        register_names[7] = "$t0";
        register_names[8] = "$t1";
        register_names[9] = "$t2";
        register_names[10] = "$t3";
        register_names[11] = "$t4";
        register_names[12] = "$t5";
        register_names[13] = "$t6";
        register_names[14] = "$t7";
        register_names[15] = "$s0";
        register_names[16] = "$s1";
        register_names[17] = "$s2";
        register_names[18] = "$s3";
        register_names[19] = "$s4";
        register_names[20] = "$s5";
        register_names[21] = "$s6";
        register_names[22] = "$s7";
        register_names[23] = "$t8";
        register_names[24] = "$t9";
        register_names[25] = "$sp";
        register_names[26] = "$ra";
    }

    public static void fillOutMaps(HashMap<String, String> opcodes, HashMap<String, String> functions, 
    HashMap<String, String> instruc_types, HashMap<String, String> registers, HashMap<String, Integer> register_nums) {
        opcodes.put("and", "000000");
        opcodes.put("or", "000000");
        opcodes.put("add", "000000");
        opcodes.put("addi", "001000");
        opcodes.put("sll", "000000");
        opcodes.put("sub", "000000");
        opcodes.put("slt", "000000");
        opcodes.put("beq", "000100");
        opcodes.put("bne", "000101");
        opcodes.put("lw", "100011");
        opcodes.put("sw", "101011");
        opcodes.put("j", "000010");
        opcodes.put("jr", "000000");
        opcodes.put("jal","000011");

        functions.put("and", "100100");
        functions.put("or", "100101");
        functions.put("add", "100000");
        functions.put("sll", "000000");
        functions.put("sub", "100010");
        functions.put("slt", "101010");
        functions.put("jr", "001000");

        instruc_types.put("and", "r");
        instruc_types.put("or", "r");
        instruc_types.put("add", "r");
        instruc_types.put("addi", "i");
        instruc_types.put("sll", "r");
        instruc_types.put("sub", "r");
        instruc_types.put("slt", "r");
        instruc_types.put("beq", "i");
        instruc_types.put("bne", "i");
        instruc_types.put("lw", "i");
        instruc_types.put("sw", "i");
        instruc_types.put("j", "j");
        instruc_types.put("jr", "r");
        instruc_types.put("jal","j");
        
        //No support: $at, $k0, $k1, $gp, $fp.
       registers.put("0", "00000");
       registers.put("zero", "00000");
       registers.put("v0", "00010");
       registers.put("v1", "00011");
       registers.put("a0", "00100");
       registers.put("a1", "00101");
       registers.put("a2", "00110");
       registers.put("a3", "00111");
       registers.put("t0", "01000");
       registers.put("t1", "01001");
       registers.put("t2", "01010");
       registers.put("t3", "01011");
       registers.put("t4", "01100");
       registers.put("t5","01101");
       registers.put("t6", "01110");
       registers.put("t7", "01111");
       registers.put("s0", "10000");
       registers.put("s1", "10001");
       registers.put("s2", "10010");
       registers.put("s3", "10011");
       registers.put("s4", "10100");
       registers.put("s5", "10101");
       registers.put("s6", "10110");
       registers.put("s7", "10111");
       registers.put("t8", "11000");
       registers.put("t9", "11001");
       registers.put("sp", "11101");
       registers.put("ra","11111");

       register_nums.put("0", 0);
       register_nums.put("zero", 0);
       register_nums.put("v0", 1);
       register_nums.put("v1", 2);
       register_nums.put("a0", 3);
       register_nums.put("a1", 4);
       register_nums.put("a2", 5);
       register_nums.put("a3", 6);
       register_nums.put("t0", 7);
       register_nums.put("t1", 8);
       register_nums.put("t2", 9);
       register_nums.put("t3", 10);
       register_nums.put("t4", 11);
       register_nums.put("t5", 12);
       register_nums.put("t6", 13);
       register_nums.put("t7", 14);
       register_nums.put("s0", 15);
       register_nums.put("s1", 16);
       register_nums.put("s2", 17);
       register_nums.put("s3", 18);
       register_nums.put("s4", 19);
       register_nums.put("s5", 20);
       register_nums.put("s6", 21);
       register_nums.put("s7", 22);
       register_nums.put("t8", 23);
       register_nums.put("t9", 24);
       register_nums.put("sp", 25);
       register_nums.put("ra",26);

    }
}
 


