package optics.raytrace.GUI.sceneObjects.boxCloaks;

import java.io.Serializable;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableBoxCloak;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedConvexPolygon;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.GlensSurface;

public abstract class CloakMaker
implements Serializable
{
	private static final long serialVersionUID = -3760565743854959417L;

	/**
	 * keep a note of the box cloak that uses this BoxCloakMaker, as this gives access to all the variables
	 */
	protected EditableBoxCloak boxCloak;
	
	
	//
	// constructors
	//
	
	/**
	 * Create a new BoxCloakMaker, which is used by <boxCloak>.
	 * Keep a note of the box cloak that uses this BoxCloakMaker, as this gives access to all the variables.
	 * @param boxCloak
	 */
	public CloakMaker(EditableBoxCloak boxCloak)
	{
		setBoxCloak(boxCloak);
	}
	
	
	//
	// getters & setters
	//
	
	public EditableBoxCloak getBoxCloak() {
		return boxCloak;
	}

	public void setBoxCloak(EditableBoxCloak boxCloak) {
		this.boxCloak = boxCloak;
	}


	
	/**
	 * Using its parameters, add to the boxCloak (which is an EdibleSceneObjectCollection)
	 * all required objects, i.e. the refracting interfaces and frames
	 */
	public abstract void addSceneObjects();	
	
	
	
	//
	// a few methods common to BoxCloakMakers
	//
	
	/**
	 * Creates a glens in the shape of a 4-sided convex polygon, with vertices c1 to c4.
	 * The glens images QNeg (in -ve space) to QPos (in +ve space), and RNeg to RPos.
	 * @param description
	 * @param c1	vertex 1
	 * @param c2	vertex 2
	 * @param c3	vertex 3
	 * @param c4	vertex 4
	 * @param neg2posVector	vector pointing in the direction of the glens's +ve space; NOT NECESSARY SURFACE NORMAL!
	 * @param QNeg
	 * @param QPos
	 * @param RNeg
	 * @param RPos
	 * @param willBeSurface	GlensHolgram that will be changed to the surface of the glens (can be null)
	 * @param parent
	 * @param studio
	 * @return
	 */
	public EditableParametrisedConvexPolygon getConvexPolygonalGlens(
			String description,
			Vector3D vertices[],
			Vector3D neg2posVector,
			Vector3D QNeg, Vector3D QPos, Vector3D RNeg, Vector3D RPos,
			GlensSurface willBeSurface,
			boolean diagnosticInfo,
			SceneObject parent, Studio studio
		)
	{
		// (vertices[1]-vertices[0]) x (vertices[2]-vertices[0]) should be normal to the polygon plane
		Vector3D normal = Vector3D.crossProduct(
				Vector3D.difference(vertices[1], vertices[0]),
				Vector3D.difference(vertices[2], vertices[0])
			);
		
		// not sure how important this is, but make the normal point outwards, i.e.
		// reverse the normal if normal.outwardsDirection < 0
		if(Vector3D.scalarProduct(normal, neg2posVector) < 0)
		{
			// the normal points inwards; reverse it
			normal = normal.getReverse();
		}
		
		// if willBeSurface has not been constructed, construct it now
		if(willBeSurface == null)
		{
			willBeSurface = new GlensSurface(
					boxCloak.getInterfaceTransmissionCoefficient(),	// transmissionCoefficient,
					false	// shadowThrowing
				);
		}
		
		try
		{
			willBeSurface.setParametersUsingTwoConjugatePairs(
					vertices[0],	// pointOnGlens,
					normal,	// opticalAxisDirectionPos; "outside" is therefore in positive space
					QNeg, QPos,	// QNeg, QPos,
					RNeg, RPos	// RNeg, RPos,
				);
		} catch (RayTraceException e)
		{
			System.out.flush();
			// System.err.println("GlensHologram::GlensHologram: " + e.getMessage());
			System.err.println("CloakMaker::getConvexPolygonalGlens: description="+description
					+ ", vertices[0]="+vertices[0]
					+ ", normal="+normal
					+ ", Q-="+QNeg
					+ ", Q+="+QPos
					+ ", R-="+RNeg
					+ ", R+="+RPos
					+ ", GlensHologram="+willBeSurface
			);
			System.err.flush();
			e.printStackTrace();
			// System.exit(-1);
		}
		
		if(diagnosticInfo)
		{
			willBeSurface.setTransmissionCoefficient(0.5);
			
			System.out.println("CloakMaker::getConvexPolygonalGlens: vertices[0]="+vertices[0]
					+ ", normal="+normal
					+ ", Q-="+QNeg
					+ ", Q+="+QPos
					+ ", R-="+RNeg
					+ ", R+="+RPos
					+ ", GlensHologram="+willBeSurface
			);

		}

		return new EditableParametrisedConvexPolygon(
				description,	// description,
				normal,	// normalToPlane,
				vertices,	// vertices[],
				(boxCloak.isShowPlaceholderSurfaces())?(new GlensSurface(boxCloak.getInterfaceTransmissionCoefficient(), false)):willBeSurface,	// surfaceProperty,
				parent, studio
			);
	}

	/**
	 * Creates a glens in the shape of a triangle, with vertices c1 to c3.
	 * The glens images QNeg (in -ve space) to QPos (in +ve space), and RNeg to RPos.
	 * @param description
	 * @param c1	vertex 1
	 * @param c2	vertex 2
	 * @param c3	vertex 3
	 * @param neg2posVector	vector pointing in the direction of the glens's +ve space; NOT NECESSARY SURFACE NORMAL!
	 * @param QNeg
	 * @param QPos
	 * @param RNeg
	 * @param RPos
	 * @param parent
	 * @param studio
	 * @return
	 */
	public EditableParametrisedConvexPolygon getConvexPolygonalGlens(
			String description,
			Vector3D c1, Vector3D c2, Vector3D c3,
			Vector3D neg2posVector,
			Vector3D QNeg, Vector3D QPos, Vector3D RNeg, Vector3D RPos,
			GlensSurface willBeSurface,
			boolean diagnosticInfo,
			SceneObject parent, Studio studio
		)
	{
		Vector3D vertices[] = new Vector3D[3];
		vertices[0] = c1;
		vertices[1] = c2;
		vertices[2] = c3;

		return getConvexPolygonalGlens(description, vertices, neg2posVector, QNeg, QPos, RNeg, RPos, willBeSurface, diagnosticInfo, parent, studio);
	}
	
	/**
	 * Creates a glens in the shape of a convex 4-sided polygon, with vertices c1 to c4.
	 * The glens images QNeg (in -ve space) to QPos (in +ve space), and RNeg to RPos.
	 * @param description
	 * @param c1	vertex 1
	 * @param c2	vertex 2
	 * @param c3	vertex 3
	 * @param c4	vertex 4
	 * @param neg2posVector	vector pointing in the direction of the glens's +ve space; NOT NECESSARY SURFACE NORMAL!
	 * @param QNeg
	 * @param QPos
	 * @param RNeg
	 * @param RPos
	 * @param parent
	 * @param studio
	 * @return
	 */
	public EditableParametrisedConvexPolygon getConvexPolygonalGlens(
			String description,
			Vector3D c1, Vector3D c2, Vector3D c3, Vector3D c4,
			Vector3D neg2posVector,
			Vector3D QNeg, Vector3D QPos, Vector3D RNeg, Vector3D RPos,
			GlensSurface willBeSurface,
			boolean diagnosticInfo,
			SceneObject parent, Studio studio
		)
	{
		Vector3D vertices[] = new Vector3D[4];
		vertices[0] = c1;
		vertices[1] = c2;
		vertices[2] = c3;
		vertices[3] = c4;

		return getConvexPolygonalGlens(description, vertices, neg2posVector, QNeg, QPos, RNeg, RPos, willBeSurface, diagnosticInfo, parent, studio);
	}
	
	protected void addFrameCylinder(String description, Vector3D startPosition, Vector3D endPosition, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(new EditableParametrisedCylinder(
				description,
				startPosition,	// start point
				endPosition,	// end point
				boxCloak.getFrameRadiusOverSideLength() * boxCloak.getSideLength(),	// radius
				boxCloak.getFrameSurfaceProperty(),
				collection,
				boxCloak.getStudio()
		));
	}
	
	protected void addFrameSphere(String description, Vector3D centrePosition, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(new EditableScaledParametrisedSphere(
				description,
				centrePosition,	// centre
				boxCloak.getFrameRadiusOverSideLength() * boxCloak.getSideLength(),	// radius
				boxCloak.getFrameSurfaceProperty(),
				collection,
				boxCloak.getStudio()
		));
	}
	

	

}
