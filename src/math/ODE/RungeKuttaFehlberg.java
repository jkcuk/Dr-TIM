/*-----------------------------------------------------------------------------*\
|                                                                               |
| Java program adapted from MatPack C++ code by J. Courtial, 2000               |
|                                                                               |
| class RungeKuttaFehlberg - RKF integrator implementation        rungekutta.cc |
|                                                                               |
| Last change: May 25, 1997                                                     |
|                                                                               |
| MatPack Library Release 1.0                                                   |
| Copyright (C) 1991-1997 by Berndt M. Gammel                                   |
|                                                                               |
| Permission to  use, copy, and  distribute  Matpack  in  its entirety  and its |
| documentation  for non-commercial purpose and  without fee is hereby granted, |
| provided that this license information and copyright notice appear unmodified |
| in all copies.  This software is provided 'as is'  without express or implied |
| warranty.  In no event will the author be held liable for any damages arising |
| from the use of this software.                                                |
| Note that distributing Matpack 'bundled' in with any product is considered to |
| be a 'commercial purpose'.                                                    |
| The software may be modified for your own purposes, but modified versions may |
| not be distributed without prior consent of the author.                       |
|                                                                               |
| Read the  COPYRIGHT and  README files in this distribution about registration	|
| and installation of Matpack.                                                  |
|                                                                               |
\*-----------------------------------------------------------------------------*/

