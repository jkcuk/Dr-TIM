package optics.raytrace.sceneObjects;

import math.Vector3D;


import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A selection of basic refractive lenses .
 * @author Maik Locher
 */
public class RefractiveBoxLens extends SceneObjectPrimitiveIntersection	//list of limitations: refractive index>1 only(warning will appear), Default constructor needs type casing
{
	private static final long serialVersionUID = -2820166386195413792L;
	
	
	/**|Important sign convention|
	 * The front radius will refer to the radius of the first surface of the lens a light ray from the observer will hit
	 * As such, the variable called radiusOfCurvatureFront will be the one closest to the observer and the one called radiusOfCurvatureBack farthest.
	 * To successfully create plano and meniscus lenses as well as semi custom radii Bi-concave/convex lenses the right radius sign has to be used in accordance with the lens makers formula																		 
	 *For the radiusOfCurvatureFront a (+)Positive value means it is convex -> (|	while a (-)Negative value means it is concave -> )|	
	 *For the radiusOfCurvatureBack a (-)Negative value means it is convex -> |)	while a (+)Positive value means it is concave -> |(	
	 *
	 *i.e a positive front and negative back will make a Bi-convex lens -> () while a negative front and positive back will make a Bi-concave lens -> )(														 
	 */
	

	private Vector3D apertureWidth, apertureHeight, clearApertureHeight, clearApertureWidth;
	private double apertureRadius;
	private double apertureRadius2;
	private double thickness, radiusOfCurvatureFront, radiusOfCurvatureBack, focalLength,refractiveIndex; 
	private double minMath = 1E-4; // small non 0 value that does not get calculated at 0.
	private double type_case;//the type of lens to be created. is the index
	/**
	 * type_cases List
	 * 0 = Bi-convex/concave lens basic, perhaps most useful case
	 * 1 = Plano Converging, -1 = Plano diverging
	 * 2.1 = Meniscus converging, -2.1 = Meniscus diverging
	 * 2.2 = Meniscus converging, -2.2 = Meniscus diverging
	 * 3 = Plano Converging, -3 = Plano diverging
	 * 404 = error case in which a message will appear
	 */
	private Boolean addSideSurfaces = true;
	
	//height and width should be less than radius of curvature

	private Vector3D centre, clearApertureCentre;

	
	
	private SurfaceProperty surface_N, surfaceN;
	private Sphere frontSphere, backSphere;
	private CylinderMantle lensCylinder;
	private Plane topPlane, bottomPlane, leftPlane, rightPlane, lensPlane;
	
