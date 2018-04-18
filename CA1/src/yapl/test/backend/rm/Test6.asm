# 
	li	$t0,	32
	sb	$t0,	0($gp)
	li	$t0,	58
	sb	$t0,	1($gp)
	li	$t0,	32
	sb	$t0,	2($gp)
	sb	$zero,	3($gp)
.globl main
main:			# main function entry point
	addi	$sp,	0,	16# local variable r
	sw	$t0,	16($sp)
	lw	$t0,	16($sp)
	li	$t1,	5
	sw	$t1,	($t0)
	lw	$t0,	16($sp)
	add	$t0,	$t0,	4
	sw	$t1,	($t0)
	lw	$t0,	16($sp)
	lw	$t0,	4($t0)
	li	$t1,	10
	sw	$t1,	($t0)
	lw	$t0,	16($sp)
	lw	$t1,	($t0)
	addi	$sp,	0,	4# pass argument
	sw	$t1,	0($sp)
	li	$v0,	4
	la	$a0,	0($gp)
	syscall
	lw	$t0,	16($sp)
	lw	$t0,	4($t0)
	lw	$t1,	0($t0)
	addi	$sp,	0,	4# pass argument
	sw	$t1,	0($sp)
main_end:			# main_epilogue
	li	$v0,	10
	syscall
