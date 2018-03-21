mul $v1, $v1, $a0
sub $v0, $v0, $v1
main_end:		# main_epilogue
li $v0 10
syscall
