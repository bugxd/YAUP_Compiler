beq $v0, 0, L1
j writeboolean_end
L1:		# write 'False'
main_end:		# main_epilogue
li $v0 10
syscall