//-----------------------------------------------------------------------------//
// class RungeKuttaFehlberg
//-----------------------------------------------------------------------------//
//
// PURPOSE:
// -------
//   RKF45 is primarily designed to solve non-stiff and mildly stiff
//   differential equations when derivative evaluations are inexpensive.
//   RKF45 should generally not be used when the user is demanding
//   high accuracy.
//
// ABSTRACT:
// ---------
//   This routine integrates a system of N first order
//   ordinary differential equations of the form
//
//            dy(i)/dt = f(t,y(1),y(2), ... ,y(N))
//            where the y(i) are given at t.
//
//   Typically the routine is used to integrate from "t" to "tout" but it
//   can be used as a one-step integrator to advance the solution a
//   single step in the direction of "tout".  On return the parameters in
//   the call list and the class variables are set for continuing the 
//   integration. The user has only to call the solver again 
//   (and perhaps define a new value for tout).
//
//   The use of the class interface is illustrated by the example below:
//
//      Vector y(n1,n2);		// define the solution vector
//      RungeKuttaFehlberg rkf(y);	// create a Runge-Kutta solver
//
//   After defining the solution vector with indices running from
//   "n1" to "n2" (not neccesarilly from 1 to N) an instance of the
//   Runge-Kutta-solver "rkf" is created. "rkf" holds all neccessary
//   working variables. The working storage consists of (order+2) vectors
//   of the same size and the same index range as the solution vector "y".
//   For the fourth-fifth-order method of RKF45 order is 5.
//
//   The integration is performed by the member function
//
//     void RungeKuttaFehlberg::Integrate(
//		void (*Model)(double t, const Vector &y, Vector &yp),
//		Vector &y, 
//		double &t, double &tout, 
//		double relerr, double abserr,
//		int &iflag, 
//              int maxnfe = 3000)
//
//   The parameters represent:
//
//     void (*Model)(double t, const Vector &y, Vector &yp)
//        -- user supplied routine to evaluate derivatives yp(i)=dy(i)/dt
//
//     Vector &y
//        -- solution vector at t
//
//     double &t 
//        -- independent variable
//
//     double &tout 
//        -- output point at which solution is desired
//
//     double relerr
//     double abserr 
//        -- relative and absolute error tolerances for local error test. 
//           At each step the code requires that
//                abs(local error) <= relerr*abs(y) + abserr
//           for each component of the local error and solution vectors
//
//     int &iflag 
//        -- indicator for status of integration
//
//     int maxnfe
//        -- The expense is controlled by restricting the number
//           of function evaluations to be approximately maxnfe.
//           The default value of maxnfe = 3000 corresponds to about 500 steps.
//           If this is not sufficient it is likely that the routine is inefficient 
//           for solving this problem. Check the returned value of iflag !
//
//   The first call:
//
//     Vector &y  
//        -- vector of initial conditions
//
//     double &t 
//        -- starting point of integration
//
//     double &tout 
//        -- output point at which solution is desired.
//           t = tout is allowed on the first call only, in which case
//           returns with "iflag = RungeKuttaFehlberg::NormalStep" 
//           if continuation is possible.
//
//     double relerr
//     double abserr 
//        -- relative and absolute local error tolerances
//           which must be non-negative. The code should normally not be
//           used with relative error control smaller than about 1.0e-8.
//           To avoid limiting precision difficulties the code requires
//           relerr to be larger than an internally computed relative
//           error parameter which is machine dependent. In particular,
//           pure absolute error is not permitted. If a smaller than
//           allowable value of relerr is attempted, the routine increases
//           relerr appropriately and returns control to the user before
//           continuing the integration.
//
//     int iflag 
//        -- "RungeKuttaFehlberg::Start" or "RungeKuttaFehlberg::StartSingleStep"
//           indicator to initialize the code for each new
//           problem. Normal input is "iflag=RungeKuttaFehlberg::Start".
//           The user should set "iflag=RungeKuttaFehlberg::StartSingleStep"
//           only when one-step integrator control is essential. In this
//           case, the routine attempts to advance the solution a single step
//           in the direction of tout each time it is called. Since this
//           mode of operation results in extra computing overhead, it
//           should be avoided unless needed.
//
//
//   The output:
//
//     Vector &y  
//        -- the solution at t
//     double &t 
//         -- last point reached in integration.
//     int iflag 
//      = RungeKuttaFehlberg::NormalStep 
//               -- integration reached tout. indicates successful return
//                  and is the normal mode for continuing integration.
//      = RungeKuttaFehlberg::SingleStep
//               -- a single successful step in the direction of tout
//                  has been taken. Normal mode for continuing
//                  integration one step at a time.
//      = RungeKuttaFehlberg::SmallRelErrorBound
//               -- integration was not completed because relative error
//                  tolerance was too small. relerr has been increased
//                  appropriately for continuing.
//      = RungeKuttaFehlberg::TooManyIterations
//               -- integration was not completed because more than
//                  maxnfe (default = 3000) derivative evaluations were needed. This
//                  is approximately maxnfe/6 steps for the 5-th order method.
//      = RungeKuttaFehlberg::SmallAbsErrorBound
//               -- integration was not completed because solution
//                  vanished making a pure relative error test
//                  impossible. Must use non-zero abserr to continue.
//                  using the one-step integration mode for one step
//                  is a good way to proceed.
//      = RungeKuttaFehlberg::MinimumStepReached
//               -- integration was not completed because requested
//                  accuracy could not be achieved using smallest
//                  allowable stepsize. User must increase the error
//                  tolerance before continued integration can be
//                  attempted.
//      = RungeKuttaFehlberg::TooManyCalls
//               -- it is likely that the routine is inefficient for solving
//                  this problem. Too much output is restricting the
//                  natural stepsize choice. Use the one-step integrator mode.
//      = RungeKuttaFehlberg::InvalidParameters
//               -- invalid input parameters.
//                  This indicator occurs if any of the following is
//                  satisfied - y.Elements <= 0
//                            - t=tout  and  iflag != +1 or -1
//                            - relerr or abserr < 0
//                            - iflag = 0  or < -2  or > 8
//      = RungeKuttaFehlberg::UnsolvableProblem
//               -- nothing helps to get make further step
//
//    After a step the working variables and vectors in the class instance
//    hold information which is usually of no interest to the user but
//    necessary for subsequent calls.
//
// Subsequent calls:
//
//   If the integration reached tout, the user need only
//   define a new tout and call "Integrate" again. In the one-step integrator
//   mode "iflag=RungeKuttaFehlberg::SingleStep" the user must keep in mind 
//   that each step taken is in the direction of the current tout. 
//   Upon reaching tout (indicated by changing iflag to 
//   RungeKuttaFehlberg::NormalStep), the user must then define a new tout and
//   reset iflag to RungeKuttaFehlberg::SingleStep to continue in the 
//   one-step integrator mode.
//
//   If the integration was not completed but the user still wants to
//   continue (iflag=RungeKuttaFehlberg::SmallRelErrorBound, 
//   RungeKuttaFehlberg::TooManyIterations cases), he just calls Integrate
//   again. With iflag=RungeKuttaFehlberg::SmallRelErrorBound
//   the relerr parameter has been adjusted appropriately for continuing
//   the integration. In the case of iflag=RungeKuttaFehlberg::TooManyIterations
//   the function counter will be reset to 0 and another maxnfe
//   function evaluations are allowed.
//
//   However,in the case iflag=RungeKuttaFehlberg::SmallAbsErrorBound, 
//   the user must first alter the error criterion to use a positive value 
//   of abserr before integration can proceed. If he does not, 
//   execution is terminated.
//
//   Also, in the case iflag=RungeKuttaFehlberg::MinimumStepReached, 
//   it is necessary for the user to reset
//   iflag to RungeKuttaFehlberg::NormalStep 
//   (or RungeKuttaFehlberg::SingleStep when the one-step integration mode is 
//   being used) as well as increasing either abserr, relerr or both before the
//   integration can be continued. If this is not done, execution will
//   be terminated. The occurrence of iflag=RungeKuttaFehlberg::MinimumStepReached
//   indicates a trouble spot (solution is changing rapidly, singularity may 
//   be present) and it often is inadvisable to continue.
//
//   If iflag=RungeKuttaFehlberg::TooManyCalls is encountered, the user 
//   should use the one-step integration mode with the stepsize determined 
//   by the code or consider switching to the Adams codes de/step, intrp. 
//   If the user insists upon continuing the integration with the Runge-
//   Kutta-method he must reset iflag to RungeKuttaFehlberg::NormalStep 
//   before calling Integrate again. Otherwise, execution will be
//   terminated.
//
//   If iflag=RungeKuttaFehlberg::InvalidParameters is obtained, 
//   integration can not be continued unless the invalid input parameters 
//   are corrected.
//
//   If iflag=RungeKuttaFehlberg::UnsolvableProblem is returned there is
//   no possibility to continue with any other parameters.
//
//
// NOTES:
// ------
//   This a C++ version of a Fehlberg fourth-fifth order Runge-Kutta method
//   based on the famous Fortran implementation of
//
//    H. A. Watts and L. F. Shampine
//    Sandia Laboratories
//    Albuquerque, New Mexico
//
//   which is commonly known as RKF45. It is publically available on the NETLIB
//   and can also be found in this archive.
//
//   (1) The method is described in the reference:
//       E. Fehlberg, 
//       "Low-order classical Runge-Kutta formulas with stepsize control",
//       NASA TR R-315.
//   (2) The performance of RKF45 is illustrated in the reference:
//       L. F. Shampine, H. A. Watts, S.Davenport, 
//       "Solving non-stiff ordinary differential equations - the state of the art",
//       Sandia Laboratories report SAND75-0182, appeared also in SIAM Review.
//   (3) More recent references:
//       Louis Baker,
//       "C Tools for Scientists and Engineers", McGraw-Hill, 
//       Hamburg, New York, 1989.
//       Also available in German:
//       "C-Werkzeuge fuer Naturwissenschaftler und Ingenieure", MacGraw-Hill,
//       Hamburg, 1990.
//
//   This C++ version for the Matpack Numerics and Graphics Library has been 
//   written by Berndt M. Gammel, 1996.
//
//-----------------------------------------------------------------------------//


