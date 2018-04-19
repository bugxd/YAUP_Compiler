# global variable
# 
	li	$t0,	32
	sb	$t0,	4($gp)
	li	$t0,	58
	sb	$t0,	5($gp)
	li	$t0,	32
	sb	$t0,	6($gp)
	sb	$zero,	7($gp)
func:			# 
	lw		lw	$t0,	0($gp)
	lw	$t1,	0($sp)
	lw	$t2,	0($sp)
	mul	$t1,	$t1,	$t2
	sub	$t0,	$t0,	$t1
.globl main
main:			# main function entry point
	addi	$sp,	0,	16# local variable
	li	$t0,	17
	sw	$t0,	0($gp)
	li	$t0,	3
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
	sw	$t0,	16($sp)
	lw	$t0,	16($sp)
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
	li	$v0,	4
	la	$a0,	4($gp)
	syscall
	lw	$t0,	16($sp)
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
main_end:			# main_epilogue
	li	$v0,	10
	syscall