	/**
	 * Default constructor
	 * 
	 * @param description
	 * @param apertureHeight
	 * @param apertureWidth
	 * @param clearApertureWidth
	 * @param clearApertureHeight
	 * @param radiusOfCurvatureFront
	 * @param radiusOfCurvatureBack
	 * @param thickness
	 * @param centre
	 * @param clearApertureCentre
	 * @param frontDirection
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	
	public RefractiveBoxLens(
			String description,
			Vector3D apertureHeight,
			Vector3D apertureWidth,
			Vector3D clearApertureWidth,
			Vector3D clearApertureHeight,
			double radiusOfCurvatureFront,
			double radiusOfCurvatureBack,
			double thickness,
			double refractiveIndex,
			Vector3D centre,
			Vector3D clearApertureCentre,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		this.apertureHeight = apertureHeight;
		this.apertureWidth = apertureWidth;
		this.clearApertureWidth = clearApertureWidth;
		this.clearApertureHeight = clearApertureHeight;
		this.radiusOfCurvatureFront = radiusOfCurvatureFront;
		this.radiusOfCurvatureBack = radiusOfCurvatureBack;
		this.thickness = thickness;
		this.centre = centre;
		this.refractiveIndex = refractiveIndex;
		this.focalLength = 1/((refractiveIndex-1)*((1/radiusOfCurvatureFront)-(1/radiusOfCurvatureBack)));
		this.clearApertureCentre = clearApertureCentre;
		this.surface_N = surfaceProperty;
		this.type_case = 10; //not right for now

		// copy the parameters into this instance's variables
		

		addElements();
	}
	/**
	 * Constructor that uses focal length and refractive index as parameters and assumes equal curvature and circular aperture
	 * 
	 * @param description
	 * @param apertureHeight
	 * @param apertureWidth
	 * @param focalLength
	 * @param refractiveIndex
	 * @param thickness
	 * @param centre
	 * @param frontDirection
	 * @param parent
	 * @param studio
	 */
	public RefractiveBoxLens(
			String description,
			Vector3D apertureHeight,
			Vector3D apertureWidth,
			double focalLength,
			double refractiveIndex,
			double thickness,
			Vector3D centre,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureHeight = apertureHeight;
		this.apertureWidth = apertureWidth;
		this.clearApertureWidth = apertureHeight;
		this.clearApertureHeight = apertureWidth;
		this.apertureRadius= Math.sqrt(0.25*(apertureHeight.getModSquared() + apertureWidth.getModSquared()));
		this.apertureRadius2 = (0.25*(apertureHeight.getModSquared() + apertureWidth.getModSquared()));
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = (2*focalLength*(refractiveIndex - 1));
		this.radiusOfCurvatureBack = (-2*focalLength*(refractiveIndex - 1));
		this.thickness = thickness;
		this.centre = centre;
		this.clearApertureCentre = centre;
		SurfaceProperty surfaceN = new Refractive(refractiveIndex, 1, true); //N standing for a refractive index of n
		SurfaceProperty surface_N = new Refractive(1/refractiveIndex, 1, true);//_N standing for a refractive index of 1/n
		this.surfaceN = surfaceN;
		this.surface_N = surface_N;
		if (focalLength == 0) {
			this.type_case = 404; //error
		}else {
			this.type_case = 0;
		}
		if (refractiveIndex <1) {
			this.type_case = 404;
		}
		

		addElements();
	}
	
	
	/**
	 * Constructor that uses focal length and refractive index as parameters and takes the radius of the front surface of curvature. 
	 * 
	 * @param description
	 * @param apertureHeight
	 * @param apertureWidth
	 * @param focalLength
	 * @param refractiveIndex
	 * @param thickness
	 * @param radiusOfCurvatureFront
	 * @param centre	 
	 * @param frontDirection
	 * @param parent
	 * @param studio
	 */
	public RefractiveBoxLens(
			String description,
			Vector3D apertureHeight,
			Vector3D apertureWidth,
			double focalLength,
			double refractiveIndex,
			double thickness,
			double radiusOfCurvatureFront,
			Vector3D centre,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureHeight = apertureHeight;
		this.apertureWidth = apertureWidth;
		this.clearApertureWidth = apertureHeight;
		this.clearApertureHeight = apertureWidth;
		this.apertureRadius= Math.sqrt(0.25*(apertureHeight.getModSquared() + apertureWidth.getModSquared()));
		this.apertureRadius2 = (0.25*(apertureHeight.getModSquared() + apertureWidth.getModSquared()));
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = radiusOfCurvatureFront;
		this.radiusOfCurvatureBack = (-radiusOfCurvatureFront*focalLength*(refractiveIndex - 1))/(radiusOfCurvatureFront-focalLength*(refractiveIndex - 1));
		this.thickness = thickness;
		this.centre = centre;
		this.clearApertureCentre = centre;
		SurfaceProperty surfaceN = new Refractive(refractiveIndex, 1, true);
		SurfaceProperty surface_N = new Refractive(1/refractiveIndex, 1, true);
		this.surfaceN = surfaceN;
		this.surface_N = surface_N;
		//setting the type case:
		if (focalLength>0) {
			if (radiusOfCurvatureFront>0 && Math.abs(radiusOfCurvatureFront) == Math.abs(focalLength)*(refractiveIndex-1)) {
				this.type_case = 1; //plano case converging				
			}
			else if (radiusOfCurvatureFront<0) {
				this.type_case = 2.1; //meniscus converging
			}
			else if (radiusOfCurvatureFront > 0 && Math.abs(radiusOfCurvatureFront)<Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 2.2; //meniscus converging
			}
			else if (radiusOfCurvatureFront>0 && Math.abs(radiusOfCurvatureFront)>Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 0;	//Biconvex
			}
			else {
				this.type_case = 404; //error
			}
		}
		if (focalLength<0) {
			if (radiusOfCurvatureFront<0 && Math.abs(radiusOfCurvatureFront) == Math.abs(focalLength)*(refractiveIndex-1)) {
				this.type_case = -1; //plano case diverging		
			}
			else if (radiusOfCurvatureFront>0) {
				this.type_case = -2.1; //meniscus diverging
			}
			else if (radiusOfCurvatureFront<0 && Math.abs(radiusOfCurvatureFront)<Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = -2.2; //meniscus diverging
			}
			else if (radiusOfCurvatureFront<0 && Math.abs(radiusOfCurvatureFront)>Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 0;	//Biconcave
			}
			else {
				this.type_case = 404; //error
			}
		}
		if (focalLength == 0) {
			this.type_case = 404; //error
		}
		if (refractiveIndex <1) {
			this.type_case = 404;
		}
		

		addElements();
	}
	
