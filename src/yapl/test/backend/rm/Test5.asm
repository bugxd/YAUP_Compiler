swap:		# 
	lw		lw		lw		addi	$sp,	0,	16	local variable tmp
	lw	$v0,	0($sp)
	lw	$v1,	0($sp)
	mul	$v1,	$v1,	4
	add	$v0,	$v0,	$v1
	div	$v1,	$v1,	4
	lw	$v0,	($v0)
	sw	$v0,	16($sp)
	lw	$v0,	0($sp)
	lw	$v1,	0($sp)
	lw	$a0,	0($sp)
	lw	$a1,	0($sp)
	mul	$a1,	$a1,	4
	add	$a0,	$a0,	$a1
	div	$a1,	$a1,	4
	lw	$a0,	($a0)
	mul	$v1,	$v1,	4
	add	$v1,	$v0,	$v1
	div	$v1,	$v1,	4
	sw	$a0,	($v1)
	lw	$v0,	0($sp)
	lw	$v1,	0($sp)
	lw	$a0,	16($sp)
	mul	$v1,	$v1,	4
	add	$v1,	$v0,	$v1
	div	$v1,	$v1,	4
	sw	$a0,	($v1)
main:		# main function entry point
	li	$v0,	3
	add	$v0,	0,	$gp
	sw	$v0,	0($gp)
	lw	$v0,	0($gp)
	li	$v1,	0
	li	$a0,	1
	mul	$v1,	$v1,	4
	add	$v1,	$v0,	$v1
	div	$v1,	$v1,	4
	sw	$a0,	($v1)
	lw	$v0,	0($gp)
	li	$v1,	1
	li	$a0,	2
	mul	$v1,	$v1,	4
	add	$v1,	$v0,	$v1
	div	$v1,	$v1,	4
	sw	$a0,	($v1)
	lw	$v0,	0($gp)
	li	$v1,	2
	li	$a0,	3
	mul	$v1,	$v1,	4
	add	$v1,	$v0,	$v1
	div	$v1,	$v1,	4
	sw	$a0,	($v1)
	lw	$v0,	0($gp)
	li	$v1,	1
	li	$a0,	2
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
	addi	$sp,	0,	4	pass argument
	sw	$v1,	0($sp)
	addi	$sp,	0,	4	pass argument
	sw	$a0,	0($sp)
	lw	$v0,	0($gp)
	li	$v1,	0
	mul	$v1,	$v1,	4
	add	$v0,	$v0,	$v1
	div	$v1,	$v1,	4
	lw	$v0,	($v0)
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
	lw	$v0,	0($gp)
	li	$v1,	1
	mul	$v1,	$v1,	4
	add	$v0,	$v0,	$v1
	div	$v1,	$v1,	4
	lw	$v0,	($v0)
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
	lw	$v0,	0($gp)
	li	$v1,	2
	mul	$v1,	$v1,	4
	add	$v0,	$v0,	$v1
	div	$v1,	$v1,	4
	lw	$v0,	($v0)
	addi	$sp,	0,	4	pass argument
	sw	$v0,	0($sp)
main_end:		# main_epilogue
.globl main
	li	$v0,	10
	syscall
