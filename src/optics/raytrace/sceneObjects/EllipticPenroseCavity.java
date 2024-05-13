package optics.raytrace.sceneObjects;
import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;

/**
 * A penrose cavity set up according to 
 * Kim, J., Kim, J., Seo, J. et al. Observation of a half-illuminated mode in an open Penrose cavity. Sci Rep 12, 9798 (2022). https://doi.org/10.1038/s41598-022-13963-y
 * We use elliptical cylinders as our mirrors.
 * In the more general set up, we ignore the symmetry of the inner ellipsoid and instead allow for both of the to be varied slightly. 
 */
public class EllipticPenroseCavity extends SceneObjectContainer
{
	private static final long serialVersionUID = 792415442714531566L;

	//The starting and end point for the cavity. In this case we take this as the axis along which the whole cavity will lie. 
	//In other words, the difference between the start and end point will the height
	private Vector3D startPoint, endPoint;

	//semi major axis of the outer elliptic cylinder. 
	private Vector3D a;
	//focal length of the elliptic cylinder taken as usual from the centre to the foci. 
	private double f;
	//The width (along the minor axis) of the cavity which includes the straight mirror section. 
	private double d;
	//The height of the straight vertical mirror on one half of the cavity. 
	private double h1;
	//The height of the straight vertical mirror on the other half of the cavity. 
	private double h2;


	//semi minor axis of one of the inner ellipsoids
	private double c1;
	//semi minor axis of the other inner ellipsoids
	private double c2;

	//The remaining paramters such as b, l, and the minor axis can be calculated using f and a.

	//make it infinitely tall?
	private boolean infinite;
	//The surface it is made of... when working well this should be set to a mirror surface.
	private SurfaceProperty surfaceProperty;


	public EllipticPenroseCavity(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,			
			boolean infinite,
			Vector3D a,
			double f,
			double d,
			double h1,
			double c1,
			double h2,
			double c2,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
			)
	{
		super(description, parent, studio);

		setStartPoint(startPoint);
		setEndPoint(endPoint);
		setInfinite(infinite);
		setA(calcAPerpendicular(startPoint,endPoint,a));
		setF(f);
		setD(d);
		setH1(h1);
		setC1(c1);
		setH2(h2);
		setC2(c2);
		setSurfaceProperty(surfaceProperty);

		//Set and check the variables
		checkParams();

		//Once this is done, add all the elements
		addElements();
	}

	public EllipticPenroseCavity(EllipticPenroseCavity original)
	{
		this(
				original.getDescription(),
				original.getStartPoint(),
				original.getEndPoint(),
				original.isInfinite(),
				original.getA(),
				original.getF(),
				original.getD(),
				original.getH1(),
				original.getC1(),
				original.getH2(),
				original.getC2(),
				original.getSurfaceProperty(),
				original.getParent(),
				original.getStudio()
				);
	}



	@Override
	public EllipticPenroseCavity clone()
	{
		return new EllipticPenroseCavity(this);
	}

