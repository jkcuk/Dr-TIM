package optics.raytrace.lights;

import java.io.Serializable;
import javax.swing.*;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.*;

/**
 * A point light source.
 * 
 * @author Dean et al.
 *
 */
public class PhongLightSource extends LightSource implements Serializable
{	
	private static final long serialVersionUID = 621210599692612723L;

	//diffuse reflection colour
	DoubleColour Id;
	//specular reflection colour
	DoubleColour Is;
	double alpha;
	//this is the position of the light source
	Vector3D p;		

	JPanel panel;
	JTextField pxField, pyField, pzField, sRedField, sGreenField, sBlueField, dRedField, dGreenField, dBlueField, alphaField;

	/**
	 * Creates a point light source with a specular and diffuse component.
	 * Renders objects realistically, taking into account each object's diffuse and specular colour.
	 * Also does shadows.
	 * 
	 * @see AmbientLight
	 * @see LightSource
	 * 
	 */
	public PhongLightSource(String description)
	{
		super(description);
		this.p = new Vector3D(20, 10, -5);
		this.Is = DoubleColour.GREY30;
		this.Id = DoubleColour.GREY30;
		this.alpha = 40;
	}

	/**
	 * Creates a point light source with a specular and diffuse component.
	 * Renders objects realistically, taking into account each object's diffuse and specular colour.
	 * Also does shadows.
	 * 
	 * @see AmbientLight
	 * @see LightSource
	 * 
	 */
	public PhongLightSource(String description, Vector3D p, DoubleColour Is, DoubleColour Id, double alpha)
	{
		super(description);
		this.p=p;
		this.Is=Is;
		this.Id=Id;
		this.alpha=alpha;
	}

	@Override
	public DoubleColour getColour(SurfaceColour surfaceColour,
			SceneObject scene, RaySceneObjectIntersection i, Ray r,
			int traceLevel)
	{
		Vector3D d = p.getDifferenceWith(i.p);	// direction from surface point to light source
		Vector3D N = i.getNormalisedOutwardsSurfaceNormal();	// surface normal at the intersection point
		Vector3D V = r.getD();  // individual ray direction

		// if the surface the light ray sees is not the side facing this light source,
		// then it's in this light source's shadow
		if(Vector3D.scalarProduct(d, V.getProjectionOnto(N)) > 0.0) return DoubleColour.BLACK;

		// calculate other shadows: send a light ray from the intersection point to the light source...
		// TODO is time 0 appropriate?
		Ray r2=new Ray(i.p,	d, 0, false);	// ray from the intersection point to the light source

		// ... and see if it intersects with anything BEFORE it hits the light source
		RaySceneObjectIntersection i2=scene.getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(r2, i.o);

		// the light ray hits something...
		if(i2!=RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// but is it further away than the light source?
			if(i2.p.getDifferenceWith(i.p).getModSquared() > d.getModSquared())
			{	
				// yes, so it's not casting a shadow, so the ray doesn't intersect
				// with this thing BEFORE it hits the light source
				i2 = RaySceneObjectIntersection.NO_INTERSECTION;
			}
		}

		if(i2==RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// there is nothing in between the intersection point and the light source;
			// calculate the diffuse and specular contributions to the Phong reflection model
			// (see http://en.wikipedia.org/wiki/Phong_shading)
			// (note that the ambient contribution is handled by a different type of light source)

			Vector3D L=Vector3D.difference(p, i.p);	// direction from the intersection to the light source
			Vector3D R=Vector3D.difference(p.getDifferenceWith(i.p),p.getDifferenceWith(i.p).getProjectionOnto(N).getProductWith(2)); // direction of the reflection
			double LdotNmag = Vector3D.scalarProduct(L, N)/(L.getLength()*N.getLength());
			double RdotVmag = Vector3D.scalarProduct(R, V)/(R.getLength()*V.getLength());

			// calculate the diffuse contribution
			DoubleColour diffuse = DoubleColour.multiply(Id.multiply(Math.abs(LdotNmag)),
					surfaceColour.getDiffuseColour());   

			if(RdotVmag > 0)
			{
				// calculate the specular contribution
				DoubleColour specular = DoubleColour.multiply(Is.multiply(Math.pow(RdotVmag, alpha)),
						surfaceColour.getSpecularColour());

				// add them together and return the sum contribution
				return diffuse.add(specular);
			}

			return diffuse;
		}

		// there is something in between the intersection point and the light source,
		// so that something must cast a shadow over the intersection point
		return DoubleColour.BLACK;
	}

	public DoubleColour getIs() {
		return Is;
	}

	public void setIs(DoubleColour is) {
		Is = is;
	}

	public DoubleColour getId() {
		return Id;
	}

	public void setId(DoubleColour id) {
		Id = id;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public Vector3D getP() {
		return p;
	}

	public void setP(Vector3D p) {
		this.p = p;
	}
}
