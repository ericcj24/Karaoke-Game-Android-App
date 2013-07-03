#include <jni.h>
#include "fftw3/include/fftw3.h"
#include <math.h> //for cos and log10 functions

JNIEXPORT int JNICALL Java_org_ece420_FinalProject_Lab4Activity_process
  (JNIEnv *env, jclass obj, jobject inbuf, jobject outbuf, jint N)
{
	int i;
	int j;
	short* inBuf = (short*)(*env)->GetDirectBufferAddress(env,inbuf);
	double* outBuf = (double*)(*env)->GetDirectBufferAddress(env,outbuf);

	int outval=0;
	double temp_sum;
	double xcorr[N];

	int f = 8000;//8000
	int lowr = floor(f/500);
	int upr = ceil(f/75);
	int maxv=0;
	int maxp=0;
	int temp_sum1;
	double temp_sum2;


	//voice detection
	temp_sum2 = 0;
	for (i=0;i<N;i++){
		temp_sum2 = temp_sum2 + (double)inBuf[i]*(double)inBuf[i];
	}


	if (temp_sum2>50000000){//50000000



	// autocorrelation
	for (i=0;i<N;i++){
		temp_sum1 = 0;
		for (j=0;j<=N-i-1;j++){
			temp_sum1 = temp_sum1 +inBuf[i+j]*inBuf[j];
		}
		xcorr[i]=temp_sum1;
	}

	maxv =xcorr[lowr];
	maxp = lowr;
	for (i=lowr;i<= upr; i++){
		if (xcorr[i]>maxv){
			maxv = xcorr[i];
			maxp = i;
		}
	}

	outval = (int)f/maxp;



	/***************************** Jian Chen ********************************/
	double w[N];
	double temp[N];
	for (i=0;i<N;i++)
	{
		w[i] = 0.54-0.45*cos(2*3.1415926*i/N);
	}
	for (i=0;i<N;i++)
	{
		temp[i] = ((double)inBuf[i])*w[i];
	}
	fftw_plan my_plan;
	fftw_complex *in, *out;
	/*in = (fftw_complex*) fftw_malloc(sizeof(fftw_complex)*2*N);
	out = (fftw_complex*) fftw_malloc(sizeof(fftw_complex)*2*N);
	my_plan = fftw_plan_dft_1d(2*N, in, out, FFTW_FORWARD, FFTW_ESTIMATE);
	 */
	in = (fftw_complex*) fftw_malloc(sizeof(fftw_complex)*16*N);//2
	out = (fftw_complex*) fftw_malloc(sizeof(fftw_complex)*16*N);//2
	my_plan = fftw_plan_dft_1d(16*N, in, out, FFTW_FORWARD, FFTW_ESTIMATE);//2

	for (i=0;i<N;i++)
	{
		in[i][0] = temp[i];
		in[i][1] = 0;
	}
	for (i=N;i<(16*N);i++)//2*N
	{
		in[i][0] = 0;
		in[i][1] = 0;
	}

	fftw_execute(my_plan);

	double temp1[N];
	for (i=0;i<N;i++)
	{
		temp1[i] = log10(out[i][0]*out[i][0] + out[i][1]*out[i][1]);

		if (temp1[i]>12)
		{
			temp1[i] = 12;
		}
		else if(temp1[i]<7)
		{
			temp1[i] = 7;
		}
		outBuf[i] = (temp1[i]*0.2)-1.4; //(12.5 6.5;1/6 5/6) (1/6 -1; 12,6)


		// overwrite to emphasize the pitch
		// *8*4000 now //
		if((i-(int)((double)outval*(double)128/(double)4000*16))<4 && (i-(int)((double)outval*(double)128/(double)4000*16))>0)
			outBuf[i] = 1;

	}

	fftw_destroy_plan(my_plan);
	fftw_free(in);
	fftw_free(out);
	/***************************** Jian Chen ********************************/
	return outval;
	//return temp_sum2;

	}
	else{
		for (i=0;i<N;i++){
			outBuf[i] = 0;
		}
		return outval = 0;
	}




}
