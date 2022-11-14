package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.Geometry;
import math.MathException;
import math.Vector3D;
import math.ODE.Derivatives;
import math.ODE.RungeKutta;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.RefractiveSimple;

public class AdamsIdealLensLookalike extends SceneObjectPrimitiveIntersection
implements Derivatives
{
	private static final long serialVersionUID = 5575403985509897219L;
	
	//camera viewing position
	private Vector3D p;
	//image position
	private Vector3D q;
	
	//point on ideal lens. This will not be the principal point unless it is chosen to lie on a line between p1 and p2
	private Vector3D pI;
	private Vector3D idealLensNormal;
	
	//define an orthogonal coordinate system using the normal to the plane and principal point as the basis, b, vectors
	Vector3D b1;
	Vector3D b3;
	Vector3D b2;

	//occular thickness
	private double dp;
	//objective thickness
	private double dq;
	//width and height along ideal thin lens.
	private double width, height;
	//surface properties
	SurfaceProperty surfaceProperty1, surfaceProperty2, surfacePropertySides;

	//steps for triangulating 
	private int iSteps;
	private int jSteps;

	//the new principal point
	Vector3D principalPoint;
	
	//Define  global ideal lens position which gets changed for every i and j step. Initially it will be the principal point.
	Vector3D idealLensPosition;
	
	private double integrationStepSize;

	/**
	 * n_inside / n_outside, i.e. ratio of refractive indices of the lens
	 */
	private double n;

	/**
	 * array of positions on the ideal lens
	 */
	//private Vector3D[][] pointsOnIdealLens;

	// useful to have

	TriangulatedSurface surface1, surface2, side1, side2, side3, side4;


	/**
	 * 
	 * @param description
	 * @param p point to be imaged
	 * @param q image of p
	 * @param dp width of the 'lens' towards p
	 * @param dq width of the 'lens' towards q
	 * @param pI point on ideal lens plane
	 * @param idealLensNormal normal to the ideal lens plane
	 * @param height height of the ideal lens
	 * @param width width of the ideal lens
	 * @param iSteps step count in the i direction. If it is odd, it will be rounded down to the nearest even number
	 * @param jSteps step count in the j direction. If it is odd, it will be rounded down to the nearest even number
	 * @param integrationStepSize step size of each integration step
	 * @param n refractive index
	 * @param surfaceProperty1 front surface property
	 * @param surfaceProperty2 back surface property
	 * @param surfacePropertySides side surface property
	 * @param parent 
	 * @param studio
	 */
	public AdamsIdealLensLookalike(
			String description,
			Vector3D p,
			Vector3D q,
			double dp,
			double dq,
			Vector3D pI,
			Vector3D idealLensNormal,
			double height,
			double width,
			int iSteps,
			int jSteps,
			double integrationStepSize,
			double n,
			SurfaceProperty surfaceProperty1,
			SurfaceProperty surfaceProperty2,
			SurfaceProperty surfacePropertySides,
			SceneObject parent,
			Studio studio
			)
	{
		super(description, parent, studio);

		setP(p);
		setQ(q);
		setDp(dp);
		setDq(dq);
		setpI(pI);
		setIdealLensNormal(idealLensNormal);
		//create and set the axis vectors
		Vector3D b3 = idealLensNormal.getNormalised();
		Vector3D b1 = Vector3D.getANormal(b3);
		setB1(b1);
		setB3(b3);
		setB2(Vector3D.crossProduct(b3,b1));
		
		setWidth(width);
		setHeight(height);
		setISteps(iSteps/2); //TODO this should round down 
		setJSteps(jSteps/2);
		setIntegrationStepSize(integrationStepSize);
		setN(n);
		setSurfaceProperty1(surfaceProperty1);
		setSurfaceProperty2(surfaceProperty2);
		setSurfacePropertySides(surfacePropertySides);
		initialise();
	}
	
	/**
	 * A second constructor which assumes the surfaces will be simple refractive and set them as such.
	 * @param description
	 * @param p point to be imaged
	 * @param q image of p
	 * @param dp width of the 'lens' towards p
	 * @param dq width of the 'lens' towards q
	 * @param pI point on ideal lens plane
	 * @param idealLensNormal normal to the ideal lens plane
	 * @param height height of the ideal lens
	 * @param width width of the ideal lens
	 * @param iSteps step count in the i direction. If it is odd, it will be rounded down to the nearest even number
	 * @param jSteps step count in the j direction. If it is odd, it will be rounded down to the nearest even number
	 * @param integrationStepSize step size of each integration step
	 * @param n refractive index
	 * @param parent 
	 * @param studio
	 */
	public AdamsIdealLensLookalike(
			String description,
			Vector3D p,
			Vector3D q,
			double dp,
			double dq,
			Vector3D pI,
			Vector3D idealLensNormal,
			double height,
			double width,
			int iSteps,
			int jSteps,
			double integrationStepSize,
			double n,
			SceneObject parent,
			Studio studio
			)
	{
		super(description, parent, studio);

		setP(p);
		setQ(q);
		setDp(dp);
		setDq(dq);
		setpI(pI);
		setIdealLensNormal(idealLensNormal);
		//create and set the basis vectors
		Vector3D b3 = idealLensNormal.getNormalised();
		Vector3D b1 = Vector3D.getANormal(b3);
		setB1(b1);
		setB3(b3);
		setB2(Vector3D.crossProduct(b3,b1));
		
		setWidth(width);
		setHeight(height);
		setISteps(iSteps/2);
		setJSteps(jSteps/2);
		setIntegrationStepSize(integrationStepSize);
		setN(n);
		setSurfaceProperty1(getRefractiveSurfaceProperty());
		setSurfaceProperty2(getRefractiveSurfaceProperty());
		setSurfacePropertySides(getRefractiveSurfaceProperty());
		initialise();
	}


	// setters & getters

	public Vector3D getP() {
		return p;
	}

	public void setP(Vector3D p) {
		this.p = p;
	}

	public Vector3D getQ() {
		return q;
	}

	public void setQ(Vector3D q) {
		this.q = q;
	}

	public double getDp() {
		return dp;
	}

	public void setDp(double dp) {
		this.dp = dp;
	}

	public double getDq() {
		return dq;
	}

	public void setDq(double dq) {
		this.dq = dq;
	}

	public Vector3D getpI() {
		return pI;
	}

	public void setpI(Vector3D pI) {
		this.pI = pI;
	}

	public Vector3D getIdealLensNormal() {
		return idealLensNormal;
	}

	public void setIdealLensNormal(Vector3D idealLensNormal) {
		this.idealLensNormal = idealLensNormal;
	}

	public Vector3D getB1() {
		return b1;
	}


	public void setB1(Vector3D b1) {
		this.b1 = b1;
	}


	public Vector3D getB3() {
		return b3;
	}


	public void setB3(Vector3D b3) {
		this.b3 = b3;
	}


	public Vector3D getB2() {
		return b2;
	}


	public void setB2(Vector3D b2) {
		this.b2 = b2;
	}


	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public int getISteps() {
		return iSteps;
	}

	public void setISteps(int iSteps) {
		this.iSteps = iSteps;
	}

	public int getJSteps() {
		return jSteps;
	}

	public void setJSteps(int jSteps) {
		this.jSteps = jSteps;
	}

	public double getIntegrationStepSize() {
		return integrationStepSize;
	}

	public void setIntegrationStepSize(double integrationStepSize) {
		this.integrationStepSize = integrationStepSize;
	}

	public SurfaceProperty getSurfaceProperty1() {
		return surfaceProperty1;
	}

	public void setSurfaceProperty1(SurfaceProperty surfaceProperty1) {
		this.surfaceProperty1 = surfaceProperty1;
	}

	public SurfaceProperty getSurfaceProperty2() {
		return surfaceProperty2;
	}

	public void setSurfaceProperty2(SurfaceProperty surfaceProperty2) {
		this.surfaceProperty2 = surfaceProperty2;
	}

	public SurfaceProperty getSurfacePropertySides() {
		return surfacePropertySides;
	}

	public void setSurfacePropertySides(SurfaceProperty surfacePropertySides) {
		this.surfacePropertySides = surfacePropertySides;
	}

	public double getN() {
		return n;
	}

	public void setN(double n) {
		this.n = n;
	}

	public TriangulatedSurface getSurface1() {
		return surface1;
	}

	public TriangulatedSurface getSurface2() {
		return surface2;
	}

	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector3D principalPoint) {
		this.principalPoint = principalPoint;
	}

	public void initialise()
	{
		// initialise the lists of positive and negative scene-object primitives
		positiveSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>(2);
		negativeSceneObjectPrimitives  = new ArrayList<SceneObjectPrimitive>();

		// TODO add aperture shape here?
		invisiblePositiveSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
		invisibleNegativeSceneObjectPrimitives  = new ArrayList<SceneObjectPrimitive>();

		// calculate array of vertices on both surface
		// create space for vertices on surface 1...
		Vector3D[][] v1 = new Vector3D[2*iSteps][2*jSteps];
		// ... and on surface 2
		Vector3D[][] v2 = new Vector3D[2*iSteps][2*jSteps];

		// initialise all these vectors to null
		for(int i=0; i<2*iSteps; i++)
			for(int j=0; j<2*jSteps; j++)
			{
				v1[i][j] = null;
				v2[i][j] = null;
			}

		//adding the 0th points i.e the principal point
		//principalPoint;
		try {
			principalPoint = Geometry.linePlaneIntersection(p, Vector3D.difference(p,q).getNormalised(), pI, idealLensNormal);
			idealLensPosition = getUVWposition(principalPoint);
		} catch (MathException e) {
			System.err.println("Ideal lens plane parallel to line from p1 to p2");
			e.printStackTrace();
		}

		// initialise one pair of points, the central ones at v[iSteps][jSteps]
		v1[iSteps][jSteps] = Vector3D.sum(
				principalPoint,
				Vector3D.difference(p, principalPoint).getWithLength(dp)
				);
		v2[iSteps][jSteps] = Vector3D.sum(
				principalPoint,
				Vector3D.difference(q, principalPoint).getWithLength(dq)
				);

		//after this it should start to differ from Johannes's ideal thin lens lookalike.

		//first, the calculate point on surface should give a result for any u, v on the ideal lens. 
		//This can then be sampled as desired and added as a triangulated surface
		for(int i =0; i<2*iSteps; i++) {
			for(int j =0; j<2*jSteps; j++) {
				//if(i == iSteps && j == jSteps) continue; //skip for 0th term as this will be the principal point equivalent.
				Vector3D[] v = calculateSurfacePoints(v1[iSteps][jSteps], v2[iSteps][jSteps], i-iSteps, j-jSteps ,integrationStepSize);
				v1[i][j] = v[0];
				v2[i][j] = v[1];
			}
		}
		
		System.out.println("front corner "+v1[0][0]);
		System.out.println("front centre "+v1[iSteps][jSteps]);
		System.out.println("front corner "+v1[2*iSteps-1][2*jSteps-1]);
		
		System.out.println("back corner "+v2[0][0]);
		System.out.println("back centre "+v2[iSteps][jSteps]);
		System.out.println("back corner "+v2[2*iSteps-1][2*jSteps-1]);

		surface1 = new TriangulatedSurface(
				"Surface 1",	// description
				v1,
				true,	//inverted
				surfaceProperty1,
				this,	// parent
				getStudio()
				);
		positiveSceneObjectPrimitives.add(surface1);
		int trangleType = 0;
		if(surface1.isInvertSurface())trangleType = 1;
		System.out.println("at "+v1[1][1]+", with normal"+surface1.getOutwardsSurfaceNormal(trangleType, 1, 1));

		surface2 = new TriangulatedSurface(
				"Surface 2",	// description
				v2,
				false,	//not inverted
				surfaceProperty2,
				this,	// parent
				getStudio()
				);
		positiveSceneObjectPrimitives.add(surface2);
		int trangleType2 = 0;
		if(surface2.isInvertSurface())trangleType2 = 1;
		System.out.println("at "+v2[1][1]+", with normal"+ surface2.getOutwardsSurfaceNormal(trangleType2, 1, 1));


		//adding sides.
		if(surfacePropertySides != null)
		{
			// the side surfaces
			Vector3D[][] vs1 = new Vector3D[2*iSteps][2];
			Vector3D[][] vs3 = new Vector3D[2*iSteps][2];
			for(int i=0; i<2*iSteps; i++)
			{
				vs1[i][0] = v1[i][0];
				vs1[i][1] = v2[i][0];
				vs3[i][0] = v2[i][2*jSteps-1];
				vs3[i][1] = v1[i][2*jSteps-1];
			}

			Vector3D[][] vs2 = new Vector3D[2][2*jSteps];
			Vector3D[][] vs4 = new Vector3D[2][2*jSteps];
			for(int j=0; j<2*jSteps; j++)
			{
				vs2[0][j] = v1[0][j];
				vs2[1][j] = v2[0][j];
				vs4[0][j] = v2[2*iSteps-1][j];
				vs4[1][j] = v1[2*iSteps-1][j];
			}

			side1 = new TriangulatedSurface(
					"Side 1",	// description
					vs1,
					false,	// not inverted
					surfacePropertySides,
					this,	// parent
					getStudio()
					);
			//System.out.println(side1.getOutwardsSurfaceNormal(0, 0, 0));
			positiveSceneObjectPrimitives.add(side1);

			side2 = new TriangulatedSurface(
					"Side 2",	// description
					vs2,
					false,	// not inverted
					surfacePropertySides,
					this,	// parent
					getStudio()
					);
			//System.out.println(side2.getOutwardsSurfaceNormal(0, 0, 0));
			positiveSceneObjectPrimitives.add(side2);

			side3 = new TriangulatedSurface(
					"Side 3",	// description
					vs3,
					false,	// not inverted
					surfacePropertySides,
					this,	// parent
					getStudio()
					);
			//System.out.println(side3.getOutwardsSurfaceNormal(0, 0, 0));
			positiveSceneObjectPrimitives.add(side3);

			side4 = new TriangulatedSurface(
					"Side 4",	// description
					vs4,
					false,	// not inverted
					surfacePropertySides,
					this,	// parent
					getStudio()
					);
			//System.out.println(side4.getOutwardsSurfaceNormal(0, 0, 0));
			positiveSceneObjectPrimitives.add(side4);
		}
	}

	/*
	 * Do some coordinate transfers again but using some build in and tested functionality now...
	 */
	private Vector3D getUVWposition(Vector3D pXYZ) {
		//Use the orthogonal system set up above
		Vector3D localUVWposition = Vector3D.difference(pXYZ, principalPoint);
		//pXYZ.toBasis(b1, b2, b3);
		
		//and finally, translate the point to the corresponding UVW coordinate. This should make calculations easier. TODO check that all this is the right way around.
		//return 	Vector3D.difference(localUVWposition, principalPoint);
		return 	localUVWposition.toOrthonormalBasis(b1, b2, b3);
	}

	//Now do the opposite
	private Vector3D getXYZposition(Vector3D pUVW) {
		//first the translation TODO check this is correct.... it is not... help!
		//Vector3D localXYZposition = Vector3D.sum(pUVW, principalPoint);
		Vector3D localXYZposition = pUVW.fromBasis(b1, b2, b3);
		//Vector3D localXYZposition = pUVW.toOrthonormalBasis(Vector3D.X, Vector3D.Y, Vector3D.Z);
		
		//and now return it to the original XYZ basis
		return Vector3D.sum(localXYZposition, principalPoint);
		//return localXYZposition.fromBasis(b1, b2, b3);
	}
	



	/*
	 * Method to create the surface with a refractive material.
	 */
	private SurfaceProperty getRefractiveSurfaceProperty() {
		double refractiveIndexRatio = 1/n; //TODO this should be correct but may need checking all surface normals face into surface hence it is 1/n.
		return new RefractiveSimple(
				refractiveIndexRatio,	// insideOutsideRefractiveIndexRatio
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
				true	// shadowThrowing
				);
	}
	
	/*
	 * The following methods are all used to solve for the surface.
	 */
	private Vector3D[] calculateSurfacePoints(Vector3D pI, Vector3D qI, int iStep, int jStep, double deltaT) {
		Vector3D[] surfaceCoordinates= new Vector3D[2]; //0th entry for front and 1st for back surface.

		//now for the hard mathy bits...
		
		//first, change to an appropriate coordinate system. This will be reversed later on.
		Vector3D pIUVW = getUVWposition(pI);
		Vector3D qIUVW = getUVWposition(qI);
		//System.out.println(pI+", "+qI+" to: "+pIUVW+", "+qIUVW+", and back to: "+pIUVW.fromBasis(b1,b2,b3)+", "+qIUVW.fromBasis(b1,b2,b3));

		
		
		
//		Vector3D pIUVW = pI;
//		Vector3D qIUVW = qI;
		
		//Transform the ideal lens system to a more suitable space and overwrite the global variable used as the lens position.
		//Vector3D currentIdealLensSurfacePosition = Vector3D.sum(b1.getProductWith(0.5*width*iStep/(iSteps)), b2.getProductWith(0.5*height*jStep/(jSteps)), principalPoint);			
		Vector3D currentIdealLensSurfacePosition = getXYZposition(new Vector3D(0.5*width*iStep/(iSteps), 0.5*height*jStep/(jSteps), 0));				
		//System.out.println("currentIdealLensSurfacePosition "+currentIdealLensSurfacePosition);
		Vector3D idealLensSurfacePositionUVW = getUVWposition(currentIdealLensSurfacePosition);
		idealLensPosition = idealLensSurfacePositionUVW;
		


		

		// set everything up to use the methods in math.ODE
		// collect the three components of the front and back positions in one array
		double[] f = new double[6];

		// perform one iteration step (initial conditions)
		f[0] = pIUVW.x;
		f[1] = pIUVW.y;
		f[2] = pIUVW.z;	
		
		f[3] = qIUVW.x;
		f[4] = qIUVW.y;
		f[5] = qIUVW.z;


		
		
		for(double t=0; t<=1; t+=deltaT) {
		RungeKutta.calculateStep(
				0,	//t
				deltaT,	//dt the path... or more so distance along a path.
				f,
				this	//model
				);

		}

		
		
		//Store the result and change it back to the XYZ basis for tim to understand
//		surfaceCoordinates[0]= new Vector3D(f[0], f[1], f[2]); //front surface
//		surfaceCoordinates[1]= new Vector3D(f[3], f[4], f[5]); // back surface	

		surfaceCoordinates[0]= getXYZposition(new Vector3D(f[0], f[1], f[2])); //front surface
		surfaceCoordinates[1]= getXYZposition(new Vector3D(f[3], f[4], f[5])); // back surface	
		return surfaceCoordinates;
	}
	
	private Vector3D[] getSurfaceNormal(Vector3D frontSurfacePosition, Vector3D backSurfacePosition) { //assumes air material intersection
		Vector3D[] normal = new Vector3D[2];
		//Vector3D.difference(Q, backSurfacePosition).getNormalised()
		//front surface normal
		//System.out.println(frontSurfacePosition+", "+backSurfacePosition);
		normal[0] = RefractiveSimple.getNormal(
				Vector3D.difference(frontSurfacePosition, p).getNormalised(), 
				Vector3D.difference(backSurfacePosition, frontSurfacePosition).getNormalised(), 
				1, n
				);
		normal[1] = RefractiveSimple.getNormal(
				Vector3D.difference(backSurfacePosition, q).getNormalised(),
				Vector3D.difference(frontSurfacePosition, backSurfacePosition).getNormalised(), 
				1, n
				);
		//System.out.println(normal[0]+", "+normal[1]);
		return normal;
	}
	
	private double getSurfaceScalar(Vector3D surfacePosition, Vector3D idealLensSurfacePosition, Vector3D PorQ) {
		
		return Vector3D.getDistance(surfacePosition, PorQ)/Vector3D.getDistance(idealLensSurfacePosition, PorQ);
	}


	public Vector3D dPdt(Vector3D pSurface, Vector3D qSurface, Vector3D idealLensSurfacePosition)
	{
		//the normals at p and q, the front and back surface coordinates respectively
		Vector3D normal = getSurfaceNormal(pSurface,qSurface)[0]; //index 0 as it is the front surface
		double scalar = getSurfaceScalar(pSurface, idealLensSurfacePosition, p);

		double dsdt = -scalar * (Vector3D.scalarProduct(normal, idealLensSurfacePosition))/
				Vector3D.scalarProduct(normal, Vector3D.difference(idealLensSurfacePosition,p));

		return Vector3D.sum(Vector3D.difference(idealLensSurfacePosition, p).getProductWith(dsdt), idealLensSurfacePosition.getProductWith(scalar));
	}

	
	public Vector3D dQdt(Vector3D pSurface, Vector3D qSurface, Vector3D idealLensSurfacePosition)
	{
		//the normals at p and q, the front and back surface coordinates respectively
		Vector3D normal = getSurfaceNormal(pSurface,qSurface)[1]; //index 1 as it is the back surface
		double scalar = getSurfaceScalar(qSurface, idealLensSurfacePosition, q);

		double dsdt = -scalar * (Vector3D.scalarProduct(normal, idealLensSurfacePosition))/
				Vector3D.scalarProduct(normal, Vector3D.difference(idealLensSurfacePosition, q));

		return Vector3D.sum(Vector3D.difference(idealLensSurfacePosition, q).getProductWith(dsdt), idealLensSurfacePosition.getProductWith(scalar));
	}

	/* (non-Javadoc)
	 * @see math.ODE.Derivatives#calculateDerivatives(double, double[], double[])
	 */
	@Override
	public void calculateDerivatives(double t, double f[], double dfdt[])
	{
		// the first three values of f[] are the front surface position, ...
		Vector3D p0 = new Vector3D(f[0], f[1], f[2]);
		
		//... the last three are the back surface position
		Vector3D q0 = new Vector3D(f[3], f[4], f[5]);
		

		// calculate dp / dt...
		Vector3D dPdt = dPdt(p0, q0, idealLensPosition);

		// ... and dq / dt, ...
		Vector3D dQdt = dQdt(p0, q0, idealLensPosition);
		
		// ... and construct from these d(x,y) / dt
		dfdt[0] = dPdt.x;
		dfdt[1] = dPdt.y;
		dfdt[2] = dPdt.z;
		dfdt[3] = dQdt.x;
		dfdt[4] = dQdt.y;
		dfdt[5] = dQdt.z;

	}

}
