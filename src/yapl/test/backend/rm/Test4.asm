func:		# 
	lw		lw	$v0,	0($gp)
	lw	$v1,	0($sp)
	lw	$a0,	0($sp)
	mul	$v1,	$v1,	$a0
	sub	$v0,	$v0,	$v1
main:		# main function entry point
	addi	$sp,	0,	16	local variable
	li	$v0,	17
	sw	$v0,	0($gp)
	li	$v0,	3
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
	sw	$v0,	16($sp)
	lw	$v0,	16($sp)
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
	lw	$v0,	16($sp)
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
main_end:		# main_epilogue
.globl main
	li	$v0,	10
	syscall