package math.ODE;

// The Java equivalent of
//
//     void (*Model)(double t, const Vector &y, Vector &yp)
//        -- user supplied routine to evaluate derivatives yp(i)=dy(i)/dt
//
// is an interface called Derivatives

public class RungeKuttaFehlberg
{
	// public variables
	
	// these variables are set by the method Integrate
	public double t;
	public int iflag; 	
	
	
	// public constants: values for iflag
	
	public final static int
		Start              =  1,
		StartSingleStep    = -1,
		NormalStep         =  2,
		SingleStep         = -2,
		SmallRelErrorBound =  3,
		TooManyIterations  =  4,
		SmallAbsErrorBound =  5,
		MinimumStepReached =  6,
		TooManyCalls       =  7,
		InvalidParameters  =  8,
		UnsolvableProblem  =  9;


	// private variables
	
	int norder,nfe,kop,init,kflag,jflag;		
	double h,savre,savae,order,iorder,crit,criti;
	double Work[][];
	
	
	// private constants
	
	final double 
		// Runge-Kutta-Fehlberg formula of 4-th order
		a[] = { 0.0, 1.0/4.0,  3.0/8.0,  12.0/13.0,  1.0,  1.0/2.0 },
		b[][] = { 
			{  0.0,            0.0,           0.0,            0.0,            0.0       },
			{  1.0/4.0,        0.0,           0.0,            0.0,            0.0       },
			{  3.0/32.0,       9.0/32.0,      0.0,            0.0,            0.0       },
			{  1932.0/2197.0, -7200.0/2197.0, 7296.0/2197.0,  0.0,            0.0       },
			{  439.0/216.0,   -8.0,           3680.0/513.0,  -845.0/4104.0,   0.0       },
			{ -8.0/27.0,       2.0,          -3544.0/2565.0,  1859.0/4104.0, -11.0/40.0 }
		},
		// uncomment only one of c[6] below:
		// for local error of 5-th order use formula below - RKF4(5) - best choice!
		c[] = { 16.0/135.0, 0.0, 6656.0/12825.0, 28561.0/56430.0, -9.0/50.0, 2.0/55.0 },
		// for local error of 4-th order use formula below - RKF4(4) - second best only!
		// c[] = { 25.0/216.0, 0.0, 1408.0/2565.0,  2197.0/4104.0, -1.0/5.0,  0.0 },
		// local error term
		e[] = { -1.0/360.0,  0.0, 128.0/4275.0, 2197.0/75240.0, -1.0/50.0, -2.0/55.0 };
	

