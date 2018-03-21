main:		# main function entry point
	li	$v0,	7
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
main_end:		# main_epilogue
.globl main
	li	$v0,	10
	syscall
