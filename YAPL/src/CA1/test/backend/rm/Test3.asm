# 
	li	$t0,	84
	sb	$t0,	0($gp)
	li	$t0,	114
	sb	$t0,	1($gp)
	li	$t0,	117
	sb	$t0,	2($gp)
	li	$t0,	101
	sb	$t0,	3($gp)
	sb	$zero,	4($gp)
# 
	li	$t0,	70
	sb	$t0,	20($gp)
	li	$t0,	97
	sb	$t0,	21($gp)
	li	$t0,	108
	sb	$t0,	22($gp)
	li	$t0,	115
	sb	$t0,	23($gp)
	li	$t0,	101
	sb	$t0,	24($gp)
	sb	$zero,	25($gp)
# 
	li	$t0,	32
	sb	$t0,	44($gp)
	li	$t0,	58
	sb	$t0,	45($gp)
	li	$t0,	32
	sb	$t0,	46($gp)
	sb	$zero,	47($gp)
writeboolean:			# 
	lw		lw	$t0,	0($sp)
	beq	$t0,	0,	L1
	li	$v0,	4
	la	$a0,	0($gp)
	syscall
j writeboolean_end
L1:			# write 'False'
	li	$v0,	4
	la	$a0,	20($gp)
	syscall
.globl main
main:			# main function entry point
	li	$t0,	1
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
	li	$v0,	4
	la	$a0,	44($gp)
	syscall
	li	$t0,	0
	addi	$sp,	0,	4# pass argument
	sw	$t0,	0($sp)
main_end:			# main_epilogue
	li	$v0,	10
	syscall
