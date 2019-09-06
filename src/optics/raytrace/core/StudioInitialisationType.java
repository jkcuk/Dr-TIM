package optics.raytrace.core;

import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.studioInitialisation.AutostereogramResonatorInitialisation;
import optics.raytrace.studioInitialisation.ChristmasInitialisation;
import optics.raytrace.studioInitialisation.CloakingInitialisation;
import optics.raytrace.studioInitialisation.CurvedSpaceInitialisation;
import optics.raytrace.studioInitialisation.CustomInitialisation;
import optics.raytrace.studioInitialisation.DistanceLabelledPlanes1Initialisation;
import optics.raytrace.studioInitialisation.DistanceLabelledPlanes2Initialisation;
import optics.raytrace.studioInitialisation.DistanceLabelledPlanes3Initialisation;
import optics.raytrace.studioInitialisation.HalloweenInitialisation;
import optics.raytrace.studioInitialisation.HeavenInitialisation;
import optics.raytrace.studioInitialisation.LatticeInitialisation;
import optics.raytrace.studioInitialisation.METATOYInitialisation;
import optics.raytrace.studioInitialisation.MinimalistInitialisation;
import optics.raytrace.studioInitialisation.OriginalInitialisation;
import optics.raytrace.studioInitialisation.ShinyBallsInitialisation;
import optics.raytrace.studioInitialisation.SurrealistInitialisation;
import optics.raytrace.studioInitialisation.SurroundLatticeInitialisation;
import optics.raytrace.studioInitialisation.TestInitialisation;
import optics.raytrace.studioInitialisation.TimHeadInitialisation;

/**
 * An enum of studio-initialisation types available in TIM's interactive version.
 * To add a new studio-initialisation type:
 * 1) Add the type, with its description, to the enum values.
 * 2) Extend the initialiseStudio method so that it can initialise the studio according to the new type.
 * 
 * @author johannes
 */
public enum StudioInitialisationType
{
	DEFAULT(new TimHeadInitialisation()),	// new TestInitialisation()),
	AUTOSTEREOGRAM_RESONATOR(new AutostereogramResonatorInitialisation(false)),
	CHRISTMAS(new ChristmasInitialisation()),
	CLOAKING(new CloakingInitialisation()),
	CURVED_SPACE(new CurvedSpaceInitialisation()),
	CUSTOM(new CustomInitialisation()),
	DISTANCE_LABELLED_PLANES_1(new DistanceLabelledPlanes1Initialisation()),
	DISTANCE_LABELLED_PLANES_2(new DistanceLabelledPlanes2Initialisation()),
	DISTANCE_LABELLED_PLANES_3(new DistanceLabelledPlanes3Initialisation()),
	HALLOWEEN(new HalloweenInitialisation()),
	HEAVEN(new HeavenInitialisation()),
	LATTICE(new LatticeInitialisation()),
	METATOY(new METATOYInitialisation()),
	MINIMALIST(new MinimalistInitialisation()),
	MINIMALIST_LOWER_FLOOR(new MinimalistInitialisation(-3)),
	ORIGINAL(new OriginalInitialisation()),
	SHINY_BALLS(new ShinyBallsInitialisation()),
	SURREALIST(new SurrealistInitialisation()),
	SURROUND_LATTICE(new SurroundLatticeInitialisation()),
	TEST(new TestInitialisation()),
	TIM_HEAD(new TimHeadInitialisation());
	
	private StudioInitialisation studioInitialisation;
	private StudioInitialisationType(StudioInitialisation studioInitialisation) {this.studioInitialisation = studioInitialisation;}	
	@Override
	public String toString() {return studioInitialisation.getDescription();}
	private StudioInitialisation getStudioInitialisation() {return studioInitialisation;}
	
	/**
	 * an alternative to values() that gives a limited list of values, in this case those that are available in fully-interactive TIM
	 */
	public static StudioInitialisationType[] limitedValuesForInteractiveTIM = {CHRISTMAS, HALLOWEEN, HEAVEN, MINIMALIST, SHINY_BALLS, SURREALIST, SURROUND_LATTICE, TIM_HEAD, AUTOSTEREOGRAM_RESONATOR, CLOAKING, CURVED_SPACE}; 

	/**
	 * an alternative to values() that gives a limited list of values, in this case those suitable as backgrounds
	 */
	public static StudioInitialisationType[] limitedValuesForBackgrounds = {CHRISTMAS, DISTANCE_LABELLED_PLANES_1, DISTANCE_LABELLED_PLANES_2, DISTANCE_LABELLED_PLANES_3, HALLOWEEN, HEAVEN, LATTICE, MINIMALIST, SHINY_BALLS, SURREALIST, TIM_HEAD};

	/**
	 * an alternative to values() that gives a limited list of values, in this case those suitable as backgrounds
	 */
	public static StudioInitialisationType[] limitedValuesAndCustomForBackgrounds = {CHRISTMAS, CUSTOM, DISTANCE_LABELLED_PLANES_1, DISTANCE_LABELLED_PLANES_2, DISTANCE_LABELLED_PLANES_3, HALLOWEEN, HEAVEN, LATTICE, MINIMALIST, SHINY_BALLS, SURREALIST, TIM_HEAD};

	/**
	 * initialise the studio according to the studioInitialisationType
	 * @param studioInitialisationType
	 * @param studio	the studio; note that the scene must be of type EditableSceneObjectCollection
	 */
	public static void initialiseSceneAndLights(StudioInitialisationType studioInitialisationType, SceneObjectContainer sceneObjectContainer, Studio studio)
	{
		studioInitialisationType.getStudioInitialisation().initialiseSceneAndLights(sceneObjectContainer, studio);
	}	
}
