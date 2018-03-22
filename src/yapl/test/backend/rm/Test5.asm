swap:			# 
	lw		lw		lw		addi	$sp,	0,	16# local variable tmp
	lw	$t0,	0($sp)
	lw	$t1,	0($sp)
	mul	$t1,	$t1,	4
	add	$t0,	$t0,	$t1
	div	$t1,	$t1,	4
	lw	$t0,	($t0)
	sw	$t0,	16($sp)
	lw	$t0,	0($sp)
	lw	$t1,	0($sp)
	lw	$t2,	0($sp)
	lw	$t3,	0($sp)
	mul	$t3,	$t3,	4
	add	$t2,	$t2,	$t3
	div	$t3,	$t3,	4
	lw	$t2,	($t2)
	mul	$t1,	$t1,	4
	add	$t1,	$t0,	$t1
	div	$t1,	$t1,	4
	sw	$t2,	($t1)
	lw	$t0,	0($sp)
	lw	$t1,	0($sp)
	lw	$t2,	16($sp)
	mul	$t1,	$t1,	4
	add	$t1,	$t0,	$t1
	div	$t1,	$t1,	4
	sw	$t2,	($t1)
# global variable a
# 
	li	$t0,	32
	sb	$t0,	4($gp)
	li	$t0,	58
	sb	$t0,	5($gp)
	li	$t0,	32
	sb	$t0,	6($gp)
	sb	$zero,	7($gp)
.globl main
main:			# main function entry point
	li	$t0,	3
	add	$t0,	0,	$gp
	sw	$t0,	0($gp)
	lw	$t0,	0($gp)
	li	$t1,	0
	li	$t2,	1
	mul	$t1,	$t1,	4
	add	$t1,	$t0,	$t1
	div	$t1,	$t1,	4
	sw	$t2,	($t1)
	lw	$t0,	0($gp)
	li	$t1,	1
	li	$t2,	2
	mul	$t1,	$t1,	4
	add	$t1,	$t0,	$t1
	div	$t1,	$t1,	4
	sw	$t2,	($t1)
	lw	$t0,	0($gp)
	li	$t1,	2
	li	$t2,	3
	mul	$t1,	$t1,	4
	add	$t1,	$t0,	$t1
	div	$t1,	$t1,	4
	sw	$t2,	($t1)
	lw	$t0,	0($gp)
	li	$t1,	1
	li	$t2,	2
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
	addi	$sp,	0,	4# pass argument
	sw	$t1,	0($sp)
	addi	$sp,	0,	4# pass argument
	sw	$t2,	0($sp)
	lw	$t0,	0($gp)
	li	$t1,	0
	mul	$t1,	$t1,	4
	add	$t0,	$t0,	$t1
	div	$t1,	$t1,	4
	lw	$t0,	($t0)
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
	li	$v0,	4
	la	$a0,	4($gp)
	syscall
	lw	$t0,	0($gp)
	li	$t1,	1
	mul	$t1,	$t1,	4
	add	$t0,	$t0,	$t1
	div	$t1,	$t1,	4
	lw	$t0,	($t0)
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
	li	$v0,	4
	la	$a0,	4($gp)
	syscall
	lw	$t0,	0($gp)
	li	$t1,	2
	mul	$t1,	$t1,	4
	add	$t0,	$t0,	$t1
	div	$t1,	$t1,	4
	lw	$t0,	($t0)
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
main_end:			# main_epilogue
	li	$v0,	10
	syscall
