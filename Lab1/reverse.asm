# Name:  Kelly Becker and Sindhu Srivats
# Section:  07
# Description:  Takes a user inputted number and returns the decimal representation of all the 32 bits reversed.

.globl prompt
.globl endprompt
 
.data
 
prompt:
    .asciiz " Enter a number (integer): "
endprompt:
    .asciiz " The reversed number in bits is: "
 
.text
#s0 = user input
#s1 = reversed result
#s2 = counter from 32

# JAVA IMPLEMENTATION

# public int reversed(int user_input) {
      # print (" Enter a number (integer): ")
      # int answer = 0;
      # for (int i = 0; i <32; i++) {
          # int masked = user_input & 1;
          # user_input = user_input >>> 1;
          # answer = answer + masked;
          # answer = answer <<< 1;
      # }
      # return answer;
# }

main:
    addi $s2, $s2, 31
 
    #print (" Enter a number (integer): ")
    ori $v0, $0, 4
    lui $a0, 0x1001
    syscall
 
    ori $v0, $0, 5
    syscall
    
    #move to s0
    add $s0, $v0, $0
 
    # while s2 > 0:
    #   mask all except last bit
    #   shift user input right
    #   add value to reversed answer
    #   shift reversed answer left
    #   decrement counter
    

    loop:
    beq $s2, $0, end
    andi $t0, $s0, 1
    srl $s0, $s0, 1
    add $s1, $s1, $t0
    sll $s1, $s1, 1
    addi $s2, $s2, -1
    j loop
 
    #print out answer
    end:
    #print end prompt
    # ASK HIM TOMORROW HOW TO GET THE END PROMPT PRINTING
    ori $v0, $0, 4
    lui $a0, 0x1001
    ori $a0, $a0, 0x1C
    syscall
 
    #clear a0
    #move answer into a0
    #syscall to print number
    add $a0, $a0, $0
    add $a0, $s1, $0
    ori $v0, $0, 1
    syscall
 
    # Exit (load 10 into $v0)
    ori $v0, $0, 10
    syscall
