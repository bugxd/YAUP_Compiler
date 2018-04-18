.globl main
main:			# main function entry point
	li	$t0,	7
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
main_end:			# main_epilogue
	li	$v0,	10
	syscall
