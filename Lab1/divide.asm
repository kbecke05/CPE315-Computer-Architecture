# Name:  Kelly Becker and Sindhu Srivats
# Section:  07
# Description:  Takes a user inputted upper and lower 32 bits and divisor, returns the divided upper and lower 32 bits

.globl promptUpper
.globl promptLower
.globl promptDivisor
.globl endpromptUpper
.globl endpromptLower
 
.data
promptUpper:
    .asciiz " Enter the upper 32 bits: "
promptLower:
    .asciiz " Enter the lower 32 bits: "
promptDivisor:
    .asciiz " Enter the divisor: "
endpromptUpper:
    .asciiz "\n Division result (upper 32 bits): "
endpromptLower:
    .asciiz "\n Division result (lower 32 bits): "
 
.text
#s0 = user input for upper 32 bits
#s1 = user input for lower 32 bits
#s2 = user input for divisor
#s4 = loop 2 counter
#s6 = value to mask the lower 32 bits with to shift in a 1!!!
 
# public static int divide(int upper32, int lower32, int divisor)
# {
#      int s0 = upper32; //s0/upper holds user input for upper 32 bits
#      int s1 = lower32; //s1/lower holds user input for lower 32 bits
#      int s2 = divisor; //s2/divisor holds user input for divisor
#      int s4; //s4 holds the counter for loop2
#      int s6; //s6 makes MSB of 32 bit value as 1
     
#      //prepreloops:
#      int s6 = 1; //$s6 holds 1 at LSB. will shift it to generate mask
#      s6 = s6 << 31; //need to shift the 1 as LSB to 1 as MSB for masking
     
#      //preloops: #first loop, will logically shift the divisor right by 1 until divisor = 0
#      s2 = s2 >>> 1;
#      s4 = s4 + 1; //incrementing s4 so it's value acts as a counter for loop 2
#      if(s2 == 0):
#           goto preloop2;
#      else:
#           goto preloops; //and repeat the current process until divisor is 0
     
     
#      //preloop2:
#      s4 -= 1; //need to decrement the counter for loop2 by 1 because the number of shifts until the divisor is 0 is increased by 1 because the divisor will always have a 0 at the end of its binary value, and the two sets of bits shouldn't be shifted an extra time because of it
#      goto loop2; //after decrementing the counter by 1, can go to the loop and start the division/shifting process
     
#      // loop2: shifts the upper 32 bits and/to the lower 32 bits by 1 power of 2 at a time until the divisor's counter (s4) reaches 0.
#      while(s4 != 0)
#      {
#           int s5 = s0 & 1; //s5 is used to determine what the LSB of the upper 32 bits is.
#           if(s5 != 0):
#                goto upperLSBOne;
#           else:
#                continue;
#           s0 = s0 >> 1; //regardless, upper 32 bits gets shifted to the right (logical) so the newly empty MSB gets replaced with a 0
#           s1 = s1 >> 1; //lower 32 bits also gets shifted to the right (logical) so the newly empty MSB gets replaced with a 0 here (because the upper 32 bits' old LSB was 0)
#           s4 -= 1; //decrement the loop counter by 1 and go back up to loop2
#           goto loop2;
         
#           upperLSBOne:
#           s1 = s1 >>> 1; //need to shift all bits of the lower 32 bits register by 1 slot. MSB becomes 1 in the next line
#           s1 = s1 | s6; //need to or the MSB of lower 32 bits register with 1, so it'll convert to 1
#           s0 = s0 >>> 1; //need to shift all bits of the upper 32 bits register by 1 slot. MSB becomes 0.
#           s4 -= 1; //decrement the loop counter by 1 and go back up to loop2
#      }
#      print("new upper 32 bits: ", s0);
#      print("new lower 32 bits: ", s1);
#      return 0;
# }
 
