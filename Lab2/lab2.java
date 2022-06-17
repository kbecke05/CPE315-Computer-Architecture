import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.io.*;
import java.lang.*;
 
public class lab2 {
 
    public static void main(String args[])
    {
        ArrayList<String> binary_array = new ArrayList<String>();

        HashMap <String, String> opcodes = new HashMap<String,String>();
        HashMap <String, String> functions = new HashMap<String,String>();
        HashMap <String, String> instruc_types = new HashMap<String,String>();
        HashMap <String, String> registers = new HashMap<String,String>();
        HashMap <String, String> label_lines = new HashMap<String,String>();


        fillOutMaps(opcodes, functions, instruc_types, registers);
        ArrayList<String> array_of_all_file_lines = entireFileArray(args[0]);

        //testing filling out hashmaps
        // for (Map.Entry<String, String> entry : opcodes.entrySet()) {
        //     System.out.println(entry.getKey()+" : "+entry.getValue());
        // }
       
        firstPass(array_of_all_file_lines, label_lines);
        // more processing before starting the second pass
        removeLabels(array_of_all_file_lines);
        ArrayList<ArrayList<String>> nested_arrays = new ArrayList<ArrayList<String>>();
        nested_arrays = fullyParse(array_of_all_file_lines);
        secondPass(nested_arrays, opcodes, functions, instruc_types, registers, label_lines, binary_array);

        //print for testing the preprocessing
        //System.out.println(opcodes.get("add"));
        // for (int i = 0; i<nested_arrays.size(); i++) {
        //     System.out.println(nested_arrays.get(i));
        // }

    }

    public static int branch_calculate(int curr_line, int label_line) {
        return label_line - (curr_line + 1);
    }
    public static String sign_extension(String binary) {
        //sign extension
        while (binary.length()<16) {
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
        while (label_as_binary.length()<26) {
            label_as_binary = "0" + label_as_binary;
        }
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
        //System.out.println("opcode!! " + instruc_array.get(0));
        //System.out.println("test!! " + instruc_array.get(3));
        if (instruc_array.get(0).equals("bne") || instruc_array.get(0).equals("beq")) {
            instruc.setRs(registers.get(instruc_array.get(1)));
            instruc.setRt(registers.get(instruc_array.get(2)));
            int label_as_immediate = Integer.parseInt(label_lines.get(instruc_array.get(3)));
            int jump_to = branch_calculate(curr_line, label_as_immediate);
            String jump_binary = Integer.toBinaryString(jump_to);
            jump_binary = sign_extension(jump_binary);
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
            imm_binary = sign_extension(imm_binary);
            instruc.setImmediate(imm_binary);
        }
        else { //addi
            int immediate_int = Integer.parseInt(instruc_array.get(3));
            String immediate_binary = Integer.toBinaryString(immediate_int);
            immediate_binary = sign_extension(immediate_binary);
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
        // for (int i = 0; i < instruc_array.size(); i++) {
        //     System.out.println(instruc_array.get(i));
        // }

        //String opcode = instruc_array.get(0);
        instruc.setOpcode(opcodes.get(instruc_array.get(0)));
        instruc.setFunction(functions.get(instruc_array.get(0)));
        //System.out.println("test  " + instruc_array.get(0));
        if (instruc_array.get(0).equals("jr")) {
            instruc.setRs(registers.get(instruc_array.get(1))); 
        }
        else if (instruc_array.get(0).equals("sll")) {
            instruc.setRd(registers.get(instruc_array.get(1)));
            instruc.setRt(registers.get(instruc_array.get(2)));
            int shamt_int = Integer.parseInt(instruc_array.get(3));
            String shamt_binary = Integer.toBinaryString(shamt_int);
            while (shamt_binary.length()<5) {
                shamt_binary = "0" + shamt_binary;
            }
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
            // if (instruc_array.get(0) == "sll") { //accounts for shamt
            //     int shamt_int = Integer.parseInt(instruc_array.get(3));
            //     String shamt_binary = Integer.toBinaryString(shamt_int);
            //     instruc.setShamt(shamt_binary);
            //     //instruc.setRt(registers.get(instruc_array.get(2)));
            // }
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
            //System.out.println(array_of_all_file_lines.get(j));
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

        // print for testing the first pass
        // System.out.println("label_lines Hash Map after First pass result: ");
        // for (String name: label_lines.keySet()) {
        //     String key = name.toString();
        //     String value = label_lines.get(name).toString();
        //     System.out.println(key + " " + value);
        // }
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
 


    public static void fillOutMaps(HashMap<String, String> opcodes, HashMap<String, String> functions, 
    HashMap<String, String> instruc_types, HashMap<String, String> registers) {
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

    }
}
 
