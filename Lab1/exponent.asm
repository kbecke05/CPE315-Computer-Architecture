# Name:  Kelly Becker and Sindhu Srivats
# Section:  07
# Description:  Takes a user inputted base and exponent and returns the result

.globl prompt1
.globl prompt2
.globl result
 
.data
 
prompt1:
    .asciiz " Enter an integer: "
prompt2:
    .asciiz " Enter another integer: "
result:
    .asciiz " The result is: "
 
# public class HelloWorld{
 
#      public static void main(String []args){
#         System.out.println(exponent(1,2));
#      }
     
#      public static int exponent(int base, int exp) {
#          int result = 0;
#          int final_result = 0;
#          int new_base = base;
         
#          for (int i = 1; i < exp; i++) {
#             for (int j = 0; j < base; j++) {
#                 result += new_base;
#             }
#             new_base = result;
#             final_result = result;
#             result = 0;
#          }
#          return final_result;
#      }
# }
 
 
# s0 = x = base
# s1 = y = exp
# s3 = new_base
# s2 = final_result
# s4 = result
# s6 = another copy of base/x
 
 
.text
 
main:
    #print out prompt1
    ori $v0, $0, 4
    add $a0, $0, $0
    lui $a0, 0x1001
    syscall
 
    #read in user input
    ori $v0, $0, 5
    syscall
 
 
    add $s0, $v0, $0 # s0 = base
 
    add $s3, $s0, $0 # int new_base = base;
    add $s6, $s0, $0 # s6 = another copy of base/x
 
    #print out prompt2
    ori $v0, $0, 4
    add $a0, $0, $0
    lui $a0, 0x1001
    ori $a0, $a0, 0x14
    syscall
 
    #read in user input
    ori $v0, $0, 5
    syscall
 
    add $s1, $v0, $0 # s1 = exp
 
   
    add $s2, $0, $0 # int final_result = 0;
    add $s4, $0, $0 # int result = 0;
 
 
    addi $s1, $s1, -1 # DECREMENT EXP INSTEAD OF STARTING AT i = 1 IN THE LOOP!!!
 
    # outer loop
    loop1:
    beq $s1, $0, end # for (int i = 1; i < exp; i++) {
 
    loop2:
    beq $s0, $0, loop2done # for (int j = 0; j < base; j++) {
    add $s4, $s4, $s3 # result += new_base;
    addi $s0, $s0, -1 # need to decrement loop 2 counter
    j loop2
 
 
    loop2done:
    addi $s1, $s1, -1 # need to decrement loop 1 counter
    add $s3, $s4, $0 # new_base = result;
    add $s2, $s4, $0 # final_result = result;
    add $s4, $0, $0 # result = 0;
    add $s0, $s6, $0 # need to restore base to original base value
    j loop1
 
 
    end:
 
    #print end string
    ori $v0, $0, 4
    lui $a0, 0x1001
    ori $a0, $a0, 0x2D        # correct offset? changed to 29
    syscall
 
    #print result value
    add $a0, $0, $0
    add $a0, $s2, $0
    ori $v0, $0, 1
    syscall
 
    # Exit (load 10 into $v0)
    ori $v0, $0, 10
    syscall