main:
    #print out upper 32 bits prompt
    ori $v0, $0, 4
    add $a0, $0, $0
    lui $a0, 0x1001
    syscall
 
    #read in user input for upper 32 bits
    ori $v0, $0, 5
    syscall
    add $s0, $v0, $0
 
    ########################################
 
    #print out lower 32 bits prompt
    ori $v0, $0, 4
    add $a0, $0, $0
    lui $a0, 0x1001
    ori $a0, $a0, 0x1B
    syscall
 
    #read in user input for lower 32 bits
    ori $v0, $0, 5
    syscall
    add $s1, $v0, $0
 
    ########################################
 
    #print out divisor prompt
    ori $v0, $0, 4
    add $a0, $0, $0
    lui $a0, 0x1001
    ori $a0, $a0, 0x36
    syscall
 
    #read in user input for divisor
    ori $v0, $0, 5
    syscall
    add $s2, $v0, $0
 
    ########################################
 
    # JUST GET THE MSB OF A 32 BIT NUMBER TO BE 1 FOLLOWED BY 31 ZEROES (NEED IT FOR MASKING LOWER 32 BITS' REGISTER)
    prepreloops:
    addi $s6, $0, 1 #//$s6 holds 1 at LSB. will shift it to generate mask
    sll $s6, $s6, 31 #need to shift the '1' as current s6 LSB to 1 as MSB for masking
 
    ########################################
    # figure out how many times need to shift both the upper and lower registers by -- need to determine what power of 2 the divisor is raised to
    preloops: #//preloops: #first loop, will logically shift the divisor right by 1 until divisor = 0
    srl $s2, $s2, 1 #s2 = s2 >>> 1;
    addi $s4, $s4, 1 #s4 = s4 + 1; //incrementing s4 so it's value acts as a counter for loop 2
    beq $s2, $0, preloop2 # if(s2 == 0): goto preloop2;
    j preloops #else: goto preloops; and repeat the current process until divisor is 0
 
   
    preloop2:
    addi $s4, $s4, -1 #//need to decrement the counter for loop2 by 1 because the number of shifts until the divisor is 0 is increased by 1 because the divisor will always have a 0 at the end of its binary value, and the two sets of bits shouldn't be shifted an extra time because of it
    j loop2 #goto loop2; -- after decrementing the counter by 1, can go to the loop and start the division/shifting process
 
    # while counter register>0, need to find the value of the LSB of upper 32 bits and store in s5 register.
    # if its not 0, need to shift in a 1 into MSB of lower 32 bits, and need to branch to the upperLSBOne label.
    # if it is 0, shift the upper 32 bits right, lower 32 bits right, and decrement the loop counter.
    loop2:
    beq $s4, $0, end #while(s4 != 0){
    andi $s5, $s0, 1 #int s5 = s0 & 1; //s5 is used to determine what the LSB of the upper 32 bits is.
    bne $s5, $0, upperLSBOne #if(s5 != 0): goto upperLSBOne;
    srl $s0, $s0, 1 #s0 = s0 >> 1; #regardless, upper 32 bits gets shifted to the right (logical) so the newly empty MSB gets replaced with a 0
    srl $s1, $s1, 1 #s1 = s1 >> 1; #lower 32 bits also gets shifted to the right (logical) so the newly empty MSB gets replaced with a 0 here (because the upper 32 bits' old LSB was 0)
    addi $s4, $s4, -1 #decrement the loop counter by 1 and go back up to loop2
    j loop2
 
    # since LSB of upper 32 bits register is 1, need to shift the lower 32 bits right, then put a 1 in the MSB of lower 32 bits register.
    # then shift the upper 32 bits right and decrement the loop counter.
    upperLSBOne:
    # mask MSB of lower 32 bits register
    srl $s1, $s1, 1 #s1 = s1 >>> 1; //need to shift all bits of the lower 32 bits register by 1 slot. MSB becomes 1 in the next line
    or $s1, $s1, $s6 #s1 = s1 | s6; //need to or the MSB of lower 32 bits register with 1, so it'll convert to 1
    srl $s0, $s0, 1 #s0 = s0 >>> 1; //need to shift all bits of the upper 32 bits register by 1 slot. MSB becomes 0.
    addi $s4, $s4, -1 #s4 -= 1; //decrement the loop counter by 1 and go back up to loop2
    j loop2
 
 
    #print out upper then lower reg
    end:
    #print end prompt
    # ASK HIM TOMORROW HOW TO GET THE END PROMPT PRINTING
    ori $v0, $0, 4
    lui $a0, 0x1001
    ori $a0, $a0, 0x4B
    syscall
 
    #clear a0
    #move new upper 32 bits value into a0
    #syscall to print upper reg
    add $a0, $0, $0
    add $a0, $s0, $0
    ori $v0, $0, 1
    syscall
   
    #print out lower reg
    ori $v0, $0, 4
    lui $a0, 0x1001
    ori $a0, $a0, 0x6F
    syscall
 
    #clear a0
    #move new lower 32 bits value into a0
    #syscall to print lower reg
    add $a0, $0, $0
    add $a0, $s1, $0
    ori $v0, $0, 1
    syscall
   
    # Exit (load 10 into $v0)
    ori $v0, $0, 10
    syscall
 
