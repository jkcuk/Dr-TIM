package optics.raytrace.GUI.sceneObjects.boxCloaks;

import optics.raytrace.GUI.sceneObjects.EditableBoxCloak;

public enum BoxCloakType
{
	CUBIC("Cubic")
	, OCTAHEDRAL("Octahedral")
	, PRISMATIC("Square prismatic")
	, ROCHESTER("Rochester")
	;
	
	private String description;

	private BoxCloakType(String description)
	{
		this.description = description;
	}
	
	public CloakMaker getCloakMaker(EditableBoxCloak boxCloak)
	{
		switch(this)
		{
		case OCTAHEDRAL:
			return new OctahedralBoxCloakMaker(boxCloak);
		case PRISMATIC:
			return new PrismaticBoxCloakMaker(boxCloak);
		case ROCHESTER:
			return new RochesterCloakMaker(boxCloak);
		case CUBIC:
		default:
			return new CubicBoxCloakMaker(boxCloak);
		}
	}

	@Override
	public String toString() {return description;}
}