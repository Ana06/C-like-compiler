	ssp 5;
	sep 50;
	ujp _begin;
main:
	ssp 7;
	sep :n_main;
	ldc 376;
	str 0 5;
	ujp e0;
imagenEspecular1:
	ssp 7;
	sep :n_imagenEspecular;
	lda 0 6;
	lda 0 5;
	ind;
	ldc 10;
	lda 0 5;
	ind;
	ldc 10;
	div;
	mul;
	sub;
	sto;
	lda 0 5;
	lda 0 5;
	ind;
	ldc 10;
	div;
	sto;
e1:
	lda 0 5;
	ind;
	ldc 0;
	grt;
	fjp e2;
	mst 0;
	cup 0 e3;
	ujp e4;
e3:
	ssp 5;
	sep :X;
	lda 1 6;
	lda 1 6;
	ind;
	ldc 10;
	mul;
	lda 1 5;
	ind;
	ldc 10;
	lda 1 5;
	ind;
	ldc 10;
	div;
	mul;
	sub;
	add;
	sto;
	lda 1 5;
	lda 1 5;
	ind;
	ldc 10;
	div;
	sto;
	retp;
e4:
	ujp e1;
e2:
	lda 0 6;
	ind;
	str 0 0;
	retf;
e0:
	lda 0 6;
	mst 0;
	lda 0 5;
	ind;
	cup 1 imagenEspecular1;
	sto;
	lda 0 6;
	ind;
	str 0 0;
	retf;
_begin:
	mst 0;
	cup 0 main;
	stp;