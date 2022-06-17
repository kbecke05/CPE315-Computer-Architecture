# Name:  Kelly Becker and Sindhu Srivats
# Section:  07
# Description:  Takes a user inputted dividend and divisor (must be power of 2) and returns the remainder

.globl promptDividend
.globl promptDivisor
.globl endprompt
 
.data
 
promptDividend:
    .asciiz " Enter the dividend: "
promptDivisor:
    .asciiz " Enter the divisor: "
endprompt:
    .asciiz "\n Mod result: "
 
.text
#s0 = user input for dividend # int s0 = divisor; //s0 holds user input for divisor
#s1 = user input for divisor # int s1 = dividend; //s1 holds user input for dividend
#s2 = loop counter #2 # int s2 = 0; //counter for loop2
#s3 = copy of loop counter #2's value # int s3; //copy of s2 (counter for loop2)
#s4 = mod result # int s4; //s4 holds mod result
#s6 = holds 1 # int s6 = 1; //$s6 holds 1 at LSB. will use it to generate mask
 
# public static int mod(int divisor, int dividend)
# {
#      int s0 = divisor; //s0 holds user input for divisor
#      int s1 = dividend; //s1 holds user input for dividend
#      int s2 = 0; //counter for loop2
#      int s3; //copy of s2 (counter for loop2)
#      int s4; //s4 holds mod result
     
#      // #s0 = user input for dividend
#      // #s1 = user input for divisor
#      // #s2 = loop counter #2
#      // #s3 = copy of loop counter #2's value
#      // #s4 = mod result
#      // #s6 = holds 1
     
#      //prepreloops:
#      int s6 = 1; //$s6 holds 1 at LSB. will use it to generate mask
#      s4 = 0; //clear $s4
     
#      //loop1:
#      s1 = s1 >>> 1;
#      s2 = s2 + 1;
#      if(s1 == 0):
#           goto preloop2;
#      else:
#           goto loop1;
     
     
#      //preloop2:
#      s2 -= 1; //need to decrement the counter for loop2 by 1 because the number of shifts until the divisor is 0 is increased by 1 because the divisor will always have a 0 at the end of its binary value, and the dividend shouldn't be shifted an extra time because of it
#      s3 = s2; //
#      goto genmask;
     
#      // genmask: # is a loop, need to figure out how much of the dividend will be the remainder
#      while(s3 != 0)
#      {
#           int t0 = s0 & s6;
#           s4 = s4 + t0; //needs to append t0 to s4
#           s6 = s6 << 1;
#           s3 -= 1;
#           goto genmask;
#      }
#      return s4;
# }
 
main:
    #print out dividend prompt
    ori $v0, $0, 4
    add $a0, $0, $0
    lui $a0, 0x1001
    syscall
 
    #read in user input for dividend
    ori $v0, $0, 5
    syscall
 
    add $s0, $v0, $0
 
    ########################################
 
    #print out divisor prompt
    ori $v0, $0, 4
    add $a0, $0, $0
    lui $a0, 0x1001
    ori $a0, $a0, 0x16
    syscall
 
    #read in user input for divisor
    ori $v0, $0, 5
    syscall
    add $s1, $v0, $0
 
    ########################################
    # this is just a separated 2 lines to prepare for the rest of the program
    prepreloops:
    addi $s6, $0, 1 #int s6 = 1; //$s6 holds 1 at LSB. will use it to generate mask
    add $s4, $0, $0 #int s4 = 0 //clear $s4
 
 
    #this loop is used to track what value the divisor is (2^x), finding x.
   
    #check if the divisor is 0 yet. if it is, branch to preloop2. if divisor is not 0 yet, jump back up to loop1 and redo the process
    loop1:
    srl $s1, $s1, 1 #s1 = s1 >>> 1; keep shifting right and replace the vacancy at the MSB with 0
    addi $s2, $s2, 1 #s2 = s2 + 1; need to remember how many times to divide the dividend by 2, so increment an initially-zero variable
    beq $s1, $0, preloop2 #if(s1 == 0): goto preloop2;
 
    j loop1 #  else: goto loop1;
 
    # this preloop2 happens 1 time to prepare for the bit masking process
    preloop2:
    # MAKE ANOTHER COPY OF COUNTER TO USE TO GENERATE MASKING
    addi $s2, $s2, -1 # s2 -= 1; //need to decrement the counter for loop2 by 1 because the number of shifts until the divisor is 0 is increased by 1 because the divisor will always have a 0 at the end of its binary value, and the dividend shouldn't be shifted an extra time because of it
    add $s3, $s2, $0 # s3 = s2; //making a copy of the counter for loop 2 for use in the bit masking process (modifiable s3).
    j genmask #goto genmask
 
    genmask: #// genmask: # is a loop, need to figure out how much of the dividend will be the remainder
    beq $s3, $0, end #while(s3 != 0){ ...}. once the s3 counter reaches 0, the brunt of the code is done, just need to print out the end prompt/result and exit the program.
    and $t0, $s0, $s6 #int t0 = s0 & s6; -- need to temporarily store the current value of the dividend in a register
    add $s4, $s4, $t0 #s4 = s4 + t0; -- need to increment the s4 final result with the latest bit
    sll $s6, $s6, 1 #s6 = s6 << 1; -- need to shift the '1' bit in the s6 register to finish properly masking the dividend and determine what the result is
    addi $s3, $s3, -1 #s3 -= 1; -- need to decrement the genmask loop's counter
    j genmask #goto genmask; -- need to check the loop's condition for whether still need to do the masking or if everything is fully complete. if complete, goes to end and does the printing of end prompt/result/exits the program
 
 
    #print out answer
    end:
    #print end prompt
    ori $v0, $0, 4
    lui $a0, 0x1001
    ori $a0, $a0, 0x2C
    syscall
 
    #clear a0
    #move mod'd value into a0
    #syscall to print number
    add $a0, $a0, $0
    add $a0, $s4, $0
    ori $v0, $0, 1
    syscall
   
    # Exit (load 10 into $v0)
    ori $v0, $0, 10
    syscall
 