	public Vector3D getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Vector3D startPoint) {
		this.startPoint = startPoint;
	}

	public Vector3D getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Vector3D endPoint) {
		this.endPoint = endPoint;
	}

	public Vector3D getA() {
		return a;
	}

	public void setA(Vector3D a) {
		this.a = a;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public double getH1() {
		return h1;
	}

	public void setH1(double h1) {
		this.h1 = h1;
	}

	public double getH2() {
		return h2;
	}

	public void setH2(double h2) {
		this.h2 = h2;
	}

	public double getC1() {
		return c1;
	}

	public void setC1(double c1) {
		this.c1 = c1;
	}

	public double getC2() {
		return c2;
	}

	public void setC2(double c2) {
		this.c2 = c2;
	}

	public boolean isInfinite() {
		return infinite;
	}

	public void setInfinite(boolean infinite) {
		this.infinite = infinite;
	}

	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}

	//setting a boolean check which needs to be false for all of the following cases in order to construct a cavity. 
	private boolean check = false;
	private void checkParams() {

		if(f>=a.getLength()) {
			System.err.println("f("+f+") needs to be smaller than the semi major axis of the outer cylinder("+a.getLength()+").");
			check = true;
		} else {

			double b = Math.sqrt(a.getModSquared()-f*f);
			if(d<=b) {
				System.err.println("d("+d+") needs to be larger than the semi minor axis of the outer cylinder("+b+").");
				check = true;
			}

			if(d-b<h1) {
				System.err.println("h1 is too large for d to accomedate the extrusion");
				check = true;
			}

			if(d-b<h2) {
				System.err.println("h2 is too large for d to accomedate the extrusion");
				check = true;
			}

			if(c1>f) {
				System.err.println("c1("+c1+") is greater than f("+f+")");
				check = true;
			}	

			if(c2>f) {
				System.err.println("c2("+c2+") is greater than f("+f+")");
				check = true;
			}
		}
	}	


	//Calculate the a vector such that is is perpendicular to the vertical axis. 
	//If they are along the same direction, a random perpendicular vector will be taken.
	private Vector3D calcAPerpendicular(Vector3D startPoint,Vector3D endPoint,Vector3D a) {

		Vector3D verticalNormalised = Vector3D.difference(endPoint, startPoint).getNormalised();

		//Check if they are along the same direction
		if(Math.abs(Vector3D.scalarProduct(verticalNormalised, a))==1) return Vector3D.getANormal(verticalNormalised).getWithLength(a.getLength());

		//if it is not, make sure it is perpendicular and return it with the correct length...
		return a.getPartPerpendicularTo(verticalNormalised).getWithLength(a.getLength());
	}

	public void addElements()
	{
		if(!check)
			//Clear it just in case of some left over collection...
			clear();
		//Creating a very basic coordinate space
		Vector3D uHat = a.getNormalised();
		Vector3D vHat = Vector3D.difference(endPoint, startPoint).getNormalised();
		Vector3D zHat = Vector3D.crossProduct(uHat, vHat);

		// b is the length of the semi minor axis of our outer ellipse
		double b = Math.sqrt(a.getModSquared()-f*f);

		//First we find the centre of the two elliptic cylinder mantle halfs that make up the outer edge.
		Vector3D topHalfCentre = Vector3D.sum(startPoint, zHat.getProductWith((d-b)));
		Vector3D bottomHalfCentre = Vector3D.sum(startPoint, zHat.getProductWith(-(d-b)));

		//Then we create the half elliptic cylinder mantle which will be the outer edge using scene intersections 
		SceneObjectIntersection topHalf = new SceneObjectIntersection("top half", this ,getStudio());

		EllipticCylinderMantle topCylinder = new EllipticCylinderMantle(			
				"top cylinder",// description,
				topHalfCentre,// startPoint,
				topHalfCentre.getSumWith(endPoint),// endPoint,
				a,// spanA,
				f,// focalLength,
				infinite,// infinite,
				surfaceProperty,//SurfaceProperty
				this,// parent,
				getStudio()// studio
				);

		Plane topCutOffPlane = new Plane("cut off plane", topHalfCentre, zHat.getProductWith(-1), surfaceProperty, this, getStudio());

		topHalf.addPositiveSceneObject(topCylinder);
		topHalf.addInvisiblePositiveSceneObject(topCutOffPlane);

		SceneObjectIntersection bottomHalf = new SceneObjectIntersection("bottom half", this ,getStudio());

		EllipticCylinderMantle bottomCylinder = new EllipticCylinderMantle(			
				"top cylinder",// description,
				bottomHalfCentre,// startPoint,
				bottomHalfCentre.getSumWith(endPoint),// endPoint,
				a,// spanA,
				f,// focalLength,
				infinite,// infinite,
				surfaceProperty,//SurfaceProperty
				this,// parent,
				getStudio()// studio
				);

		Plane bottomCutOffPlane = new Plane("cut off plane", bottomHalfCentre, zHat.getProductWith(1), surfaceProperty, this, getStudio());

		bottomHalf.addPositiveSceneObject(bottomCylinder);
		bottomHalf.addInvisiblePositiveSceneObject(bottomCutOffPlane);

		//Now that this has worked, the extrusions can be made.. 

		//We start by finding the two extrusion centres.
		Vector3D extrusionCentre1 = Vector3D.sum(startPoint, uHat.getProductWith(f));
		Vector3D extrusionCentre2 = Vector3D.sum(startPoint, uHat.getProductWith(-f));

		//We set up the individual half elliptical cylinder mantle extrusions such that their outer edge coincides with the foci of the outer one.
		SceneObjectIntersection ellipticExtrusion1 = new SceneObjectIntersection("elliptic extrusion 1", this ,getStudio());
		EllipticCylinderMantle extrusionCylinder1 = new EllipticCylinderMantle(			
				"extrusion cylinder1",// description,
				extrusionCentre1,// startPoint,
				extrusionCentre1.getSumWith(endPoint),// endPoint,
				zHat.getProductWith(d-b),// spanA,
				uHat.getProductWith(c1),// spanC,
				infinite,// infinite,
				surfaceProperty,// surfaceProperty,
				this,// parent,
				getStudio()// studio	
				);
		Plane extrusionCutOffPlane1 = new Plane("extrusion cut off plane1", extrusionCentre1, uHat.getProductWith(1), surfaceProperty, this, getStudio());
		ellipticExtrusion1.addPositiveSceneObject(extrusionCylinder1);
		ellipticExtrusion1.addPositiveSceneObject(extrusionCutOffPlane1);

		SceneObjectIntersection ellipticExtrusion2 = new SceneObjectIntersection("elliptic extrusion 2", this ,getStudio());
		EllipticCylinderMantle extrusionCylinder2 = new EllipticCylinderMantle(			
				"extrusion cylinder2",// description,
				extrusionCentre2,// startPoint,
				extrusionCentre2.getSumWith(endPoint),// endPoint,
				zHat.getProductWith(d-b),// spanA,
				uHat.getProductWith(c2),// spanC,
				infinite,// infinite,
				surfaceProperty,// surfaceProperty,
				this,// parent,
				getStudio()// studio	
				);
		Plane extrusionCutOffPlane2 = new Plane("extrusion cut off plane2", extrusionCentre2, uHat.getProductWith(-1), surfaceProperty, this, getStudio());
		ellipticExtrusion2.addPositiveSceneObject(extrusionCylinder2);
		ellipticExtrusion2.addPositiveSceneObject(extrusionCutOffPlane2);

		//Lastly, we create the planar extrusions and the planar mirror on the side of the cavity.
		Vector3D sideCenter1 = Vector3D.sum(startPoint, uHat.getProductWith(a.getLength()));
		Vector3D sideCenter2 = Vector3D.sum(startPoint, uHat.getProductWith(-a.getLength()));

		Vector3D planarExtrusionCenterTop1 = Vector3D.sum(startPoint, zHat.getProductWith((d-(h1+b))));
		Vector3D planarExtrusionCenterBottom1 = Vector3D.sum(startPoint, zHat.getProductWith(-(d-(h1+b))));

		Vector3D planarExtrusionCenterTop2 = Vector3D.sum(startPoint, zHat.getProductWith((d-(h2+b))));
		Vector3D planarExtrusionCenterBottom2 = Vector3D.sum(startPoint, zHat.getProductWith(-(d-(h2+b))));


		SceneObjectIntersection planarExtrusion1 = new SceneObjectIntersection("plane extrusion 1", this ,getStudio());
		SceneObjectIntersection sides = new SceneObjectIntersection("sides", this ,getStudio());

		Plane planarExtrusionTop1 = new Plane("planar extrusion top 1", planarExtrusionCenterTop1, zHat.getProductWith(1), surfaceProperty, this, getStudio());
		Plane planarExtrusionBottom1 = new Plane("planar extrusion bottom 1", planarExtrusionCenterBottom1, zHat.getProductWith(-1), surfaceProperty, this, getStudio());
		Plane planarside1 = new Plane("planar side 1", sideCenter1, uHat.getProductWith(1), surfaceProperty, this, getStudio());

		planarExtrusion1.addPositiveSceneObject(planarExtrusionTop1);
		planarExtrusion1.addPositiveSceneObject(planarExtrusionBottom1);
		planarExtrusion1.addInvisiblePositiveSceneObject(planarside1);
		planarExtrusion1.addInvisibleNegativeSceneObject(extrusionCutOffPlane1);		



		SceneObjectIntersection planarExtrusion2 = new SceneObjectIntersection("plane extrusion 2", this ,getStudio());

		Plane planarExtrusionTop2 = new Plane("planar extrusion top 2", planarExtrusionCenterTop2, zHat.getProductWith(1), surfaceProperty, this, getStudio());
		Plane planarExtrusionBottom2 = new Plane("planar extrusion bottom 2", planarExtrusionCenterBottom2, zHat.getProductWith(-1), surfaceProperty, this, getStudio());
		Plane planarside2 = new Plane("planar side 2", sideCenter2, uHat.getProductWith(-1), surfaceProperty, this, getStudio());

		planarExtrusion2.addPositiveSceneObject(planarExtrusionTop2);
		planarExtrusion2.addPositiveSceneObject(planarExtrusionBottom2);
		planarExtrusion2.addInvisiblePositiveSceneObject(planarside2);
		planarExtrusion2.addInvisibleNegativeSceneObject(extrusionCutOffPlane2);

		sides.addPositiveSceneObject(planarside1);
		sides.addPositiveSceneObject(planarside2);
		sides.addInvisibleNegativeSceneObject(bottomCutOffPlane);
		sides.addInvisibleNegativeSceneObject(topCutOffPlane);

		//making the planes at the start and end point. If it is not infinite, they will cut the cavity to size
		if(!infinite) {
			Plane floor = new Plane("Floor", startPoint, vHat.getProductWith(-1), surfaceProperty, this, getStudio());
			Plane ceil = new Plane("Ceiling", endPoint, vHat.getProductWith(1), surfaceProperty, this, getStudio());

			planarExtrusion1.addInvisiblePositiveSceneObject(floor);
			planarExtrusion1.addInvisiblePositiveSceneObject(ceil);

			planarExtrusion2.addInvisiblePositiveSceneObject(floor);
			planarExtrusion2.addInvisiblePositiveSceneObject(ceil);

			sides.addInvisiblePositiveSceneObject(floor);
			sides.addInvisiblePositiveSceneObject(ceil);

		}

		//
		//Adding all the components together to hopefully make a working Penrose cavity...
		//
		if(!check) {
			addSceneObject(topHalf);
			addSceneObject(bottomHalf);
			addSceneObject(ellipticExtrusion1);
			addSceneObject(ellipticExtrusion2);
			addSceneObject(planarExtrusion1);
			addSceneObject(planarExtrusion2);
			addSceneObject(sides);
		}
	}




	public String getType()
	{
		return "Elliptic penrose cavity";
	}
}
