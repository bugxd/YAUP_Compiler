.globl main
main:			# main function entry point
# 
	li	$t0,	72
	sb	$t0,	0($gp)
	li	$t0,	101
	sb	$t0,	1($gp)
	li	$t0,	108
	sb	$t0,	2($gp)
	li	$t0,	108
	sb	$t0,	3($gp)
	li	$t0,	111
	sb	$t0,	4($gp)
	li	$t0,	32
	sb	$t0,	5($gp)
	li	$t0,	119
	sb	$t0,	6($gp)
	li	$t0,	111
	sb	$t0,	7($gp)
	li	$t0,	114
	sb	$t0,	8($gp)
	li	$t0,	108
	sb	$t0,	9($gp)
	li	$t0,	100
	sb	$t0,	10($gp)
	li	$t0,	33
	sb	$t0,	11($gp)
	sb	$zero,	12($gp)
	li	$v0,	4
	la	$a0,	0($gp)
	syscall
main_end:			# main_epilogue
	li	$v0,	10
	syscall
