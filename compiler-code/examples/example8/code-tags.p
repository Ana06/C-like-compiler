	ssp 5;
	sep 50;
	ldc 4;
	str 0 5;
	ujp _begin;
factorial0:
	ssp 7;
	sep :n_factorial;
	ldc 0;
	str 0 6;
	lda 0 5;
	ind;
	ldc 1;
	grt;
	fjp e0;
	mst 0;
	cup 0 e1;
	ujp e2;
e1:
	ssp 5;
	sep :X;
	lda 1 6;
	lda 1 5;
	ind;
	mst 2;
	lda 1 5;
	ind;
	ldc 1;
	sub;
	cup 1 factorial0;
	mul;
	sto;
	retp;
e2:
	ujp e3;
e0:
	mst 0;
	cup 0 e4;
	ujp e5;
e4:
	ssp 5;
	sep :X;
	lda 1 6;
	ldc 1;
	sto;
	retp;
e5:
e3:
	lda 0 6;
	ind;
	str 0 0;
	retf;
main:
	ssp 5;
	sep :n_main;
	mst 1;
	lda 1 5;
	ind;
	cup 1 factorial0;
	str 0 0;
	retf;
_begin:
	mst 0;
	cup 0 main;
	stp;