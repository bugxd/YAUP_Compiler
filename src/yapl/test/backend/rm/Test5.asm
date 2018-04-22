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
swap:				# 
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
	# local variable tmp
	addi	$sp	$sp	-4
	lw	$t0,	-76($fp)
	lw	$t1,	-80($fp)
	add	$t4,	$t1,	$zero
	# multiply index with word size (4)
	mul	$t4,	$t4,	4
	# add word size (4) because a[0] is actually the array header and not the first element. so we have to shift by 1 element.
	add	$t4,	$t4,	4
	add	$t0,	$t0,	$t4
	lw	$t0,	($t0)
	sw	$t0,	-24($fp)
	lw	$t0,	-76($fp)
	lw	$t1,	-80($fp)
	lw	$t2,	-76($fp)
	lw	$t3,	-84($fp)
	add	$t4,	$t3,	$zero
	# multiply index with word size (4)
	mul	$t4,	$t4,	4
	# add word size (4) because a[0] is actually the array header and not the first element. so we have to shift by 1 element.
	add	$t4,	$t4,	4
	add	$t2,	$t2,	$t4
	lw	$t2,	($t2)
	add	$t4,	$t1,	$zero
	# multiply index with word size (4)
	mul	$t4,	$t4,	4
	# add word size (4) because a[0] is actually the array header and not the first element. so we have to shift by 1 element.
	add	$t4,	$t4,	4
	add	$t1,	$t0,	$t4
	sw	$t2,	($t1)
	lw	$t0,	-76($fp)
	lw	$t1,	-84($fp)
	lw	$t2,	-24($fp)
	add	$t4,	$t1,	$zero
	# multiply index with word size (4)
	mul	$t4,	$t4,	4
	# add word size (4) because a[0] is actually the array header and not the first element. so we have to shift by 1 element.
	add	$t4,	$t4,	4
	add	$t1,	$t0,	$t4
	sw	$t2,	($t1)
swap_end:				# procedure_epilogue
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

.data
.space 4	# global variable a
.align 2
.asciiz	" : "
.align 2
.text
.globl main
main:				# main function entry point
	# Set $fp to content of $sp which is top of the stack frame
	move	$fp,	$sp
	li	$t0,	3
	move	$s7,	$t0
	move	$a0,	$s7
	add	$a0,	$a0,	1
	mul	$a0,	$a0,	4
	li	$v0,	9
	syscall
	move	$t0,	$v0
	sw	$s7,	($t0)
	sw	$t0,	32768($gp)
	lw	$t0,	32768($gp)
	li	$t1,	0
	li	$t2,	1
	add	$t3,	$t1,	$zero
	# multiply index with word size (4)
	mul	$t3,	$t3,	4
	# add word size (4) because a[0] is actually the array header and not the first element. so we have to shift by 1 element.
	add	$t3,	$t3,	4
	add	$t1,	$t0,	$t3
	sw	$t2,	($t1)
	lw	$t0,	32768($gp)
	li	$t1,	1
	li	$t2,	2
	add	$t3,	$t1,	$zero
	# multiply index with word size (4)
	mul	$t3,	$t3,	4
	# add word size (4) because a[0] is actually the array header and not the first element. so we have to shift by 1 element.
	add	$t3,	$t3,	4
	add	$t1,	$t0,	$t3
	sw	$t2,	($t1)
	lw	$t0,	32768($gp)
	li	$t1,	2
	li	$t2,	3
	add	$t3,	$t1,	$zero
	# multiply index with word size (4)
	mul	$t3,	$t3,	4
	# add word size (4) because a[0] is actually the array header and not the first element. so we have to shift by 1 element.
	add	$t3,	$t3,	4
	add	$t1,	$t0,	$t3
	sw	$t2,	($t1)
	lw	$t0,	32768($gp)
	li	$t1,	1
	li	$t2,	2
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-4($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-12
	sw	$t0,	-76($fp)
	sw	$t1,	-80($fp)
	sw	$t2,	-84($fp)
	jal	swap
	lw	$t0,	32768($gp)
	li	$t1,	0
	add	$t3,	$t1,	$zero
	# multiply index with word size (4)
	mul	$t3,	$t3,	4
	# add word size (4) because a[0] is actually the array header and not the first element. so we have to shift by 1 element.
	add	$t3,	$t3,	4
	add	$t0,	$t0,	$t3
	lw	$t0,	($t0)
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-12($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t0,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32772($gp)
	syscall
	lw	$t0,	32768($gp)
	li	$t1,	1
	add	$t3,	$t1,	$zero
	# multiply index with word size (4)
	mul	$t3,	$t3,	4
	# add word size (4) because a[0] is actually the array header and not the first element. so we have to shift by 1 element.
	add	$t3,	$t3,	4
	add	$t0,	$t0,	$t3
	lw	$t0,	($t0)
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-12($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t0,	-76($fp)
	jal	writeint
	li	$v0,	4
	la	$a0,	32772($gp)
	syscall
	lw	$t0,	32768($gp)
	li	$t1,	2
	add	$t3,	$t1,	$zero
	# multiply index with word size (4)
	mul	$t3,	$t3,	4
	# add word size (4) because a[0] is actually the array header and not the first element. so we have to shift by 1 element.
	add	$t3,	$t3,	4
	add	$t0,	$t0,	$t3
	lw	$t0,	($t0)
	# place for old frame pointer
	addi	$sp	$sp	-4
	sw	$fp,	-12($fp)
	move	$fp,	$sp
	# push stack frame
	addi	$sp	$sp	-4
	sw	$t0,	-76($fp)
	jal	writeint
main_end:				# main_epilogue
	li	$v0,	10
	syscall
