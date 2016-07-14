	ssp 5;
	sep 50;
	ujp _begin;
c0:
	ssp 6;
	sep :n_c;
	lda 0 5;
	ind;
	ldc 1;
	add;
	str 0 0;
	retf;
main:
	ssp 6;
	sep :n_main;
	lda 0 5;
	ldc 5;
	sto;
	ldc 77;
	mst 1;
	lda 0 5;
	ind;
	cup 1 c0;
	add;
	str 0 0;
	retf;
_begin:
	mst 0;
	cup 0 main;
	stp;