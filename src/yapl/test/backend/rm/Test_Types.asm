	# PREDEFINED PROCEDURES START
.text
writeint:				# 
	# make place for return address
	addi	$sp	$sp	-4
	sw	$ra,	-4($fp)
	# store all saved and temporary registers
	addi	$sp	$sp	-4
	sw	$t0,	-8($fp)
	sw	$t1,	-12($fp)
	sw	$t2,	-16($fp)
	sw	$t3,	-20($fp)
	sw	$t4,	-24($fp)
	sw	$t5,	-28($fp)
	sw	$t6,	-32($fp)
	sw	$t7,	-36($fp)
	sw	$t8,	-40($fp)
	sw	$t9,	-44($fp)
	sw	$s0,	-48($fp)
	sw	$s1,	-52($fp)
	sw	$s2,	-56($fp)
	sw	$s3,	-60($fp)
	sw	$s4,	-64($fp)
	sw	$s5,	-68($fp)
	sw	$s6,	-72($fp)
	# Finished storing registers!!
	lw	$t0,	-76($fp)
	move	$a0,	$t0
	li	$v0,	1
	syscall
	j writeint_end
writeint_end:				# procedure_epilogue
	move	$sp,	$fp
	# Eliminate place on stack where preserved arguments (old frame pointer) were stored
	add	$sp,	$sp,	4
	# load stored registers
	lw	$t0,	-8($fp)
	lw	$t1,	-12($fp)
	lw	$t2,	-16($fp)
	lw	$t3,	-20($fp)
	lw	$t4,	-24($fp)
	lw	$t5,	-28($fp)
	lw	$t6,	-32($fp)
	lw	$t7,	-36($fp)
	lw	$t8,	-40($fp)
	lw	$t9,	-44($fp)
	lw	$s0,	-48($fp)
	lw	$s1,	-52($fp)
	lw	$s2,	-56($fp)
	lw	$s3,	-60($fp)
	lw	$s4,	-64($fp)
	lw	$s5,	-68($fp)
	lw	$s6,	-72($fp)
	# Load old framepointer and return address
	lw	$ra,	-4($fp)
	lw	$fp,	0($fp)
	jr	$ra

	# PREDEFINED PROCEDURES END
.globl main
main:				# main function entry point
	# Set $fp to content of $sp which is top of the stack frame
	move	$fp,	$sp
.data
.asciiz	" : "
.align 2
.text
	add	$t1,	$zero,	5
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t1,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32768($gp)
	syscall
	mul	$t0,	$t1,	-1
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t0,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32768($gp)
	syscall
	add	$t0,	$zero,	10
	div	$t2,	$t0,	$t1
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t2,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32768($gp)
	syscall
	add	$t0,	$zero,	10
	div	$t0,	$t1
	mfhi	$t2
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t2,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32768($gp)
	syscall
	not	$t2,	$t0
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t2,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32768($gp)
	syscall
	and	$t2,	$t0,	$t1
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t2,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32768($gp)
	syscall
	or	$t2,	$t0,	$t1
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t2,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32768($gp)
	syscall
	slt	$t2,	$t0,	$t1
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t2,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32768($gp)
	syscall
	seq	$t2,	$t0,	$t1
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t2,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32768($gp)
	syscall
	sle	$t2,	$t0,	$t1
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t2,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32768($gp)
	syscall
	li	$t3,	4
	move	$s7,	$t3
	move	$a0,	$s7
	add	$a0,	$a0,	1
	mul	$a0,	$a0,	4
	li	$v0,	9
	syscall
	move	$t4,	$v0
	sw	$s7,	($t4)
	lw	$t3,	($t4)
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t3,	-76($fp)
	jal	writeint
exit:				# main_epilogue
	li	$v0,	10
	syscall
