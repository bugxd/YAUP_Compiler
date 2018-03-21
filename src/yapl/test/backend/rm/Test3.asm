writeboolean:		# 
	lw		lw	$v0,	0($sp)
	beq	$v0,	0,	L1
j writeboolean_end
L1:		# write 'False'
main:		# main function entry point
	li	$v0,	1
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
	li	$v0,	0
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
main_end:		# main_epilogue
.globl main
	li	$v0,	10
	syscall