	/**
	 * Constructor that uses focal length and refractive index as parameters and takes the radius of the back surface of curvature. 
	 * 
	 * @param description
	 * @param apertureHeight
	 * @param apertureWidth
	 * @param focalLength
	 * @param refractiveIndex
	 * @param thickness
	 * @param centre
	 * @param radiusOfCurvatureBack
	 * @param frontDirection
	 * @param parent
	 * @param studio
	 */
	public RefractiveBoxLens(
			String description,
			Vector3D apertureHeight,
			Vector3D apertureWidth,
			double focalLength,
			double refractiveIndex,
			double thickness,
			Vector3D centre,
			double radiusOfCurvatureBack,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureHeight = apertureHeight;
		this.apertureWidth = apertureWidth;
		this.clearApertureWidth = apertureHeight;
		this.clearApertureHeight = apertureWidth;
		this.apertureRadius= Math.sqrt(0.25*(apertureHeight.getModSquared() + apertureWidth.getModSquared()));
		this.apertureRadius2 = (0.25*(apertureHeight.getModSquared() + apertureWidth.getModSquared()));
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = (focalLength*(refractiveIndex - 1)*radiusOfCurvatureBack)/(focalLength*(refractiveIndex - 1) + radiusOfCurvatureBack);
		this.radiusOfCurvatureBack = radiusOfCurvatureBack;
		this.thickness = thickness;
		this.centre = centre;
		this.clearApertureCentre = centre;
		SurfaceProperty surfaceN = new Refractive(refractiveIndex, 1, true); 
		SurfaceProperty surface_N = new Refractive(1/refractiveIndex, 1, true);
		this.surfaceN = surfaceN;
		this.surface_N = surface_N;
		//setting the type case:
		if (focalLength>0) {
			if (radiusOfCurvatureBack<0 && Math.abs(radiusOfCurvatureBack) == Math.abs(focalLength)*(refractiveIndex-1)) {
				this.type_case = 3; //plano case converging				
			}
			else if (radiusOfCurvatureBack < 0 && Math.abs(radiusOfCurvatureBack)<Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 2.1; //meniscus converging
			}
			else if (radiusOfCurvatureBack>0) {
				this.type_case = 2.2; //meniscus converging
			}
			else if (radiusOfCurvatureBack<0 && Math.abs(radiusOfCurvatureBack)>Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 0;	//Biconvex
			}
			else {
				this.type_case = 404; //error
			}
		}
		if (focalLength<0) {
			if (radiusOfCurvatureBack>0 && Math.abs(radiusOfCurvatureBack) == Math.abs(focalLength)*(refractiveIndex-1)) {
				this.type_case = -3; //plano case diverging		
			}
			else if (radiusOfCurvatureBack>0 && Math.abs(radiusOfCurvatureBack)<Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = -2.1; //meniscus diverging
			}
			else if (radiusOfCurvatureBack<0) {
				this.type_case = -2.2; //meniscus diverging
			}
			else if (radiusOfCurvatureBack>0 && Math.abs(radiusOfCurvatureBack)>Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 0;	//Biconcave
			}
			else {
				this.type_case = 404; //error
			}
		}
		if (focalLength == 0) {
			this.type_case = 404; //error
		}
		if (refractiveIndex <1) {
			this.type_case = 404;
		}

		addElements();
	}
	
	
	
	
	/**
	 * Constructor that creates an off centred clearAperture as part of a lens at the centre of min. thickness of equal radii. 
	 * 
	 * @param description
	 * @param clearApertureWidth
	 * @param clearApertureHeight
	 * @param focalLength
	 * @param refractiveIndex
	 * @param centre
	 * @param clearApertureCentre
	 * @param frontDirection
	 * @param parent
	 * @param studio
	 */
	public RefractiveBoxLens(
			String description,
			Vector3D clearApertureHeight,
			Vector3D clearApertureWidth,
			double focalLength,
			double refractiveIndex,
			Vector3D centre,
			Vector3D clearApertureCentre,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureRadius = ((((clearApertureHeight.getAbs().getSumWith(clearApertureWidth.getAbs())).getProductWith(0.5)).getAbs()).getSumWith((centre.getDifferenceWith(clearApertureCentre)).getAbs())).getLength();
		this.apertureRadius2 = apertureRadius*apertureRadius;
		//lens radius needed to fill clearAperture. 
		this.clearApertureCentre = clearApertureCentre; //will be used to cut out the remainder of the lens		
		this.clearApertureWidth = clearApertureWidth;
		this.clearApertureHeight = clearApertureHeight;
		this.apertureHeight = clearApertureHeight.getNormalised();
		this.apertureWidth = clearApertureWidth.getNormalised();
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = (2*focalLength*(refractiveIndex - 1));
		this.radiusOfCurvatureBack = (-2*focalLength*(refractiveIndex - 1));
		this.centre = centre;

		SurfaceProperty surfaceN = new Refractive(refractiveIndex, 1, true);
		SurfaceProperty surface_N = new Refractive(1/refractiveIndex, 1, true);
		this.surfaceN = surfaceN;
		this.surface_N = surface_N;
		if (focalLength == 0) {
			this.type_case = 404; //error
		}else {
			this.type_case = 0;
		}
		
		double sphereRadius = Math.abs(2*focalLength*(refractiveIndex - 1)); //a variable to give the sphere radius as calculated above, for ease of use below
		
		/**
		 * Calculating parameters for which the min. thickness is achieved
		 */
		
		//define the locations of the corners of the box and an equation of the lines
		double rad2; //allowed aperture radius squared such that it is min at closest point
		double u,v; //parameters describing the shift from centre to clearApertureCentre
		v = centre.getDifferenceWith(clearApertureCentre).y;
		u = centre.getDifferenceWith(clearApertureCentre).x;
		double t12,t23,t34,t41; //parameter to quantify the lines at min distance: between 0 and 1
		t12 = 0.5-(u/clearApertureWidth.getLength());
		t23 = 0.5+(v/clearApertureHeight.getLength());
		t34 = 0.5+(u/clearApertureWidth.getLength());
		t41 = 0.5-(v/clearApertureHeight.getLength());
		Vector3D c1,c2,c3,c4; //corners. To make more sense, drawing on one note.
		c1 = clearApertureCentre.getSumWith(new Vector3D(-clearApertureWidth.getLength()/2, clearApertureHeight.getLength()/2, 0));
		//top right corner
		c2 = clearApertureCentre.getSumWith(new Vector3D(clearApertureWidth.getLength()/2, clearApertureHeight.getLength()/2, 0));
		//bottom right corner
		c3 = clearApertureCentre.getSumWith(new Vector3D(clearApertureWidth.getLength()/2, -clearApertureHeight.getLength()/2, 0));
		//bottom left corner
		c4 = clearApertureCentre.getSumWith(new Vector3D(-clearApertureWidth.getLength()/2, -clearApertureHeight.getLength()/2, 0));
		
		Vector3D l12,l23,l34,l41;//lines, which are a point when t is defined as above
		//line between c1 and c2
		l12 = c1.getSumWith((c2.getDifferenceWith(c1)).getWithLength(t12));
		//line between c2 and c3
		l23 = c2.getSumWith((c3.getDifferenceWith(c2)).getWithLength(t23));
		//line between c3 and c4
		l34 = c3.getSumWith((c4.getDifferenceWith(c3)).getWithLength(t34));
		//line between c4 and c1
		l41 = c4.getSumWith((c1.getDifferenceWith(c4)).getWithLength(t41));
		//now, as all those are set, define regions around the lens clearAperture using if functions. This is split into 9 regions.
		
		
		//first case for a diverging (focal length less than 0) lens
		if (focalLength<0) {
			/**For reference the regions are below showing the corners too. 
			 * 			:					:
			 * region 1	:		region 2	:	region 3
			 * 			:					:
			 *  ......c1:___________________:c2..........
			 *  		|					|
			 * region 8	| 		region 9	| 	region 4
			 *  		|(clearAperture)	|
			 * .......c4|___________________|c3..........
			 *  		:					:
			 * region 7 :		region 6	:	region 5
			 * 			:					:
			 */
			//start with region 9 and define anything inside or on the lines
			if (centre.x>= (clearApertureCentre.x-0.5*clearApertureWidth.getLength()) && 
					centre.x<= (clearApertureCentre.x+0.5*clearApertureWidth.getLength()) && 
					centre.y <= (clearApertureCentre.y+0.5*clearApertureHeight.getLength())&&
					centre.y >= (clearApertureCentre.y-0.5*clearApertureHeight.getLength())
					) {
				this.thickness = 0;

				
				
			}else {
				//region 1 first
				if (centre.x<= (clearApertureCentre.x-0.5*clearApertureWidth.getLength()) && centre.y >= (clearApertureCentre.y+0.5*clearApertureHeight.getLength())) {
					rad2 = centre.getDifferenceWith(c1).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 2
				if (centre.x>(clearApertureCentre.x-0.5*clearApertureWidth.getLength()) && centre.x<(clearApertureCentre.x+0.5*clearApertureWidth.getLength()) && centre.y>(clearApertureCentre.y+0.5*clearApertureHeight.getLength())) {
					rad2 = centre.getDifferenceWith(l12).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}	
				//region 3
				if (centre.x>=(clearApertureCentre.x+0.5*clearApertureWidth.getLength()) && centre.y>=(clearApertureCentre.y+0.5*clearApertureHeight.getLength())) {
					rad2 = centre.getDifferenceWith(c2).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 4
				if (centre.x>(clearApertureCentre.x+0.5*clearApertureWidth.getLength()) && centre.y<(clearApertureCentre.y+0.5*clearApertureHeight.getLength()) && centre.y>(clearApertureCentre.y-0.5*clearApertureHeight.getLength())) {
					rad2 = centre.getDifferenceWith(l23).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 5
				if (centre.x>=(clearApertureCentre.x+0.5*clearApertureWidth.getLength()) && centre.y<=(clearApertureCentre.y-0.5*clearApertureHeight.getLength())) {
					rad2 = centre.getDifferenceWith(c3).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 6
				if (centre.x<(clearApertureCentre.x+0.5*clearApertureWidth.getLength()) && centre.x>(clearApertureCentre.x-0.5*clearApertureWidth.getLength()) && centre.y<(clearApertureCentre.y-0.5*clearApertureHeight.getLength())) {
					rad2 = centre.getDifferenceWith(l34).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 7
				if (centre.x<=(clearApertureCentre.x-0.5*clearApertureWidth.getLength()) && centre.y<=(clearApertureCentre.y-0.5*clearApertureHeight.getLength())) {
					rad2 = centre.getDifferenceWith(c4).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 8
				if (centre.x<(clearApertureCentre.x-0.5*clearApertureWidth.getLength()) && centre.y<(clearApertureCentre.y+0.5*clearApertureHeight.getLength()) && centre.y>(clearApertureCentre.y-0.5*clearApertureHeight.getLength())) {
					rad2 = centre.getDifferenceWith(l41).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//that should be all the regions, including the intersections between regions taken care of.
			}	
		}
		
		//Now the case for converging i.e focal length bigger than 0
		if (focalLength>0) {
			this.thickness = 0;
		}

		addElements();
	}
	
	
	
	/**
	 * Constructor that creates an off centred clearAperture as part of a lens at the centre regardless of lens type. Note; may not be min thickness. Here the front radius is provided 
	 * 
	 * @param description
	 * @param clearApertureWidth
	 * @param clearApertureHeight
	 * @param focalLength
	 * @param refractiveIndex
	 * @param centre
	 * @param clearApertureCentre
	 * @param frontDirection
	 * @param parent
	 * @param studio
	 */
	public RefractiveBoxLens(
			String description,
			Vector3D clearApertureHeight,
			Vector3D clearApertureWidth,
			double focalLength,
			double refractiveIndex,
			double thickness,
			double radiusOfCurvatureFront,
			Vector3D centre,
			Vector3D clearApertureCentre,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureRadius = ((((clearApertureHeight.getAbs().getSumWith(clearApertureWidth.getAbs())).getProductWith(0.5)).getAbs()).getSumWith((centre.getDifferenceWith(clearApertureCentre)).getAbs())).getLength();
		this.apertureRadius2 = apertureRadius*apertureRadius;
		this.clearApertureCentre = clearApertureCentre; //will be used to cut out the remainder of the lens		
		this.clearApertureWidth = clearApertureWidth;
		this.clearApertureHeight = clearApertureHeight;
		this.apertureHeight = clearApertureHeight.getNormalised();
		this.apertureWidth = clearApertureWidth.getNormalised();
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = (2*focalLength*(refractiveIndex - 1));
		this.radiusOfCurvatureBack = (-2*focalLength*(refractiveIndex - 1));
		this.thickness = thickness;
		this.centre = centre;
		SurfaceProperty surfaceN = new Refractive(refractiveIndex, 1, true);
		SurfaceProperty surface_N = new Refractive(1/refractiveIndex, 1, true);
		this.surfaceN = surfaceN;
		this.surface_N = surface_N;
		
		//setting the type case:
		if (focalLength>0) {
			if (radiusOfCurvatureFront>0 && Math.abs(radiusOfCurvatureFront) == Math.abs(focalLength)*(refractiveIndex-1)) {
				this.type_case = 1; //plano case converging				
			}
			else if (radiusOfCurvatureFront<0) {
				this.type_case = 2.1; //meniscus converging
			}
			else if (radiusOfCurvatureFront > 0 && Math.abs(radiusOfCurvatureFront)<Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 2.2; //meniscus converging
			}
			else if (radiusOfCurvatureFront>0 && Math.abs(radiusOfCurvatureFront)>Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 0;	//Biconvex
			}
			else {
				this.type_case = 404; //error
			}
		}
		if (focalLength<0) {
			if (radiusOfCurvatureFront<0 && Math.abs(radiusOfCurvatureFront) == Math.abs(focalLength)*(refractiveIndex-1)) {
				this.type_case = -1; //plano case diverging		
			}
			else if (radiusOfCurvatureFront>0) {
				this.type_case = -2.1; //meniscus diverging
			}
			else if (radiusOfCurvatureFront<0 && Math.abs(radiusOfCurvatureFront)<Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = -2.2; //meniscus diverging
			}
			else if (radiusOfCurvatureFront<0 && Math.abs(radiusOfCurvatureFront)>Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 0;	//Biconcave
			}
			else {
				this.type_case = 404; //error
			}
		}
		if (focalLength == 0) {
			this.type_case = 404; //error
		}
		if (refractiveIndex <1) {
			this.type_case = 404;
		}
		

		addElements();
	}
	
	/**
	 * Constructor that creates an off centred clearAperture as part of a lens at the centre regardless of lens type. Note; may not be min thickness. Here the back radius is provided.
	 * 
	 * @param description
	 * @param clearApertureWidth
	 * @param clearApertureHeight
	 * @param focalLength
	 * @param refractiveIndex
	 * @param centre
	 * @param clearApertureCentre
	 * @param frontDirection
	 * @param parent
	 * @param studio
	 */
	public RefractiveBoxLens(
			String description,
			Vector3D clearApertureHeight,
			Vector3D clearApertureWidth,
			double focalLength,
			double refractiveIndex,
			double thickness,
			Vector3D centre,
			double radiusOfCurvatureBack,
			Vector3D clearApertureCentre,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureRadius = ((((clearApertureHeight.getAbs().getSumWith(clearApertureWidth.getAbs())).getProductWith(0.5)).getAbs()).getSumWith((centre.getDifferenceWith(clearApertureCentre)).getAbs())).getLength();
		this.apertureRadius2 = apertureRadius*apertureRadius;
		this.clearApertureCentre = clearApertureCentre; //will be used to cut out the remainder of the lens		
		this.clearApertureWidth = clearApertureWidth;
		this.clearApertureHeight = clearApertureHeight;
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = (2*focalLength*(refractiveIndex - 1));
		this.radiusOfCurvatureBack = (-2*focalLength*(refractiveIndex - 1));
		this.thickness = thickness;
		this.centre = centre;
		SurfaceProperty surfaceN = new Refractive(refractiveIndex, 1, true);
		SurfaceProperty surface_N = new Refractive(1/refractiveIndex, 1, true);
		this.surfaceN = surfaceN;
		this.surface_N = surface_N;
		//setting the type case:
		if (focalLength>0) {
			if (radiusOfCurvatureBack<0 && Math.abs(radiusOfCurvatureBack) == Math.abs(focalLength)*(refractiveIndex-1)) {
				this.type_case = 3; //plano case converging				
			}
			else if (radiusOfCurvatureBack < 0 && Math.abs(radiusOfCurvatureBack)<Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 2.1; //meniscus converging
			}
			else if (radiusOfCurvatureBack>0) {
				this.type_case = 2.2; //meniscus converging
			}
			else if (radiusOfCurvatureBack<0 && Math.abs(radiusOfCurvatureBack)>Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 0;	//Biconvex
			}
			else {
				this.type_case = 404; //error
			}
		}
		if (focalLength<0) {
			if (radiusOfCurvatureBack>0 && Math.abs(radiusOfCurvatureBack) == Math.abs(focalLength)*(refractiveIndex-1)) {
				this.type_case = -3; //plano case diverging		
			}
			else if (radiusOfCurvatureBack>0 && Math.abs(radiusOfCurvatureBack)<Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = -2.1; //meniscus diverging
			}
			else if (radiusOfCurvatureBack<0) {
				this.type_case = -2.2; //meniscus diverging
			}
			else if (radiusOfCurvatureBack>0 && Math.abs(radiusOfCurvatureBack)>Math.abs(focalLength)*(refractiveIndex -1)) {
				this.type_case = 0;	//Biconcave
			}
			else {
				this.type_case = 404; //error
			}
		}
		if (focalLength == 0) {
			this.type_case = 404; //error
		}
		if (refractiveIndex <1) {
			this.type_case = 404;
		}
		
		addElements();
	}
	
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public RefractiveBoxLens(RefractiveBoxLens original)
	{
		super(original);
		
		// copy the original's parameters
		this.apertureHeight = original.getApertureHeight();
		this.apertureWidth = original.getApertureWidth();
		this.clearApertureHeight = original.getclearApertureHeight();
		this.clearApertureWidth = original.getclearApertureWidth();
		this.radiusOfCurvatureFront = original.getRadiusOfCurvatureFront();
		this.radiusOfCurvatureBack = original.getRadiusOfCurvatureBack();
		this.thickness = original.getthickness();
		this.focalLength = original.getFocalLength();
		this.refractiveIndex = original.getRefractiveIndex();
		this.centre = original.getCentre().clone();
		this.clearApertureCentre = original.getclearApertureCentre().clone();
		this.surfaceN = original.getsurfaceN();
		this.surface_N = original.getsurface_N();
//		this.type_case = o
		

		
		addElements();
	}
	/**
	 * setters and getters
	 */
	@Override
	public RefractiveBoxLens clone()
	{
		return new RefractiveBoxLens(this);
	}
	

	public Vector3D getApertureHeight() {
		return apertureHeight;
	}

	public void setApertureHeight(Vector3D apertureHeight) {
		this.apertureHeight = apertureHeight;
	}
	
	public Vector3D getApertureWidth() {
		return apertureWidth;
	}

	public void setApertureWidth(Vector3D apertureWidth) {
		this.apertureWidth = apertureWidth;
	}
	
	public Vector3D getclearApertureHeight() {
		return clearApertureHeight;
	}

	public void setclearApertureHeight(Vector3D clearApertureHeight) {
		this.clearApertureHeight = clearApertureHeight;
	}
	
	public Vector3D getclearApertureWidth() {
		return clearApertureWidth;
	}

	public void setclearApertureWidth(Vector3D clearApertureWidth) {
		this.clearApertureWidth = clearApertureWidth;
	}

	public double getRadiusOfCurvatureFront() {
		return radiusOfCurvatureFront;
	}

	public void setRadiusOfCurvatureFront(double radiusOfCurvatureFront) {
		this.radiusOfCurvatureFront = radiusOfCurvatureFront;
	}

	public double getRadiusOfCurvatureBack() {
		return radiusOfCurvatureBack;
	}

	public void setRadiusOfCurvatureBack(double radiusOfCurvatureBack) {
		this.radiusOfCurvatureBack = radiusOfCurvatureBack;
	}
	
	public double getthickness() {
		return thickness;
	}
	public void setthickness(double thickness) {
		this.thickness = thickness;
	}
	
	public double getFocalLength() {
		return focalLength;
	}
	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}
	
	public double getRefractiveIndex() {
		return refractiveIndex;
	}
	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}
	
	public Vector3D getclearApertureCentre() {
		return clearApertureCentre;
	}
	
	public void setclearApertureCentre(Vector3D clearApertureCentre) {
		this.clearApertureCentre = clearApertureCentre;
	}


	public SurfaceProperty getsurfaceN()
	{
		return surfaceN;
	}
	
	public void setsurfaceN(SurfaceProperty surfaceN)
	{
		this.surfaceN = surfaceN;
	}
	
	public SurfaceProperty getsurface_N()
	{
		return surface_N;
	}
	
	public void setSurface_N(SurfaceProperty surface_N)
	{
		this.surface_N = surface_N;
	}
	
	public Boolean getAddSideSurfaces() {
		return addSideSurfaces;
	}
	
	public void setAddSideSurfaces(Boolean addSideSurfaces) {
		this.addSideSurfaces = addSideSurfaces;
	}
	
	private void addElements()
	{
		/**
		 * Many cases for a few factors, should be able to have all basic lens types using constraints on the lenses using the plano radii as a limiting case. i.e if less than is a meniscus if more a concave/convex
		 */


		Vector3D frontDirection = Vector3D.crossProduct(apertureHeight, apertureWidth).getNormalised();

		//Distinguish between the cases:
		if (type_case == 0) {
			if (focalLength>0) {	//Biconvex
				
				Vector3D frontSphereCentre = centre.getSumWith(
							frontDirection.getWithLength(
								Math.sqrt(
									radiusOfCurvatureFront*radiusOfCurvatureFront
									-(apertureRadius2 - minMath/2) //note minMath used as it creates a lens which has minimum but not 0 thickness at thinnest part
								)
							)
						);
				

				Vector3D backSphereCentre = centre.getSumWith(
						frontDirection.getWithLength(-
								Math.sqrt(
									radiusOfCurvatureBack*radiusOfCurvatureBack
									-(apertureRadius2 - minMath/2) //note minMath used as it creates a lens which has minimum but not 0 thickness at thinnest part
								)
							)
						);
				
				
				// front sphere, here referring to the sphere which creates the first surface a ray meets
				frontSphere = new Sphere("front surface",
					frontSphereCentre,	// centre of sphere	
					Math.abs(radiusOfCurvatureFront),
					surfaceN,
					this,
					getStudio()
				);
				

				
				//back sphere, here referring to the sphere which forms the end of the lens
				

				backSphere = new Sphere("back surface",
					backSphereCentre,	// centre of sphere	
					Math.abs(radiusOfCurvatureBack),
					surfaceN,
					this,
					getStudio()
				);

				addPositiveSceneObjectPrimitive(frontSphere);
				addPositiveSceneObjectPrimitive(backSphere);
				
				System.out.println("frontSpheresCentre = "+frontSphereCentre+", backSphereCentre = "+backSphereCentre+", frontSphereRadius = "+radiusOfCurvatureFront+", backSphereRadius = "+radiusOfCurvatureBack);
			}
			if (focalLength<0) {	//Biconcave
				
				Vector3D backSphereCentre =	centre.getSumWith(
						frontDirection.getWithLength(
								(thickness/2 + radiusOfCurvatureBack + minMath/2)
							)
						);
				Vector3D frontSphereCentre = centre.getSumWith(
						frontDirection.getWithLength(
								-(thickness/2 + Math.abs(radiusOfCurvatureFront) + minMath/2) //note minMath used as it creates a lens which has minimum but not 0 thickness at thinnest part
							)
						);
				
				
				//cylinder to form the positive part of the lens
				lensCylinder = new CylinderMantle(
						"the cylinder that will consist of the positive space part of the lens",
						frontSphereCentre,
						backSphereCentre,
						apertureRadius, //the diagonal radius of the box 
						surface_N,//n
						this,
						getStudio()
						);

				
				//front sphere to remove the front part of the lens
				frontSphere = new Sphere("front negative surface",
					frontSphereCentre,
					Math.abs(radiusOfCurvatureFront),
					surface_N,//1/n
					this,
					getStudio()
				);


				//back sphere to remove back part of lens
				backSphere = new Sphere("back negative surface",
					backSphereCentre,
					Math.abs(radiusOfCurvatureBack),
					surface_N,//1/n
					this,
					getStudio()
				);

				
				addPositiveSceneObjectPrimitive(lensCylinder);
				addNegativeSceneObjectPrimitive(frontSphere);
				addNegativeSceneObjectPrimitive(backSphere);	
				
			}
		}
		
		if (type_case == 1) { //plano converging lens
			
			Vector3D frontSphereCentre = centre.getSumWith(
					frontDirection.getWithLength(Math.sqrt(
							radiusOfCurvatureFront*radiusOfCurvatureFront
							-apertureRadius2
						)
					)
				);	// centre of sphere
			
			//back of lens which is a flat plane
			lensPlane = new Plane(
					"the cylinder that will consist of the positive space part of the lens",
					centre, //centre of front sphere
					frontDirection.getWithLength(-1), //centre of back sphere
					surface_N,
					this,
					getStudio()
					);

			
			//sphere which is the front of the lens
			frontSphere = new Sphere("front surface",
				frontSphereCentre,
				Math.abs(radiusOfCurvatureFront),
				surfaceN,
				this,
				getStudio()
			);
			

			addNegativeSceneObjectPrimitive(lensPlane);
			addPositiveSceneObjectPrimitive(frontSphere);
			
		}

		if (type_case == -1) { 	//plano diverging lens
			
			Vector3D frontSphereCentre = centre.getSumWith(
					frontDirection.getWithLength(
						-(thickness + Math.abs(radiusOfCurvatureFront))
					)
				); //centre of back sphere
			
			
			//A flat surface to act as the end of the lens cylinder
			lensPlane = new Plane(
					"The front(or back) plane of the surface",
					centre, //centre of front sphere
					frontDirection.getWithLength(1),
					surfaceN,
					this,
					getStudio()
					);


			
			//the sides and interior of the lens
			lensCylinder = new CylinderMantle(
					"the cylinder that will consist of the positive space part of the lens",
					centre, //centre of back sphere
					frontSphereCentre,
					apertureRadius, //the diagonal radius of the box 
					surfaceN,
					this,
					getStudio()
					);

			
			//the front a negative sphere which cuts out the diverging part of the lens
			frontSphere = new Sphere("Sphere to cut out concave shape",
				frontSphereCentre,
				Math.abs(radiusOfCurvatureFront),
				surface_N,
				this,
				getStudio()
			);
			

			addPositiveSceneObjectPrimitive(lensPlane);
			addPositiveSceneObjectPrimitive(lensCylinder);
			addNegativeSceneObjectPrimitive(frontSphere);
		}
		
		if (type_case == 3) { //plano converging lens
			Vector3D backSphereCentre = centre.getSumWith(
					frontDirection.getWithLength(Math.sqrt(
							radiusOfCurvatureBack*radiusOfCurvatureBack
							-(apertureRadius2)
						)
					)
				);	// centre of sphere
			//back of lens which is a flat plane
			lensPlane = new Plane(
					"the cylinder that will consist of the positive space part of the lens",
					centre, //centre of front sphere
					frontDirection.getWithLength(-1), //centre of back sphere
					surface_N,
					this,
					getStudio()
					);

			
			//sphere whihc is the front of the lens
			backSphere = new Sphere("back surface",
				backSphereCentre,
				Math.abs(radiusOfCurvatureBack),
				surfaceN,
				this,
				getStudio()
			);
			

			addNegativeSceneObjectPrimitive(lensPlane);
			addPositiveSceneObjectPrimitive(backSphere);
		}
		
		if (type_case == -3) { 	//plano diverging lens
			Vector3D backSphereCentre = centre.getSumWith(
					frontDirection.getWithLength(
						(thickness + Math.abs(radiusOfCurvatureBack))
					)
				); //centre of back sphere
			
			//A flat surface to act as the end of the lens cylinder
			lensPlane = new Plane(
					"The front(or back) plane of the surface",
					centre, //centre of front sphere
					frontDirection.getWithLength(-1),
					surfaceN,
					this,
					getStudio()
					);

			
			//the sides and interior of the lens
			lensCylinder = new CylinderMantle(
					"the cylinder that will consist of the positive space part of the lens",
					centre, //centre of front sphere
					backSphereCentre,
					0.5*apertureRadius, //the diagonal radius of the box 
					surfaceN,
					this,
					getStudio()
					);

			
			//the back a negative sphere which cuts out the diverging part of the lens
			backSphere = new Sphere("Sphere to cut out concave shape",
				backSphereCentre,
				Math.abs(radiusOfCurvatureBack),
				surface_N,
				this,
				getStudio()
			);
			

			addPositiveSceneObjectPrimitive(lensPlane);
			addPositiveSceneObjectPrimitive(lensCylinder);
			addNegativeSceneObjectPrimitive(backSphere);
		}
		

		if (type_case == 2.1) { //meniscus converging lens
			Vector3D frontShpereCentre = centre.getSumWith(
					frontDirection.getWithLength(-
						Math.sqrt(
							radiusOfCurvatureFront*radiusOfCurvatureFront - apertureRadius2
						)
					)
				);	// centre of front sphere
			Vector3D backSphereCentre = centre.getSumWith(
						frontDirection.getWithLength(-
							Math.sqrt(
								radiusOfCurvatureBack*radiusOfCurvatureBack	- apertureRadius2
							)
						)
					);	// centre of back sphere	
			
			frontSphere = new Sphere("front surface",
				frontShpereCentre,
				Math.abs(radiusOfCurvatureFront),
				surface_N,
				this,
				getStudio()
			);
			
			//create the back sphere negatively, which will cut out part of the front sphere
			backSphere = new Sphere("back surface",
					backSphereCentre,
					Math.abs(radiusOfCurvatureBack),
					surfaceN,
					this,
					getStudio()
				);


				addNegativeSceneObjectPrimitive(frontSphere);		
				addPositiveSceneObjectPrimitive(backSphere);
		}
		
		if (type_case == -2.1) { //meniscus diverging lens
			Vector3D frontSphereCentre = centre.getSumWith(
					frontDirection.getWithLength(
							Math.abs(radiusOfCurvatureFront) -(
							thickness + Math.abs(radiusOfCurvatureBack) -(
							 Math.sqrt(radiusOfCurvatureBack*radiusOfCurvatureBack-apertureRadius2)) //drawing in notes about how calc. was derived
									)
							)
					);	// centre of front sphere
			Vector3D backSphereCentre = centre.getSumWith(
					frontDirection.getWithLength(
							Math.sqrt(
									radiusOfCurvatureBack*radiusOfCurvatureBack
									-(apertureRadius2)
									)
							)
					);	// centre of back sphere
			
			//front sphere which now is now greater in size than the back sphere
			frontSphere = new Sphere("front surface",
					frontSphereCentre,
					Math.abs(radiusOfCurvatureFront),
					surfaceN,
					this,
					getStudio()
				);

			
			//add cylinder to limit aperture size as back sphere is smaller
			lensCylinder = new 	CylinderMantle(
					"the cylinder that will restrict the lens size",
					frontSphereCentre,
					backSphereCentre,
					apertureRadius,
					surface_N,
					this,
					getStudio()
					);

			
				// back sphere to shape the inner part of the lens negatively
				backSphere = new Sphere("back surface",
						backSphereCentre,	
						Math.abs(radiusOfCurvatureBack),
						surface_N,
						this,
						getStudio()
					);
				

				addPositiveSceneObjectPrimitive(frontSphere);		
				addPositiveSceneObjectPrimitive(lensCylinder);
				addNegativeSceneObjectPrimitive(backSphere);			
		}
		
		if (type_case == 2.2) { //meniscus converging lens
			Vector3D frontSphereCentre = centre.getSumWith(
						frontDirection.getWithLength(
							Math.sqrt(
								radiusOfCurvatureFront*radiusOfCurvatureFront - apertureRadius*apertureRadius
							)
						)
					);	// centre of front sphere
			Vector3D backSphereCentre = centre.getSumWith(
					frontDirection.getWithLength(
						Math.sqrt(
							radiusOfCurvatureBack*radiusOfCurvatureBack	- apertureRadius*apertureRadius
						)
					)
				);// centre of back sphere	
			
			frontSphere = new Sphere("front surface",
				frontSphereCentre,
				Math.abs(radiusOfCurvatureFront),
				surfaceN,
				this,
				getStudio()
			);

			
			//create the back sphere negatively, which will cut out part of the front sphere
			backSphere = new Sphere("back surface",
					backSphereCentre,
					Math.abs(radiusOfCurvatureBack),
					surface_N,
					this,
					getStudio()
				);

			
				addPositiveSceneObjectPrimitive(frontSphere);
				addNegativeSceneObjectPrimitive(backSphere);
		}
		
		if (type_case == -2.2) { //meniscus diverging lens
			
			Vector3D frontSphereCentre = centre.getSumWith(frontDirection.getWithLength(-
					Math.sqrt(
							radiusOfCurvatureFront*radiusOfCurvatureFront
							-(apertureRadius2)
								)
						)
					);	// centre of front sphere
			Vector3D backSphereCentre = centre.getSumWith(
					frontDirection.getWithLength(-(Math.abs(radiusOfCurvatureBack)-(thickness + Math.abs(radiusOfCurvatureFront) -(
							 Math.sqrt(radiusOfCurvatureFront*radiusOfCurvatureFront-apertureRadius*apertureRadius)))
									)
								)
							);// centre of back sphere	
			
			//front sphere which now is now greater in size than the back sphere
			frontSphere = new Sphere("front surface",
					frontSphereCentre,
					Math.abs(radiusOfCurvatureFront),
					surfaceN,
					this,
					getStudio()
				);

			
			//add cylinder to limit aperture size as back sphere is smaller
			lensCylinder = new 	CylinderMantle(
					"the cylinder that will restrict the lens size",
					frontSphereCentre,
					backSphereCentre,
					apertureRadius,
					surfaceN,
					this,
					getStudio()
					);

			
			// back sphere to shape the inner part of the lens negatively
			backSphere = new Sphere("back surface",
					backSphereCentre,
					Math.abs(radiusOfCurvatureBack),
					surfaceN,
					this,
					getStudio()
				);
				

			
			addNegativeSceneObjectPrimitive(frontSphere);
			addPositiveSceneObjectPrimitive(lensCylinder);
			addPositiveSceneObjectPrimitive(backSphere);
		}
		
		if (type_case == 404) {
			

			System.out.println("The lens you are trying to make cannot be created");		
		}
		
		if (Math.abs(radiusOfCurvatureFront) < 0.5*apertureRadius || Math.abs(radiusOfCurvatureBack) < 0.5*apertureRadius) {
			System.out.println("Warning, the aperture size is greater than the smallest lens radii!");	
		}
		
		
		//adding the planes to make it a box shaped aperture
		topPlane = new Plane(
				"the planes which will cut the lens into a box",
				clearApertureCentre.getSumWith(clearApertureHeight.getProductWith(0.5)), //point on top plane
				clearApertureHeight.getWithLength(-1), //"outside" direction
				surface_N,
				this,
				getStudio()
				);
		bottomPlane = new Plane(
				"the planes which will cut the lens into a box",
				clearApertureCentre.getSumWith(clearApertureHeight.getProductWith(-0.5)), //point on bottom plane
				clearApertureHeight.getWithLength(1), //"outside" direction
				surface_N,
				this,
				getStudio()
				);
		leftPlane = new Plane(
				"the planes which will cut the lens into a box",
				clearApertureCentre.getSumWith(clearApertureWidth.getProductWith(0.5)), //point on left plane
				clearApertureWidth.getWithLength(-1), //"outside" direction
				surface_N,
				this,
				getStudio()
				);
		rightPlane = new Plane(
				"the planes which will cut the lens into a box",
				clearApertureCentre.getSumWith(clearApertureWidth.getProductWith(-0.5)), //point on right plane
				clearApertureWidth.getWithLength(1), //"outside" direction
				surface_N,
				this,
				getStudio()
				);
		if(addSideSurfaces)
		{
			addNegativeSceneObjectPrimitive(topPlane);
			addNegativeSceneObjectPrimitive(bottomPlane);
			addNegativeSceneObjectPrimitive(leftPlane);
			addNegativeSceneObjectPrimitive(rightPlane);
		}

	}
	
	
	@Override
	public String getType()
	{
		return "refractive box lenses";
	}
}
