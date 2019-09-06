package optics.raytrace.core;

import math.Vector3D;

/**
 * Describes a coordinate transformation.
 * This is used by TransformableSceneObjects to describe the transformation.
 * 
 * @author Johannes Courtial
 */
public abstract class Transformation 
{
        /**
         * Describes the transformation of a position Vector3D.
         * 
         * @param p a position Vector3D
         * @return the transformed position Vector3D
         */
        public abstract Vector3D transformPosition(Vector3D p);
        
        /**
         * Describes the transformation of a direction Vector3D.
         * 
         * @param d	a direction Vector3D
         * @return the transformed direction Vector3D
         */
        public abstract Vector3D transformDirection(Vector3D d);       
}