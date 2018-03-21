main:		# main function entry point
	addi	$sp,	0,	16	local variable r
	sw	$v0,	16($sp)
	lw	$v0,	16($sp)
	li	$v1,	5
	sw	$v1,	($v0)
	lw	$v0,	16($sp)
	add	$v0,	$v0,	4
	sw	$v1,	($v0)
	lw	$v0,	16($sp)
	lw	$v0,	4($v0)
	li	$v1,	10
	sw	$v1,	($v0)
	lw	$v0,	16($sp)
	lw	$v1,	($v0)
	addi	$sp,	0,	4	pass argument
	sw	$v1,	0($sp)
	lw	$v0,	16($sp)
	lw	$v0,	4($v0)
	lw	$v1,	0($v0)
	addi	$sp,	0,	4	pass argument
	sw	$v1,	0($sp)
main_end:		# main_epilogue
.globl main
	li	$v0,	10
	syscall
