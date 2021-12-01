package optics.raytrace.sceneObjects;

import math.Vector3D;

import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A selection of basic refractive lenses .
 * @author Maik Locher
 */
public class RefractiveBoxLens extends SceneObjectIntersection	//list of limitations: refractive index>1 only(warning will appear), Default constructor needs type casing
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
	

	
	private double apertureWidth, apertureHeight, thickness, radiusOfCurvatureFront, radiusOfCurvatureBack, windowHeight, windowWidth, focalLength,refractiveIndex; 
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
	
	//height and width should be less than radius of curvature
	private Vector3D centre, frontDirection, windowCentre;
	private SurfaceProperty surface_N, surfaceN;
	private EditableScaledParametrisedSphere frontSphere, backSphere;
	private ParametrisedCylinderMantle lensCylinder, cross2;
	private ParametrisedPlane topPlane, bottomPlane, leftPlane, rightPlane, lensPlane;
	
	/**
	 * Default constructor
	 * 
	 * @param description
	 * @param apertureHeight
	 * @param apertureWidth
	 * @param windowWidth
	 * @param windowHeight
	 * @param radiusOfCurvatureFront
	 * @param radiusOfCurvatureBack
	 * @param thickness
	 * @param centre
	 * @param windowCentre
	 * @param frontDirection
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	
	public RefractiveBoxLens(
			String description,
			double apertureHeight,
			double apertureWidth,
			double windowWidth,
			double windowHeight,
			double radiusOfCurvatureFront,
			double radiusOfCurvatureBack,
			double thickness,
			double refractiveIndex,
			Vector3D centre,
			Vector3D windowCentre,
			Vector3D frontDirection,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		this.apertureHeight = apertureHeight;
		this.apertureWidth = apertureWidth;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.radiusOfCurvatureFront = radiusOfCurvatureFront;
		this.radiusOfCurvatureBack = radiusOfCurvatureBack;
		this.thickness = thickness;
		this.centre = centre;
		this.refractiveIndex = refractiveIndex;
		this.focalLength = 1/((refractiveIndex-1)*((1/radiusOfCurvatureFront)-(1/radiusOfCurvatureBack)));
		this.windowCentre = windowCentre;
		this.frontDirection = frontDirection;
		this.surface_N = surfaceProperty;
		this.type_case = 0; //not right for now

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
			double apertureHeight,
			double apertureWidth,
			double focalLength,
			double refractiveIndex,
			double thickness,
			Vector3D centre,
			Vector3D frontDirection,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureHeight = apertureHeight;
		this.apertureWidth = apertureWidth;
		this.windowWidth = apertureHeight;
		this.windowHeight = apertureWidth;
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = (2*focalLength*(refractiveIndex - 1));
		this.radiusOfCurvatureBack = (-2*focalLength*(refractiveIndex - 1));
		this.thickness = thickness;
		this.centre = centre;
		this.windowCentre = centre;
		this.frontDirection = frontDirection;
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
			double apertureHeight,
			double apertureWidth,
			double focalLength,
			double refractiveIndex,
			double thickness,
			double radiusOfCurvatureFront,
			Vector3D centre,
			Vector3D frontDirection,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureHeight = apertureHeight;
		this.apertureWidth = apertureWidth;
		this.windowWidth = apertureHeight;
		this.windowHeight = apertureWidth;
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = radiusOfCurvatureFront;
		this.radiusOfCurvatureBack = (-radiusOfCurvatureFront*focalLength*(refractiveIndex - 1))/(radiusOfCurvatureFront-focalLength*(refractiveIndex - 1));
		this.thickness = thickness;
		this.centre = centre;
		this.windowCentre = centre;
		this.frontDirection = frontDirection;
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
			double apertureHeight,
			double apertureWidth,
			double focalLength,
			double refractiveIndex,
			double thickness,
			Vector3D centre,
			double radiusOfCurvatureBack,
			Vector3D frontDirection,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureHeight = apertureHeight;
		this.apertureWidth = apertureWidth;
		this.windowWidth = apertureHeight;
		this.windowHeight = apertureWidth;
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = (focalLength*(refractiveIndex - 1)*radiusOfCurvatureBack)/(focalLength*(refractiveIndex - 1) + radiusOfCurvatureBack);
		this.radiusOfCurvatureBack = radiusOfCurvatureBack;
		this.thickness = thickness;
		this.centre = centre;
		this.windowCentre = centre;
		this.frontDirection = frontDirection;
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
	 * Constructor that creates an off centred window as part of a lens at the centre of min. thickness of equal radii. 
	 * 
	 * @param description
	 * @param windowWidth
	 * @param windowHeight
	 * @param focalLength
	 * @param refractiveIndex
	 * @param centre
	 * @param windowCentre
	 * @param frontDirection
	 * @param parent
	 * @param studio
	 */
	public RefractiveBoxLens(
			String description,
			double windowHeight,
			double windowWidth,
			double focalLength,
			double refractiveIndex,
			Vector3D centre,
			Vector3D windowCentre,
			Vector3D frontDirection,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureHeight = 2*(Math.abs(0.5*windowHeight) + Math.abs(centre.getDifferenceWith(windowCentre).y));
		// given a window height and width, calculates the required lens height and width to let the window be contained in the lens
		this.apertureWidth = 2*(Math.abs(0.5*windowWidth) + Math.abs(centre.getDifferenceWith(windowCentre).x));
		this.windowCentre = windowCentre; //will be used to cut out the remainder of the lens		
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = (2*focalLength*(refractiveIndex - 1));
		this.radiusOfCurvatureBack = (-2*focalLength*(refractiveIndex - 1));
		this.centre = centre;
		this.frontDirection = frontDirection;
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
		double u,v; //parameters describing the shift from centre to windowCentre
		v = centre.getDifferenceWith(windowCentre).y;
		u = centre.getDifferenceWith(windowCentre).x;
		double t12,t23,t34,t41; //parameter to quantify the lines at min distance: between 0 and 1
		t12 = 0.5-(u/windowWidth);
		t23 = 0.5+(v/windowHeight);
		t34 = 0.5+(u/windowWidth);
		t41 = 0.5-(v/windowHeight);
		Vector3D c1,c2,c3,c4; //corners. To make more sense, drawing on one note.
		c1 = windowCentre.getSumWith(new Vector3D(-windowWidth/2, windowHeight/2, 0));
		//top right corner
		c2 = windowCentre.getSumWith(new Vector3D(windowWidth/2, windowHeight/2, 0));
		//bottom right corner
		c3 = windowCentre.getSumWith(new Vector3D(windowWidth/2, -windowHeight/2, 0));
		//bottom left corner
		c4 = windowCentre.getSumWith(new Vector3D(-windowWidth/2, -windowHeight/2, 0));
		
		Vector3D l12,l23,l34,l41;//lines, which are a point when t is defined as above
		//line between c1 and c2
		l12 = c1.getSumWith((c2.getDifferenceWith(c1)).getWithLength(t12));
		//line between c2 and c3
		l23 = c2.getSumWith((c3.getDifferenceWith(c2)).getWithLength(t23));
		//line between c3 and c4
		l34 = c3.getSumWith((c4.getDifferenceWith(c3)).getWithLength(t34));
		//line between c4 and c1
		l41 = c4.getSumWith((c1.getDifferenceWith(c4)).getWithLength(t41));
		//now, as all those are set, define regions around the lens window using if functions. This is split into 9 regions.
		
		
		//first case for a diverging (focal length less than 0) lens
		if (focalLength<0) {
			/**For reference the regions are below showing the corners too. 
			 * 			:					:
			 * region 1	:		region 2	:	region 3
			 * 			:					:
			 *  ......c1:___________________:c2..........
			 *  		|					|
			 * region 8	| 		region 9	| 	region 4
			 *  		|	(lens window)	|
			 * .......c4|___________________|c3..........
			 *  		:					:
			 * region 7 :		region 6	:	region 5
			 * 			:					:
			 */
			//start with region 9 and define anything inside or on the lines
			if (centre.x>= (windowCentre.x-0.5*windowWidth) && 
					centre.x<= (windowCentre.x+0.5*windowWidth) && 
					centre.y <= (windowCentre.y+0.5*windowHeight)&&
					centre.y >= (windowCentre.y-0.5*windowHeight)
					) {
				this.thickness = 0;

				
				
			}else {
				//region 1 first
				if (centre.x<= (windowCentre.x-0.5*windowWidth) && centre.y >= (windowCentre.y+0.5*windowHeight)) {
					rad2 = centre.getDifferenceWith(c1).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 2
				if (centre.x>(windowCentre.x-0.5*windowWidth) && centre.x<(windowCentre.x+0.5*windowWidth) && centre.y>(windowCentre.y+0.5*windowHeight)) {
					rad2 = centre.getDifferenceWith(l12).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}	
				//region 3
				if (centre.x>=(windowCentre.x+0.5*windowWidth) && centre.y>=(windowCentre.y+0.5*windowHeight)) {
					rad2 = centre.getDifferenceWith(c2).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 4
				if (centre.x>(windowCentre.x+0.5*windowWidth) && centre.y<(windowCentre.y+0.5*windowHeight) && centre.y>(windowCentre.y-0.5*windowHeight)) {
					rad2 = centre.getDifferenceWith(l23).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 5
				if (centre.x>=(windowCentre.x+0.5*windowWidth) && centre.y<=(windowCentre.y-0.5*windowHeight)) {
					rad2 = centre.getDifferenceWith(c3).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 6
				if (centre.x<(windowCentre.x+0.5*windowWidth) && centre.x>(windowCentre.x-0.5*windowWidth) && centre.y<(windowCentre.y-0.5*windowHeight)) {
					rad2 = centre.getDifferenceWith(l34).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 7
				if (centre.x<=(windowCentre.x-0.5*windowWidth) && centre.y<=(windowCentre.y-0.5*windowHeight)) {
					rad2 = centre.getDifferenceWith(c4).getModSquared();
					this.thickness = -2*(sphereRadius-Math.sqrt(sphereRadius*sphereRadius - rad2));
				}
				//region 8
				if (centre.x<(windowCentre.x-0.5*windowWidth) && centre.y<(windowCentre.y+0.5*windowHeight) && centre.y>(windowCentre.y-0.5*windowHeight)) {
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
	 * Constructor that creates an off centred window as part of a lens at the centre regardless of lens type. Note; may not be min thickness. Here the front radius is provided 
	 * 
	 * @param description
	 * @param windowWidth
	 * @param windowHeight
	 * @param focalLength
	 * @param refractiveIndex
	 * @param centre
	 * @param windowCentre
	 * @param frontDirection
	 * @param parent
	 * @param studio
	 */
	public RefractiveBoxLens(
			String description,
			double windowHeight,
			double windowWidth,
			double focalLength,
			double refractiveIndex,
			double thickness,
			double radiusOfCurvatureFront,
			Vector3D centre,
			Vector3D windowCentre,
			Vector3D frontDirection,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureHeight = 2*(Math.abs(0.5*windowHeight) + Math.abs(centre.getDifferenceWith(windowCentre).y));
		// given a window height and width, calculates the required lens height and width to let the window be contained in the lens
		this.apertureWidth = 2*(Math.abs(0.5*windowWidth) + Math.abs(centre.getDifferenceWith(windowCentre).x));
		this.windowCentre = windowCentre; //will be used to cut out the remainder of the lens		
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = (2*focalLength*(refractiveIndex - 1));
		this.radiusOfCurvatureBack = (-2*focalLength*(refractiveIndex - 1));
		this.thickness = thickness;
		this.centre = centre;
		this.frontDirection = frontDirection;
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
	 * Constructor that creates an off centred window as part of a lens at the centre regardless of lens type. Note; may not be min thickness. Here the back radius is provided.
	 * 
	 * @param description
	 * @param windowWidth
	 * @param windowHeight
	 * @param focalLength
	 * @param refractiveIndex
	 * @param centre
	 * @param windowCentre
	 * @param frontDirection
	 * @param parent
	 * @param studio
	 */
	public RefractiveBoxLens(
			String description,
			double windowHeight,
			double windowWidth,
			double focalLength,
			double refractiveIndex,
			double thickness,
			Vector3D centre,
			double radiusOfCurvatureBack,
			Vector3D windowCentre,
			Vector3D frontDirection,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureHeight = 2*(Math.abs(0.5*windowHeight) + Math.abs(centre.getDifferenceWith(windowCentre).y));
		// given a window height and width, calculates the required lens height and width to let the window be contained in the lens
		this.apertureWidth = 2*(Math.abs(0.5*windowWidth) + Math.abs(centre.getDifferenceWith(windowCentre).x));
		this.windowCentre = windowCentre; //will be used to cut out the remainder of the lens		
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.radiusOfCurvatureFront = (2*focalLength*(refractiveIndex - 1));
		this.radiusOfCurvatureBack = (-2*focalLength*(refractiveIndex - 1));
		this.thickness = thickness;
		this.centre = centre;
		this.frontDirection = frontDirection;
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
		this.windowHeight = original.getWindowHeight();
		this.windowWidth = original.getWindowHeight();
		this.radiusOfCurvatureFront = original.getRadiusOfCurvatureFront();
		this.radiusOfCurvatureBack = original.getRadiusOfCurvatureBack();
		this.thickness = original.getthickness();
		this.focalLength = original.getFocalLength();
		this.refractiveIndex = original.getRefractiveIndex();
		this.centre = original.getCentre().clone();
		this.windowCentre = original.getWindowCentre().clone();
		this.frontDirection = original.getFrontDirection().clone();
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
	

	public double getApertureHeight() {
		return apertureHeight;
	}

	public void setApertureHeight(double apertureHeight) {
		this.apertureHeight = apertureHeight;
	}
	
	public double getApertureWidth() {
		return apertureWidth;
	}

	public void setApertureWidth(double apertureWidth) {
		this.apertureWidth = apertureWidth;
	}
	
	public double getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(double windowHeight) {
		this.windowHeight = windowHeight;
	}
	
	public double getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(double windowWidth) {
		this.windowWidth = windowWidth;
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
	
	public Vector3D getWindowCentre() {
		return windowCentre;
	}
	
	public void setWindowCentre(Vector3D windowCentre) {
		this.windowCentre = windowCentre;
	}

	public Vector3D getFrontDirection() {
		return frontDirection;
	}

	public void frontDirection(Vector3D frontDirection) {
		this.frontDirection = frontDirection;
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
	
	private void addElements()
	{
		/**
		 * Many cases for a few factors, should be able to have all basic lens types using constraints on the lenses using the plano radii as a limiting case. i.e if less than is a meniscus if more a concave/convex
		 */
		double apertureRadius = Math.sqrt(0.25*(apertureHeight*apertureHeight + apertureWidth*apertureWidth));
		//create a scene intersection to which all the components will be added
		SceneObjectPrimitiveIntersection refractiveBoxLens = new SceneObjectPrimitiveIntersection(
				"Component which tracks, adds and subtracts all the components",	// description,
				this,	// parent
				getStudio()	// studio
				);

		//Distinguish between the cases:
		if (type_case == 0) {
			if (focalLength>0) {	//Biconvex
				
				// front sphere, here referring to the sphere which creates the first surface a ray meets
				frontSphere = new EditableScaledParametrisedSphere("front surface",
						centre.getSumWith(
							frontDirection.getWithLength(
								Math.sqrt(
									radiusOfCurvatureFront*radiusOfCurvatureFront
									-(0.25*(apertureHeight*apertureHeight + apertureWidth*apertureWidth) - minMath/2) //note minMath used as it creates a lens which has minimum but not 0 thickness at thinnest part
								)
							)
						)
						,	// centre of sphere	
					Math.abs(radiusOfCurvatureFront),
					surfaceN,
					this,
					getStudio()
				);
				
				// make the direction to the front the direction to the pole
				frontSphere.setDirections(frontDirection);
				refractiveBoxLens.addPositiveSceneObjectPrimitive(frontSphere);
				
				//back sphere, here referring to the sphere which forms the end of the lens
				
				backSphere = new EditableScaledParametrisedSphere("back surface",
					centre.getSumWith(
						frontDirection.getWithLength(-
							Math.sqrt(
								radiusOfCurvatureBack*radiusOfCurvatureBack
								-(0.25*(apertureHeight*apertureHeight + apertureWidth*apertureWidth) - minMath/2) //note minMath used as it creates a lens which has minimum but not 0 thickness at thinnest part
							)
						)
					)
					,	// centre of sphere	
					Math.abs(radiusOfCurvatureBack),
					surfaceN,
					this,
					getStudio()
				);
				//setting direction
				backSphere.setDirections(frontDirection);
				refractiveBoxLens.addPositiveSceneObjectPrimitive(backSphere);
				
			}
			if (focalLength<0) {	//Biconcave
				
				//cylinder to form the positive part of the lens
				lensCylinder = new ParametrisedCylinderMantle(
						"the cylinder that will consist of the positive space part of the lens",
						centre.getSumWith(
								frontDirection.getWithLength(
									-(thickness/2 + Math.abs(radiusOfCurvatureFront) + minMath/2) //note minMath used as it creates a lens which has minimum but not 0 thickness at thinnest part
								)
							), //centre of front sphere
						centre.getSumWith(
								frontDirection.getWithLength(
									(thickness/2 + radiusOfCurvatureBack + minMath/2)
								)
							), //centre of back sphere
						Math.sqrt(0.5*apertureHeight*0.5*apertureHeight + 0.5*apertureWidth*0.5*apertureWidth), //the diagonal radius of the box 
						surface_N,//n
						this,
						getStudio()
						);
				refractiveBoxLens.addPositiveSceneObjectPrimitive(lensCylinder);
				
				//front sphere to remove the front part of the lens
				frontSphere = new EditableScaledParametrisedSphere("front negative surface",
					centre.getSumWith(
						frontDirection.getWithLength(
							-((thickness/2) + Math.abs(radiusOfCurvatureFront) + minMath/2) //note minMath used as it creates a lens which has minimum but not 0 thickness at thinnest part
						)
					),	// centre of sphere
					Math.abs(radiusOfCurvatureFront),
					surface_N,//1/n
					this,
					getStudio()
				);
				
				// make the direction to the front the direction to the pole
				frontSphere.setDirections(frontDirection);
				refractiveBoxLens.addNegativeSceneObjectPrimitive(frontSphere);

				//back sphere to remove back part of lens
				backSphere = new EditableScaledParametrisedSphere("back negative surface",
					centre.getSumWith(
						frontDirection.getWithLength(
							((thickness/2) + Math.abs(radiusOfCurvatureBack) + minMath/2)
						)
					),	// centre of sphere	
					Math.abs(radiusOfCurvatureBack),
					surface_N,//1/n
					this,
					getStudio()
				);
				backSphere.setDirections(frontDirection);
				refractiveBoxLens.addNegativeSceneObjectPrimitive(backSphere);	
				
			}
		}
		
		if (type_case == 1) { //plano converging lens
			//back of lens which is a flat plane
			lensPlane = new ParametrisedPlane(
					"the cylinder that will consist of the positive space part of the lens",
					centre, //centre of front sphere
					frontDirection.getWithLength(-1), //centre of back sphere
					surface_N,
					this,
					getStudio()
					);
			refractiveBoxLens.addNegativeSceneObjectPrimitive(lensPlane);
			
			//sphere which is the front of the lens
			frontSphere = new EditableScaledParametrisedSphere("front surface",
					centre.getSumWith(
							frontDirection.getWithLength(Math.sqrt(
									radiusOfCurvatureFront*radiusOfCurvatureFront
									-(0.25*(apertureHeight*apertureHeight + apertureWidth*apertureWidth))
								)
							)
						),	// centre of sphere
				Math.abs(radiusOfCurvatureFront),
				surfaceN,
				this,
				getStudio()
			);
			
			frontSphere.setDirections(frontDirection);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(frontSphere);
			
		}

		if (type_case == -1) { 	//plano diverging lens
			//A flat surface to act as the end of the lens cylinder
			lensPlane = new ParametrisedPlane(
					"The front(or back) plane of the surface",
					centre, //centre of front sphere
					frontDirection.getWithLength(1),
					surfaceN,
					this,
					getStudio()
					);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(lensPlane);
			
			//the sides and interior of the lens
			lensCylinder = new ParametrisedCylinderMantle(
					"the cylinder that will consist of the positive space part of the lens",
					centre, //centre of front sphere
					centre.getSumWith(
							frontDirection.getWithLength(
								-(thickness + Math.abs(radiusOfCurvatureFront))
							)
						), //centre of back sphere
					apertureRadius, //the diagonal radius of the box 
					surfaceN,
					this,
					getStudio()
					);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(lensCylinder);
			
			//the front a negative sphere which cuts out the diverging part of the lens
			frontSphere = new EditableScaledParametrisedSphere("Sphere to cut out concave shape",
					centre.getSumWith(
							frontDirection.getWithLength(
								-(Math.abs(radiusOfCurvatureFront) + thickness)
							)
						),	// centre of sphere
				Math.abs(radiusOfCurvatureFront),
				surface_N,
				this,
				getStudio()
			);
			
			frontSphere.setDirections(frontDirection);
			refractiveBoxLens.addNegativeSceneObjectPrimitive(frontSphere);
		}
		
		if (type_case == 3) { //plano converging lens
			//back of lens which is a flat plane
			lensPlane = new ParametrisedPlane(
					"the cylinder that will consist of the positive space part of the lens",
					centre, //centre of front sphere
					frontDirection.getWithLength(-1), //centre of back sphere
					surface_N,
					this,
					getStudio()
					);
			refractiveBoxLens.addNegativeSceneObjectPrimitive(lensPlane);
			
			//sphere whihc is the front of the lens
			backSphere = new EditableScaledParametrisedSphere("back surface",
					centre.getSumWith(
							frontDirection.getWithLength(Math.sqrt(
									radiusOfCurvatureBack*radiusOfCurvatureBack
									-(0.25*(apertureHeight*apertureHeight + apertureWidth*apertureWidth))
								)
							)
						),	// centre of sphere
				Math.abs(radiusOfCurvatureBack),
				surfaceN,
				this,
				getStudio()
			);
			
			backSphere.setDirections(frontDirection);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(backSphere);
		}
		
		if (type_case == -3) { 	//plano diverging lens
			//A flat surface to act as the end of the lens cylinder
			lensPlane = new ParametrisedPlane(
					"The front(or back) plane of the surface",
					centre, //centre of front sphere
					frontDirection.getWithLength(-1),
					surfaceN,
					this,
					getStudio()
					);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(lensPlane);
			
			//the sides and interior of the lens
			lensCylinder = new ParametrisedCylinderMantle(
					"the cylinder that will consist of the positive space part of the lens",
					centre, //centre of front sphere
					centre.getSumWith(
							frontDirection.getWithLength(
								(thickness + Math.abs(radiusOfCurvatureBack))
							)
						), //centre of back sphere
					0.5*Math.sqrt(apertureHeight*apertureHeight + apertureWidth*apertureWidth), //the diagonal radius of the box 
					surfaceN,
					this,
					getStudio()
					);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(lensCylinder);
			
			//the back a negative sphere which cuts out the diverging part of the lens
			backSphere = new EditableScaledParametrisedSphere("Sphere to cut out concave shape",
					centre.getSumWith(
							frontDirection.getWithLength((
									Math.abs(radiusOfCurvatureBack) + thickness)
								)
							),	// centre of sphere
				Math.abs(radiusOfCurvatureBack),
				surface_N,
				this,
				getStudio()
			);
			
			backSphere.setDirections(frontDirection);
			refractiveBoxLens.addNegativeSceneObjectPrimitive(backSphere);
		}
		

		if (type_case == 2.1) { //meniscus converging lens
			
			frontSphere = new EditableScaledParametrisedSphere("front surface",
					centre.getSumWith(
						frontDirection.getWithLength(-
							Math.sqrt(
								radiusOfCurvatureFront*radiusOfCurvatureFront - apertureRadius*apertureRadius
							)
						)
					)
					,	// centre of sphere	
				Math.abs(radiusOfCurvatureFront),
				surface_N,
				this,
				getStudio()
			);
			frontSphere.setDirections(frontDirection);
			refractiveBoxLens.addNegativeSceneObjectPrimitive(frontSphere);
			
			//create the back sphere negatively, which will cut out part of the front sphere
			backSphere = new EditableScaledParametrisedSphere("back surface",
					centre.getSumWith(
						frontDirection.getWithLength(-
							Math.sqrt(
								radiusOfCurvatureBack*radiusOfCurvatureBack	- apertureRadius*apertureRadius
							)
						)
					)
					,	// centre of sphere	
					Math.abs(radiusOfCurvatureBack),
					surfaceN,
					this,
					getStudio()
				);
				backSphere.setDirections(frontDirection);
				refractiveBoxLens.addPositiveSceneObjectPrimitive(backSphere);
		}
		
		if (type_case == -2.1) { //meniscus diverging lens
			
			//front sphere which now is now greater in size than the back sphere
			frontSphere = new EditableScaledParametrisedSphere("front surface",
					centre.getSumWith(
							frontDirection.getWithLength(
									Math.abs(radiusOfCurvatureFront) -(
									thickness + Math.abs(radiusOfCurvatureBack) -(
									 Math.sqrt(radiusOfCurvatureBack*radiusOfCurvatureBack-apertureRadius*apertureRadius)) //drawing in notes about how calc. was derived
											)
									)
							)
					,	// centre of sphere	
					Math.abs(radiusOfCurvatureFront),
					surfaceN,
					this,
					getStudio()
				);
			frontSphere.setDirections(frontDirection);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(frontSphere);
			
			//add cylinder to limit aperture size as back sphere is smaller
			lensCylinder = new 	ParametrisedCylinderMantle(
					"the cylinder that will restrict the lens size",
					centre.getSumWith(
							frontDirection.getWithLength(-(
									thickness + Math.abs(radiusOfCurvatureBack) -(
											 Math.sqrt(radiusOfCurvatureBack*radiusOfCurvatureBack-apertureRadius*apertureRadius)) //drawing in notes about how calc. was derived
													)
									)
						), //centre of front sphere
					centre.getSumWith(
							frontDirection.getWithLength(
									Math.sqrt(radiusOfCurvatureBack*radiusOfCurvatureBack-apertureRadius*apertureRadius)
							)
						), //centre of back sphere
					apertureRadius,
					surface_N,
					this,
					getStudio()
					);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(lensCylinder);
			
				// back sphere to shape the inner part of the lens negatively
				backSphere = new EditableScaledParametrisedSphere("back surface",
						centre.getSumWith(
								frontDirection.getWithLength(
										Math.sqrt(
												radiusOfCurvatureBack*radiusOfCurvatureBack
												-(0.25*(apertureHeight*apertureHeight + apertureWidth*apertureWidth))
												)
										)
								)
						,	// centre of sphere	
						Math.abs(radiusOfCurvatureBack),
						surface_N,
						this,
						getStudio()
					);
				
				// make the direction to the front the direction to the pole
				backSphere.setDirections(frontDirection);
				refractiveBoxLens.addNegativeSceneObjectPrimitive(backSphere);			
		}
		
		if (type_case == 2.2) { //meniscus converging lens
			
			frontSphere = new EditableScaledParametrisedSphere("front surface",
					centre.getSumWith(
						frontDirection.getWithLength(
							Math.sqrt(
								radiusOfCurvatureFront*radiusOfCurvatureFront - apertureRadius*apertureRadius
							)
						)
					)
					,	// centre of sphere	
					Math.abs(radiusOfCurvatureFront),
				surfaceN,
				this,
				getStudio()
			);
			frontSphere.setDirections(frontDirection);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(frontSphere);
			
			//create the back sphere negatively, which will cut out part of the front sphere
			backSphere = new EditableScaledParametrisedSphere("back surface",
					centre.getSumWith(
						frontDirection.getWithLength(
							Math.sqrt(
								radiusOfCurvatureBack*radiusOfCurvatureBack	- apertureRadius*apertureRadius
							)
						)
					)
					,	// centre of sphere	
					Math.abs(radiusOfCurvatureBack),
					surface_N,
					this,
					getStudio()
				);
				backSphere.setDirections(frontDirection);
				refractiveBoxLens.addNegativeSceneObjectPrimitive(backSphere);
		}
		
		if (type_case == -2.2) { //meniscus diverging lens
			
			//front sphere which now is now greater in size than the back sphere
			frontSphere = new EditableScaledParametrisedSphere("front surface",
					centre.getSumWith(frontDirection.getWithLength(-
							Math.sqrt(
									radiusOfCurvatureFront*radiusOfCurvatureFront
									-(0.25*(apertureHeight*apertureHeight + apertureWidth*apertureWidth))
										)
								)
							)
					,	// centre of sphere	
					Math.abs(radiusOfCurvatureFront),
					surfaceN,
					this,
					getStudio()
				);
			frontSphere.setDirections(frontDirection);
			refractiveBoxLens.addNegativeSceneObjectPrimitive(frontSphere);
			
			//add cylinder to limit aperture size as back sphere is smaller
			lensCylinder = new 	ParametrisedCylinderMantle(
					"the cylinder that will restrict the lens size",

					centre.getSumWith(
							frontDirection.getWithLength(-
									Math.sqrt(radiusOfCurvatureFront*radiusOfCurvatureFront-apertureRadius*apertureRadius)
							)
						), //centre of front sphere
					centre.getSumWith(
							frontDirection.getWithLength((thickness + Math.abs(radiusOfCurvatureFront) -(
									 Math.sqrt(radiusOfCurvatureFront*radiusOfCurvatureFront-apertureRadius*apertureRadius)))
									)
						), //centre of back sphere
					apertureRadius,
					surfaceN,
					this,
					getStudio()
					);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(lensCylinder);
			
			// back sphere to shape the inner part of the lens negatively
			backSphere = new EditableScaledParametrisedSphere("back surface",
					centre.getSumWith(
							frontDirection.getWithLength(-(Math.abs(radiusOfCurvatureBack)-(thickness + Math.abs(radiusOfCurvatureFront) -(
									 Math.sqrt(radiusOfCurvatureFront*radiusOfCurvatureFront-apertureRadius*apertureRadius)))
											)
										)
									)
					,	// centre of sphere	
					Math.abs(radiusOfCurvatureBack),
					surfaceN,
					this,
					getStudio()
				);
				
			// make the direction to the front the direction to the pole
			backSphere.setDirections(frontDirection);
			refractiveBoxLens.addPositiveSceneObjectPrimitive(backSphere);
		}
		
		if (type_case == 404) {
			
//			//add a cross
//			cross1 = new 	ParametrisedCylinderMantle(
//					"the cylinder that will restrict the lens size",
//					centre.getSumWith(new Vector3D (-0.5*apertureHeight,0.5*apertureHeight,0)),
//					centre.getSumWith(new Vector3D (0.5*apertureHeight,-0.5*apertureHeight,0)),
//					0.25*apertureRadius,
//					SurfaceColour.RED_SHINY,
//					this,
//					getStudio()
//					);

			cross2 = new 	ParametrisedCylinderMantle(
					"the cylinder that will restrict the lens size",
					centre.getSumWith(new Vector3D (0.5*apertureHeight,0.5*apertureHeight,0)),
					centre.getSumWith(new Vector3D (-0.5*apertureHeight,-0.5*apertureHeight,0)),
					0.25*apertureRadius,
					SurfaceColour.RED_SHINY,
					this,
					getStudio()
					);
			
//			addSceneObject(cross1);
			addSceneObject(cross2);
			System.out.println("The lens you are trying to make cannot be created");		
		}
		
		if (Math.abs(radiusOfCurvatureFront) < 0.5*Math.sqrt(apertureHeight*apertureHeight + apertureWidth*apertureWidth) || Math.abs(radiusOfCurvatureBack) < 0.5*Math.sqrt(apertureHeight*apertureHeight + apertureWidth*apertureWidth)) {
			System.out.println("Warning, the aperture size is greater than the smallest lens radii!");	
		}
		
		
		//adding the planes to make it a box shaped aperture
		topPlane = new ParametrisedPlane(
				"the planes which will cut the lens into a box",
				windowCentre.getSumWith(new Vector3D (0,1,0).getWithLength(0.5*windowHeight)), //point on top plane
				new Vector3D (0,-1,0), //"outside" direction
				//new Refractive(1, 1, true),
				surface_N,
				this,
				getStudio()
				);
		bottomPlane = new ParametrisedPlane(
				"the planes which will cut the lens into a box",
				windowCentre.getSumWith(new Vector3D (0,-1,0).getWithLength(0.5*windowHeight)), //point on bottom plane
				new Vector3D (0,1,0), //"outside" direction
//				new Refractive(1, 1, true),
				surface_N,
				this,
				getStudio()
				);
		leftPlane = new ParametrisedPlane(
				"the planes which will cut the lens into a box",
				windowCentre.getSumWith(new Vector3D (-1,0,0).getWithLength(0.5*windowWidth)), //point on left plane
				new Vector3D (1,0,0), //"outside" direction
//				new Refractive(1, 1, true),
				surface_N,
				this,
				getStudio()
				);
		rightPlane = new ParametrisedPlane(
				"the planes which will cut the lens into a box",
				windowCentre.getSumWith(new Vector3D (1,0,0).getWithLength(0.5*windowWidth)), //point on right plane
				new Vector3D (-1,0,0), //"outside" direction
//				new Refractive(1, 1, true),
				surface_N,
				this,
				getStudio()
				);
		refractiveBoxLens.addNegativeSceneObjectPrimitive(topPlane);
		refractiveBoxLens.addNegativeSceneObjectPrimitive(bottomPlane);
		refractiveBoxLens.addNegativeSceneObjectPrimitive(leftPlane);
		refractiveBoxLens.addNegativeSceneObjectPrimitive(rightPlane);	

		//adding the composite object i.e the concave lens as a scene object
		addSceneObject(refractiveBoxLens);
	}
	
	
	@Override
	public String getType()
	{
		return "refractive box lenses";
	}
}
