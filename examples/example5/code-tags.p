	ssp 5;
	sep 50;
	ldc 3;
	str 0 5;
	ujp _begin;
main:
	ssp 6;
	sep :n_main;
	ldc 5;
	str 0 5;
	lda 0 5;
	ind;
	ldc 5;
	equ;
	fjp e0;
	mst 0;
	cup 0 e1;
	ujp e2;
e1:
	ssp 5;
	sep :X;
	mst 0;
	cup 0 e3;
	ujp e4;
e3:
	ssp 5;
	sep :X;
	mst 0;
	cup 0 e5;
	ujp e6;
e5:
	ssp 6;
	sep :X;
	lda 0 5;
	ldc 1;
	sto;
	retp;
e6:
	lda 2 5;
	lda 2 5;
	ind;
	ldc 1;
	add;
	sto;
	retp;
e4:
	lda 1 5;
	lda 1 5;
	ind;
	ldc 1;
	add;
	sto;
	retp;
e2:
e0:
	lda 0 5;
	ind;
	str 0 0;
	retf;
_begin:
	mst 0;
	cup 0 main;
	stp;