	//-----------------------------------------------------------------------------//

	// constructor
	public RungeKuttaFehlberg (double y[], int norder) 
 	// : norder(norder) <-- What does that do?
	{
		// check for implemented orders

		if (norder != 5)
			throw new Error("RungeKuttaFehlberg: Currently only order=5, RKF45, supported");

		// allocate working space according to order of Runge-Kutta method 
		// and the number of equations

		Work = new double[norder+2][y.length];
		
		// initialize constants which depend on the order
		order  = norder;
		iorder = 1.0 / order;
		crit   = Math.pow(9.0,order);
		criti  = Math.pow((0.9/order),order);
	}

	//-----------------------------------------------------------------------------//

	double ErrorTerm (int k)
	{
		double sum = 0;
		for (int i = 0; i <= norder; i++)
			sum += e[i] * Work[i][k];
		return sum;
	}

	//-----------------------------------------------------------------------------//
	
	void RungeKuttaStep 
		(Derivatives model,
		 double[] y,
		 double t, double h,
		 double[] s)
	{

		// Integrates a system of N first order
		// ordinary differential equations of the form
		//
		//        dy(i) / dt = f(t, y(1), ... ,y(N))
		//
		// where the initial values y(i) and the initial derivatives
		// yp(i) are specified at the starting point t. It advances
		// the solution over the fixed step h and returns
		// solution approximation at t+h in array s(i).
		// (In the case of Shampine's original rkf45 the fourth order solution, 
		// fifth order accurate locally).
		// It should be called with an h not smaller than 13 units of
		// roundoff in t so that the various independent arguments can be
		// distinguished.

		for (int j = 1; j <= norder; j++)
		{
			for (int i = 0; i < y.length; i++)
			{
				double x = 0.0;
				for (int m = 0; m < j; m++) 
					x += b[j][m] * Work[m][i];
				s[i] = x * h + y[i];
			}
			model.calculateDerivatives(t + a[j] * h, s, Work[j]);
		}

		for (int i = 0; i < y.length; i++)
		{
			double x = 0.0;
			for (int j = 0; j <= norder; j++) 
				x += c[j] * Work[j][i];
			s[i] = h * x + y[i];
		}
	}
	
	// return first number with sign of second number
	// (from Matpack file "matpack/include/common.h")
	private double CopySign(double x, double y)
	{ 
		return (y < 0) ? ((x < 0) ? x : -x) : ((x > 0) ? x : -x);
	}
	
	private int CopySign(int x, int y)
	{ 
		return (y < 0) ? ((x < 0) ? x : -x) : ((x > 0) ? x : -x);
	}

	//-----------------------------------------------------------------------------//

	public void Integrate
		(Derivatives model,
		 double y[], 
		 double tIn, double tout, 
		 double relerr, double abserr,
		 int iflagIn, int maxnfe)
	{
		// copy tIn and iflagIn to this.t and this.iflag, respectively
		t = tIn;
		iflag = iflagIn;
		
		// Get machine epsilon
 
		final double
			// C-type DBL_EPSILON, the difference between 1 and the least value
			// greater than 1 that is representable.
			// Calculated by converting into a bit representation, increasing the
			// mantissa (bits 0-51) by 1 (simply by adding 1L to the bit representation),
			// converting back to a double number, and finally subtracting 1.0
			eps = Double.longBitsToDouble(Double.doubleToLongBits(1.0)+1L)-1.0,
			u26 = 26*eps;
                 
		// remin is the minimum acceptable value of relerr. Attempts
		// to obtain higher accuracy with this subroutine are usually
		// very expensive and often unsuccessful.

		final double remin = 1e-12;

		// The expense is controlled by restricting the number
		// of function evaluations to be approximately maxnfe.
		// The default value of maxnfe = 3000 corresponds to about 500 steps.

		int mflag = Math.abs(iflag);

		// input check parameters

		if (y.length < 1 || relerr < 0.0 || abserr < 0.0 ||  mflag == 0  
			|| mflag > InvalidParameters 
			|| ((t == tout) && (kflag != SmallRelErrorBound)))
		{
			iflag = InvalidParameters;
			return;
		}

		double dt,rer,scale,hmin,eeoet,ee,et,esttol,s,ae,tol = 0;
		boolean output, hfaild;

		// references for convenience

		double
			yp[] = Work[0],
			f1[] = Work[1],
			ss[] = Work[norder+1];

		int gflag = 0;
        
		if (iflag == SmallRelErrorBound
			|| (mflag == NormalStep && (init == 0 || kflag == NormalStep)))
		{
			gflag = Start;
		}
		else if (iflag == TooManyIterations 
			|| (kflag == TooManyIterations && mflag == NormalStep))
		{
			nfe = 0;
			if (mflag != NormalStep) gflag = Start;
		}
		else if ((kflag == SmallAbsErrorBound && abserr == 0.0)
			|| (kflag == MinimumStepReached && relerr < savre && abserr < savae))
		{
			iflag = UnsolvableProblem;
			return;
		}
   
		// next:

		if (gflag != 0)
		{
			iflag = jflag;
			if (kflag == SmallRelErrorBound) mflag = Math.abs(iflag);
		}

		jflag = iflag;
		kflag = 0;
		savre = relerr;
		savae = abserr;
    
		// Restrict relative error tolerance to be at least as large as
		// 2*eps+remin to avoid limiting precision difficulties arising
		// from impossible accuracy requests

		rer = 2 * eps + remin;

		// Relative error tolerance is too small

		if (relerr < rer)
		{
			relerr = rer;
			iflag = kflag = SmallRelErrorBound;
			return;
		}
    
		gflag = 0;
		dt = tout - t;

		if (mflag == Start)
		{

			// Initialization
			// Set initialization completion indicator, init
			// Set indicator for too many output points,kop
			// Evaluate initial derivatives
			// Set counter for function evaluations,nfe
			// Estimate starting stepsize

			init = 0;
			kop = 0;
			gflag = Start;
			model.calculateDerivatives(t,y,yp);	// call function
			nfe = 1;
			if (t == tout)
			{
				iflag = NormalStep;
				return;
			}
		}

		if (init == 0 || gflag != 0)
		{
			init = 1;
			h = Math.abs(dt);
			double ypk;
			for (int k = 0; k < y.length; k++)
			{
				tol = relerr * Math.abs(y[k]) + abserr;
				if (tol > 0.0)
				{
					ypk = Math.abs(yp[k]);
					if (ypk * Math.pow(h,order) > tol)
						h = Math.pow(tol/ypk,iorder);
				}
			}

			if (tol <= 0.0) h = 0.0;
			ypk = Math.max(Math.abs(dt),Math.abs(t));
			h = Math.max(h, u26 * ypk);
			jflag = CopySign(NormalStep,iflag);
		}

		// Set stepsize for integration in the direction from t to tout

		h = CopySign(h,dt);

		// Test to see if this routine is being severely impacted by too many
		// output points

		if (Math.abs(h) >= 2*Math.abs(dt)) kop++;

		if (kop == 100)
		{
			kop = 0;
			iflag = TooManyCalls;
			return;
		}
		
		if (Math.abs(dt) <= u26 * Math.abs(t))
		{

			// If too close to output point,extrapolate and return

			for (int k = 0; k < y.length; k++)
				y[k] += dt * yp[k];

			model.calculateDerivatives(tout,y,yp);
			nfe++;
			t = tout;
			iflag = NormalStep;
			return;
		}

		// Initialize output point indicator

		output = false;

		// To avoid premature underflow in the error tolerance function,
		// scale the error tolerances

		scale = 2.0 / relerr;
		ae = scale * abserr; 

		// Step by step integration - as an endless loop over steps

		for (;;)
		{ 

			hfaild = false;

			// Set smallest allowable stepsize

			hmin = u26 * Math.abs(t);

			// Adjust stepsize if necessary to hit the output point.
			// Look ahead two steps to avoid drastic changes in the stepsize and
			// thus lessen the impact of output points on the code.

			dt = tout - t;
			if (Math.abs(dt) < 2 * Math.abs(h))
			{
				if (Math.abs(dt) <= Math.abs(h))
				{

					// The next successful step will complete the 
					// integration to the output point

					output = true;
					h = dt;
				} 
				else
					h = 0.5 * dt;
			}

			// Core integrator for taking a single step
			//
			// The tolerances have been scaled to avoid premature underflow in
			// computing the error tolerance function et.
			// To avoid problems with zero crossings, relative error is measured
			// using the average of the magnitudes of the solution at the
			// beginning and end of a step.
			// The error estimate formula has been grouped to control loss of
			// significance.
			// To distinguish the various arguments, h is not permitted
			// to become smaller than 26 units of roundoff in t.
			// Practical limits on the change in the stepsize are enforced to
			// smooth the stepsize selection process and to avoid excessive
			// chattering on problems having discontinuities.
			// To prevent unnecessary failures, the code uses 9/10 the stepsize
			// it estimates will succeed.
			// After a step failure, the stepsize is not allowed to increase for
			// the next attempted step. This makes the code more efficient on
			// problems having discontinuities and more effective in general
			// since local extrapolation is being used and extra caution seems
			// warranted.
			//
			// Test number of derivative function evaluations.
			// If okay,try to advance the integration from t to t+h
	
			if (nfe > maxnfe)
			{

				// Too much work

				iflag = kflag = TooManyIterations;
				return;
			}
	
			// step:
			
			while (true)
			{

				// Advance an approximate solution over one step of length h

				RungeKuttaStep(model,y,t,h,ss);
	
				for (int i = 0; i < y.length; i++) f1[i] = ss[i];
				nfe += norder;

				// Compute and test allowable tolerances versus local error estimates
				// and remove scaling of tolerances. note that relative error is
				// measured with respect to the average of the magnitudes of the
				// solution at the beginning and end of the step.
	
				eeoet = 0.0;
				for (int k = 0; k < y.length; k++)
				{
	
					et = Math.abs(y[k]) + Math.abs(f1[k]) + ae;
	
					// Inappropriate error tolerance
	
					if (et <= 0.0)
					{
						iflag = SmallAbsErrorBound;
						return;
					}
	    
					ee = Math.abs( ErrorTerm(k) );
					eeoet = Math.max(eeoet,ee/et);
				}
	
				esttol = Math.abs(h) * eeoet * scale;
	
				if (esttol <= 1.0) break;

				// Unsuccessful step
				// Reduce the stepsize , try again
				// The decrease is limited to a factor of 1/10

				hfaild = true;
				output = false;
				s = 0.1;
				if (esttol < crit) s = 0.9 / Math.pow(esttol,iorder);
				h *= s;
					
				// if (abs(h) > hmin) goto step; // loop
				if (Math.abs(h) <= hmin)
				{
	
					// Requested error unattainable at smallest allowable stepsize
	
					iflag = kflag = MinimumStepReached;
					return;
				}
			
			} // while (true)

			// Successful step
			// Store solution at t+h and evaluate derivatives there

			t += h;
			for (int k = 0; k < y.length; k++) y[k] = f1[k];

			model.calculateDerivatives(t,y,yp);
			nfe++;

			// Choose next stepsize
			// The increase is limited to a factor of 5
			// if step failure has just occurred, next
			// stepsize is not allowed to increase
	
			s = 5.0;
			if (esttol > criti) s = 0.9 / Math.pow(esttol,iorder);
			if (hfaild) s = Math.min(1.0,s);
			h = CopySign(Math.max(hmin,s*Math.abs(h)),h);
	
			// End of core integrator

			if (output)
			{
				t = tout;
				iflag = NormalStep;
				return;
			}

			if (iflag <= 0)
			{

				// one-step mode

				iflag = SingleStep;
				return;
			}
	
		} // for (;;)
	}

	//-----------------------------------------------------------------------------//
}