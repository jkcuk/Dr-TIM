package optics.raytrace.surfaces.diffraction;

import math.InterpolatedFunction1D;
import math.Vector3D;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.surfaces.PhaseHologram;

/**
 * The inverse cumulative probability function for the light-ray direction change due to single-slit diffraction.
 * This function is calculated numerically, from a pre-calculated table of values.
 * @author johannes
 */
public class SingleSlitDiffraction
{
	// The far-field intensity distribution of a single slit of width w under normal, uniform plane-wave, illumination is of the form 
	// 	I(theta) = I_0 sinc^2(w pi/lambda sin(theta))
	// (see https://en.wikipedia.org/wiki/Diffraction).
	// This becomes the probability density p(x) = (2/pi) sinc^2(x) of a light ray to change direction, where
	//  x = w pi / lambda sin(theta).
	// The corresponding cumulative probability distribution is P(x) = Integrate[p(x'), {x', -infinity, x}] is then (according to Mathematica)
	// 	P(x) = (2 (-(Sin[Infty]^2/Infty) - Sin[X]^2/X + SinIntegral[2 Infty] + SinIntegral[2 X]))/pi,
	// or, simplified,
	// 	P(x) = 1/Pi (-(Sin[X]^2/X) + SinIntegral[2 X]) + 0.5.
	// The inverse function, x(P), is not easily expressed explicitly, but can be calculated numerically, which is what the table below is.
	// (It was calculated in the Mathematica notebook slitDiffraction.nb.)
	private static double[][] PxTABLE = 
		{{0., -8.82104e15}, {0.001, -158.91}, {0.002, -79.8397}, {0.003, 
			-52.5532}, {0.004, -39.5406}, {0.005, -32.3138}, {0.006, -26.6235}, 
			{0.007, -23.1256}, {0.008, -20.16}, {0.009, -17.4971}, {0.01, 
			-16.3796}, {0.011, -14.3209}, {0.012, -13.6705}, {0.013, -11.733}, 
			{0.014, -11.2048}, {0.015, -10.8192}, {0.016, -10.4016}, {0.017, 
			-8.87415}, {0.018, -8.40776}, {0.019, -8.14789}, {0.02, -7.93667}, 
			{0.021, -7.74296}, {0.022, -7.55055}, {0.023, -7.3432}, {0.024, 
			-7.08855}, {0.025, -6.51235}, {0.026, -5.58911}, {0.027, -5.40089}, 
			{0.028, -5.26583}, {0.029, -5.15528}, {0.03, -5.05908}, {0.031, 
			-4.97228}, {0.032, -4.89202}, {0.033, -4.81645}, {0.034, -4.7443}, 
			{0.035, -4.6746}, {0.036, -4.60657}, {0.037, -4.53956}, {0.038, 
			-4.47295}, {0.039, -4.40615}, {0.04, -4.33855}, {0.041, -4.26943}, 
			{0.042, -4.19792}, {0.043, -4.12288}, {0.044, -4.04268}, {0.045, 
			-3.95473}, {0.046, -3.85437}, {0.047, -3.73131}, {0.048, -3.54973}, 
			{0.049, -2.81991}, {0.05, -2.66574}, {0.051, -2.57839}, {0.052, 
			-2.51376}, {0.053, -2.46129}, {0.054, -2.41656}, {0.055, -2.37728}, 
			{0.056, -2.34206}, {0.057, -2.31}, {0.058, -2.28049}, {0.059, 
			-2.25309}, {0.06, -2.22745}, {0.061, -2.20332}, {0.062, -2.1805}, 
			{0.063, -2.15882}, {0.064, -2.13816}, {0.065, -2.1184}, {0.066, 
			-2.09945}, {0.067, -2.08123}, {0.068, -2.06368}, {0.069, -2.04673}, 
			{0.07, -2.03035}, {0.071, -2.01448}, {0.072, -1.99908}, {0.073, 
			-1.98412}, {0.074, -1.96958}, {0.075, -1.95541}, {0.076, -1.94161}, 
			{0.077, -1.92814}, {0.078, -1.91499}, {0.079, -1.90213}, {0.08, 
			-1.88956}, {0.081, -1.87725}, {0.082, -1.86519}, {0.083, -1.85338}, 
			{0.084, -1.84179}, {0.085, -1.83041}, {0.086, -1.81925}, {0.087, 
			-1.80828}, {0.088, -1.7975}, {0.089, -1.78689}, {0.09, -1.77646}, 
			{0.091, -1.7662}, {0.092, -1.75609}, {0.093, -1.74614}, {0.094, 
			-1.73633}, {0.095, -1.72667}, {0.096, -1.71714}, {0.097, -1.70774}, 
			{0.098, -1.69846}, {0.099, -1.68931}, {0.1, -1.68028}, {0.101, 
			-1.67136}, {0.102, -1.66254}, {0.103, -1.65384}, {0.104, -1.64524}, 
			{0.105, -1.63674}, {0.106, -1.62833}, {0.107, -1.62002}, {0.108, 
			-1.6118}, {0.109, -1.60367}, {0.11, -1.59562}, {0.111, -1.58766}, 
			{0.112, -1.57978}, {0.113, -1.57198}, {0.114, -1.56425}, {0.115, 
			-1.5566}, {0.116, -1.54903}, {0.117, -1.54152}, {0.118, -1.53408}, 
			{0.119, -1.52671}, {0.12, -1.51941}, {0.121, -1.51217}, {0.122, 
			-1.50499}, {0.123, -1.49787}, {0.124, -1.49082}, {0.125, -1.48382}, 
			{0.126, -1.47688}, {0.127, -1.46999}, {0.128, -1.46316}, {0.129, 
			-1.45638}, {0.13, -1.44966}, {0.131, -1.44298}, {0.132, -1.43636}, 
			{0.133, -1.42978}, {0.134, -1.42326}, {0.135, -1.41677}, {0.136, 
			-1.41034}, {0.137, -1.40395}, {0.138, -1.3976}, {0.139, -1.3913}, 
			{0.14, -1.38504}, {0.141, -1.37882}, {0.142, -1.37264}, {0.143, 
			-1.3665}, {0.144, -1.36041}, {0.145, -1.35435}, {0.146, -1.34832}, 
			{0.147, -1.34234}, {0.148, -1.33639}, {0.149, -1.33048}, {0.15, 
			-1.3246}, {0.151, -1.31876}, {0.152, -1.31295}, {0.153, -1.30717}, 
			{0.154, -1.30143}, {0.155, -1.29572}, {0.156, -1.29004}, {0.157, 
			-1.28439}, {0.158, -1.27878}, {0.159, -1.27319}, {0.16, -1.26763}, 
			{0.161, -1.2621}, {0.162, -1.25661}, {0.163, -1.25114}, {0.164, 
			-1.24569}, {0.165, -1.24028}, {0.166, -1.23489}, {0.167, -1.22953}, 
			{0.168, -1.22419}, {0.169, -1.21888}, {0.17, -1.2136}, {0.171, 
			-1.20834}, {0.172, -1.20311}, {0.173, -1.1979}, {0.174, -1.19271}, 
			{0.175, -1.18755}, {0.176, -1.18241}, {0.177, -1.17729}, {0.178, 
			-1.1722}, {0.179, -1.16713}, {0.18, -1.16208}, {0.181, -1.15705}, 
			{0.182, -1.15204}, {0.183, -1.14706}, {0.184, -1.1421}, {0.185, 
			-1.13715}, {0.186, -1.13223}, {0.187, -1.12732}, {0.188, -1.12244}, 
			{0.189, -1.11758}, {0.19, -1.11273}, {0.191, -1.10791}, {0.192, 
			-1.1031}, {0.193, -1.09831}, {0.194, -1.09354}, {0.195, -1.08879}, 
			{0.196, -1.08405}, {0.197, -1.07934}, {0.198, -1.07464}, {0.199, 
			-1.06995}, {0.2, -1.06529}, {0.201, -1.06064}, {0.202, -1.05601}, 
			{0.203, -1.05139}, {0.204, -1.04679}, {0.205, -1.04221}, {0.206, 
			-1.03764}, {0.207, -1.03308}, {0.208, -1.02855}, {0.209, -1.02402}, 
			{0.21, -1.01952}, {0.211, -1.01502}, {0.212, -1.01055}, {0.213, 
			-1.00608}, {0.214, -1.00163}, {0.215, -0.997199}, {0.216, -0.992778}, 
			{0.217, -0.98837}, {0.218, -0.983977}, {0.219, -0.979597}, {0.22, 
			-0.97523}, {0.221, -0.970877}, {0.222, -0.966537}, {0.223, 
			-0.962209}, {0.224, -0.957895}, {0.225, -0.953593}, {0.226, 
			-0.949304}, {0.227, -0.945026}, {0.228, -0.940762}, {0.229, 
			-0.936509}, {0.23, -0.932268}, {0.231, -0.928039}, {0.232, 
			-0.923822}, {0.233, -0.919617}, {0.234, -0.915423}, {0.235, 
			-0.91124}, {0.236, -0.907069}, {0.237, -0.902908}, {0.238, 
			-0.898759}, {0.239, -0.89462}, {0.24, -0.890493}, {0.241, -0.886376}, 
			{0.242, -0.882269}, {0.243, -0.878173}, {0.244, -0.874088}, {0.245, 
			-0.870012}, {0.246, -0.865947}, {0.247, -0.861892}, {0.248, 
			-0.857846}, {0.249, -0.853811}, {0.25, -0.849785}, {0.251, 
			-0.845769}, {0.252, -0.841762}, {0.253, -0.837765}, {0.254, 
			-0.833777}, {0.255, -0.829799}, {0.256, -0.825829}, {0.257, 
			-0.821869}, {0.258, -0.817918}, {0.259, -0.813975}, {0.26, 
			-0.810042}, {0.261, -0.806117}, {0.262, -0.802201}, {0.263, 
			-0.798293}, {0.264, -0.794394}, {0.265, -0.790503}, {0.266, 
			-0.78662}, {0.267, -0.782746}, {0.268, -0.77888}, {0.269, -0.775022}, 
			{0.27, -0.771172}, {0.271, -0.76733}, {0.272, -0.763495}, {0.273, 
			-0.759669}, {0.274, -0.75585}, {0.275, -0.752039}, {0.276, 
			-0.748235}, {0.277, -0.744439}, {0.278, -0.740651}, {0.279, 
			-0.736869}, {0.28, -0.733095}, {0.281, -0.729328}, {0.282, 
			-0.725569}, {0.283, -0.721816}, {0.284, -0.71807}, {0.285, 
			-0.714332}, {0.286, -0.7106}, {0.287, -0.706875}, {0.288, -0.703157}, 
			{0.289, -0.699445}, {0.29, -0.69574}, {0.291, -0.692042}, {0.292, 
			-0.68835}, {0.293, -0.684665}, {0.294, -0.680986}, {0.295, 
			-0.677313}, {0.296, -0.673647}, {0.297, -0.669986}, {0.298, 
			-0.666332}, {0.299, -0.662685}, {0.3, -0.659043}, {0.301, -0.655407}, 
			{0.302, -0.651777}, {0.303, -0.648153}, {0.304, -0.644535}, {0.305, 
			-0.640922}, {0.306, -0.637316}, {0.307, -0.633715}, {0.308, 
			-0.630119}, {0.309, -0.626529}, {0.31, -0.622945}, {0.311, 
			-0.619366}, {0.312, -0.615793}, {0.313, -0.612225}, {0.314, 
			-0.608662}, {0.315, -0.605105}, {0.316, -0.601552}, {0.317, 
			-0.598005}, {0.318, -0.594463}, {0.319, -0.590927}, {0.32, 
			-0.587395}, {0.321, -0.583868}, {0.322, -0.580346}, {0.323, 
			-0.576829}, {0.324, -0.573317}, {0.325, -0.56981}, {0.326, 
			-0.566307}, {0.327, -0.56281}, {0.328, -0.559317}, {0.329, 
			-0.555828}, {0.33, -0.552344}, {0.331, -0.548865}, {0.332, -0.54539}, 
			{0.333, -0.54192}, {0.334, -0.538454}, {0.335, -0.534993}, {0.336, 
			-0.531536}, {0.337, -0.528083}, {0.338, -0.524634}, {0.339, 
			-0.52119}, {0.34, -0.51775}, {0.341, -0.514314}, {0.342, -0.510882}, 
			{0.343, -0.507454}, {0.344, -0.50403}, {0.345, -0.500611}, {0.346, 
			-0.497195}, {0.347, -0.493783}, {0.348, -0.490375}, {0.349, 
			-0.486971}, {0.35, -0.483571}, {0.351, -0.480174}, {0.352, 
			-0.476782}, {0.353, -0.473393}, {0.354, -0.470007}, {0.355, 
			-0.466626}, {0.356, -0.463248}, {0.357, -0.459873}, {0.358, 
			-0.456502}, {0.359, -0.453135}, {0.36, -0.449771}, {0.361, -0.44641}, 
			{0.362, -0.443053}, {0.363, -0.439699}, {0.364, -0.436349}, {0.365, 
			-0.433001}, {0.366, -0.429657}, {0.367, -0.426317}, {0.368, 
			-0.422979}, {0.369, -0.419645}, {0.37, -0.416314}, {0.371, 
			-0.412986}, {0.372, -0.409661}, {0.373, -0.406339}, {0.374, 
			-0.40302}, {0.375, -0.399704}, {0.376, -0.396392}, {0.377, 
			-0.393082}, {0.378, -0.389775}, {0.379, -0.38647}, {0.38, -0.383169}, 
			{0.381, -0.37987}, {0.382, -0.376575}, {0.383, -0.373282}, {0.384, 
			-0.369991}, {0.385, -0.366704}, {0.386, -0.363419}, {0.387, 
			-0.360136}, {0.388, -0.356857}, {0.389, -0.35358}, {0.39, -0.350305}, 
			{0.391, -0.347033}, {0.392, -0.343763}, {0.393, -0.340496}, {0.394, 
			-0.337232}, {0.395, -0.333969}, {0.396, -0.330709}, {0.397, 
			-0.327452}, {0.398, -0.324197}, {0.399, -0.320944}, {0.4, -0.317693}, 
			{0.401, -0.314445}, {0.402, -0.311199}, {0.403, -0.307955}, {0.404, 
			-0.304713}, {0.405, -0.301474}, {0.406, -0.298236}, {0.407, 
			-0.295001}, {0.408, -0.291768}, {0.409, -0.288536}, {0.41, 
			-0.285307}, {0.411, -0.28208}, {0.412, -0.278855}, {0.413, 
			-0.275631}, {0.414, -0.27241}, {0.415, -0.26919}, {0.416, -0.265973}, 
			{0.417, -0.262757}, {0.418, -0.259543}, {0.419, -0.256331}, {0.42, 
			-0.25312}, {0.421, -0.249911}, {0.422, -0.246704}, {0.423, 
			-0.243499}, {0.424, -0.240296}, {0.425, -0.237094}, {0.426, 
			-0.233893}, {0.427, -0.230695}, {0.428, -0.227498}, {0.429, 
			-0.224302}, {0.43, -0.221108}, {0.431, -0.217915}, {0.432, 
			-0.214724}, {0.433, -0.211535}, {0.434, -0.208347}, {0.435, 
			-0.20516}, {0.436, -0.201974}, {0.437, -0.19879}, {0.438, -0.195608}, 
			{0.439, -0.192426}, {0.44, -0.189246}, {0.441, -0.186068}, {0.442, 
			-0.18289}, {0.443, -0.179714}, {0.444, -0.176539}, {0.445, 
			-0.173365}, {0.446, -0.170192}, {0.447, -0.167021}, {0.448, 
			-0.163851}, {0.449, -0.160681}, {0.45, -0.157513}, {0.451, 
			-0.154346}, {0.452, -0.15118}, {0.453, -0.148015}, {0.454, -0.14485}, 
			{0.455, -0.141687}, {0.456, -0.138525}, {0.457, -0.135364}, {0.458, 
			-0.132203}, {0.459, -0.129044}, {0.46, -0.125885}, {0.461, 
			-0.122727}, {0.462, -0.11957}, {0.463, -0.116414}, {0.464, 
			-0.113259}, {0.465, -0.110104}, {0.466, -0.10695}, {0.467, 
			-0.103797}, {0.468, -0.100644}, {0.469, -0.0974923}, {0.47, 
			-0.094341}, {0.471, -0.0911904}, {0.472, -0.0880404}, {0.473, 
			-0.0848909}, {0.474, -0.0817421}, {0.475, -0.0785937}, {0.476, 
			-0.0754459}, {0.477, -0.0722986}, {0.478, -0.0691518}, {0.479, 
			-0.0660054}, {0.48, -0.0628594}, {0.481, -0.0597139}, {0.482, 
			-0.0565688}, {0.483, -0.053424}, {0.484, -0.0502796}, {0.485, 
			-0.0471355}, {0.486, -0.0439918}, {0.487, -0.0408483}, {0.488, 
			-0.0377051}, {0.489, -0.0345621}, {0.49, -0.0314194}, {0.491, 
			-0.0282768}, {0.492, -0.0251345}, {0.493, -0.0219923}, {0.494, 
			-0.0188503}, {0.495, -0.0157084}, {0.496, -0.0125666}, {0.497, 
			-0.00942487}, {0.498, -0.00628321}, {0.499, -0.0031416}, {0.5, 
			-6.29981e-21}, {0.501, 0.0031416}, {0.502, 0.00628321}, {0.503, 
			  0.00942487}, {0.504, 0.0125666}, {0.505, 0.0157084}, {0.506, 
			  0.0188503}, {0.507, 0.0219923}, {0.508, 0.0251345}, {0.509, 
			  0.0282768}, {0.51, 0.0314194}, {0.511, 0.0345621}, {0.512, 
			  0.0377051}, {0.513, 0.0408483}, {0.514, 0.0439918}, {0.515, 
			  0.0471355}, {0.516, 0.0502796}, {0.517, 0.053424}, {0.518, 
			  0.0565688}, {0.519, 0.0597139}, {0.52, 0.0628594}, {0.521, 
			  0.0660054}, {0.522, 0.0691518}, {0.523, 0.0722986}, {0.524, 
			  0.0754459}, {0.525, 0.0785937}, {0.526, 0.0817421}, {0.527, 
			  0.0848909}, {0.528, 0.0880404}, {0.529, 0.0911904}, {0.53, 
			  0.094341}, {0.531, 0.0974923}, {0.532, 0.100644}, {0.533, 
			  0.103797}, {0.534, 0.10695}, {0.535, 0.110104}, {0.536, 
			  0.113259}, {0.537, 0.116414}, {0.538, 0.11957}, {0.539, 
			  0.122727}, {0.54, 0.125885}, {0.541, 0.129044}, {0.542, 
			  0.132203}, {0.543, 0.135364}, {0.544, 0.138525}, {0.545, 
			  0.141687}, {0.546, 0.14485}, {0.547, 0.148015}, {0.548, 
			  0.15118}, {0.549, 0.154346}, {0.55, 0.157513}, {0.551, 
			  0.160681}, {0.552, 0.163851}, {0.553, 0.167021}, {0.554, 
			  0.170192}, {0.555, 0.173365}, {0.556, 0.176539}, {0.557, 
			  0.179714}, {0.558, 0.18289}, {0.559, 0.186068}, {0.56, 
			  0.189246}, {0.561, 0.192426}, {0.562, 0.195608}, {0.563, 
			  0.19879}, {0.564, 0.201974}, {0.565, 0.20516}, {0.566, 
			  0.208347}, {0.567, 0.211535}, {0.568, 0.214724}, {0.569, 
			  0.217915}, {0.57, 0.221108}, {0.571, 0.224302}, {0.572, 
			  0.227498}, {0.573, 0.230695}, {0.574, 0.233893}, {0.575, 
			  0.237094}, {0.576, 0.240296}, {0.577, 0.243499}, {0.578, 
			  0.246704}, {0.579, 0.249911}, {0.58, 0.25312}, {0.581, 
			  0.256331}, {0.582, 0.259543}, {0.583, 0.262757}, {0.584, 
			  0.265973}, {0.585, 0.26919}, {0.586, 0.27241}, {0.587, 
			  0.275631}, {0.588, 0.278855}, {0.589, 0.28208}, {0.59, 
			  0.285307}, {0.591, 0.288536}, {0.592, 0.291768}, {0.593, 
			  0.295001}, {0.594, 0.298236}, {0.595, 0.301474}, {0.596, 
			  0.304713}, {0.597, 0.307955}, {0.598, 0.311199}, {0.599, 
			  0.314445}, {0.6, 0.317693}, {0.601, 0.320944}, {0.602, 
			  0.324197}, {0.603, 0.327452}, {0.604, 0.330709}, {0.605, 
			  0.333969}, {0.606, 0.337232}, {0.607, 0.340496}, {0.608, 
			  0.343763}, {0.609, 0.347033}, {0.61, 0.350305}, {0.611, 
			  0.35358}, {0.612, 0.356857}, {0.613, 0.360136}, {0.614, 
			  0.363419}, {0.615, 0.366704}, {0.616, 0.369991}, {0.617, 
			  0.373282}, {0.618, 0.376575}, {0.619, 0.37987}, {0.62, 
			  0.383169}, {0.621, 0.38647}, {0.622, 0.389775}, {0.623, 
			  0.393082}, {0.624, 0.396392}, {0.625, 0.399704}, {0.626, 
			  0.40302}, {0.627, 0.406339}, {0.628, 0.409661}, {0.629, 
			  0.412986}, {0.63, 0.416314}, {0.631, 0.419645}, {0.632, 
			  0.422979}, {0.633, 0.426317}, {0.634, 0.429657}, {0.635, 
			  0.433001}, {0.636, 0.436349}, {0.637, 0.439699}, {0.638, 
			  0.443053}, {0.639, 0.44641}, {0.64, 0.449771}, {0.641, 
			  0.453135}, {0.642, 0.456502}, {0.643, 0.459873}, {0.644, 
			  0.463248}, {0.645, 0.466626}, {0.646, 0.470007}, {0.647, 
			  0.473393}, {0.648, 0.476782}, {0.649, 0.480174}, {0.65, 
			  0.483571}, {0.651, 0.486971}, {0.652, 0.490375}, {0.653, 
			  0.493783}, {0.654, 0.497195}, {0.655, 0.500611}, {0.656, 
			  0.50403}, {0.657, 0.507454}, {0.658, 0.510882}, {0.659, 
			  0.514314}, {0.66, 0.51775}, {0.661, 0.52119}, {0.662, 
			  0.524634}, {0.663, 0.528083}, {0.664, 0.531536}, {0.665, 
			  0.534993}, {0.666, 0.538454}, {0.667, 0.54192}, {0.668, 
			  0.54539}, {0.669, 0.548865}, {0.67, 0.552344}, {0.671, 
			  0.555828}, {0.672, 0.559317}, {0.673, 0.56281}, {0.674, 
			  0.566307}, {0.675, 0.56981}, {0.676, 0.573317}, {0.677, 
			  0.576829}, {0.678, 0.580346}, {0.679, 0.583868}, {0.68, 
			  0.587395}, {0.681, 0.590927}, {0.682, 0.594463}, {0.683, 
			  0.598005}, {0.684, 0.601552}, {0.685, 0.605105}, {0.686, 
			  0.608662}, {0.687, 0.612225}, {0.688, 0.615793}, {0.689, 
			  0.619366}, {0.69, 0.622945}, {0.691, 0.626529}, {0.692, 
			  0.630119}, {0.693, 0.633715}, {0.694, 0.637316}, {0.695, 
			  0.640922}, {0.696, 0.644535}, {0.697, 0.648153}, {0.698, 
			  0.651777}, {0.699, 0.655407}, {0.7, 0.659043}, {0.701, 
			  0.662685}, {0.702, 0.666332}, {0.703, 0.669986}, {0.704, 
			  0.673647}, {0.705, 0.677313}, {0.706, 0.680986}, {0.707, 
			  0.684665}, {0.708, 0.68835}, {0.709, 0.692042}, {0.71, 
			  0.69574}, {0.711, 0.699445}, {0.712, 0.703157}, {0.713, 
			  0.706875}, {0.714, 0.7106}, {0.715, 0.714332}, {0.716, 
			  0.71807}, {0.717, 0.721816}, {0.718, 0.725569}, {0.719, 
			  0.729328}, {0.72, 0.733095}, {0.721, 0.736869}, {0.722, 
			  0.740651}, {0.723, 0.744439}, {0.724, 0.748235}, {0.725, 
			  0.752039}, {0.726, 0.75585}, {0.727, 0.759669}, {0.728, 
			  0.763495}, {0.729, 0.76733}, {0.73, 0.771172}, {0.731, 
			  0.775022}, {0.732, 0.77888}, {0.733, 0.782746}, {0.734, 
			  0.78662}, {0.735, 0.790503}, {0.736, 0.794394}, {0.737, 
			  0.798293}, {0.738, 0.802201}, {0.739, 0.806117}, {0.74, 
			  0.810042}, {0.741, 0.813975}, {0.742, 0.817918}, {0.743, 
			  0.821869}, {0.744, 0.825829}, {0.745, 0.829799}, {0.746, 
			  0.833777}, {0.747, 0.837765}, {0.748, 0.841762}, {0.749, 
			  0.845769}, {0.75, 0.849785}, {0.751, 0.853811}, {0.752, 
			  0.857846}, {0.753, 0.861892}, {0.754, 0.865947}, {0.755, 
			  0.870012}, {0.756, 0.874088}, {0.757, 0.878173}, {0.758, 
			  0.882269}, {0.759, 0.886376}, {0.76, 0.890493}, {0.761, 
			  0.89462}, {0.762, 0.898759}, {0.763, 0.902908}, {0.764, 
			  0.907069}, {0.765, 0.91124}, {0.766, 0.915423}, {0.767, 
			  0.919617}, {0.768, 0.923822}, {0.769, 0.928039}, {0.77, 
			  0.932268}, {0.771, 0.936509}, {0.772, 0.940762}, {0.773, 
			  0.945026}, {0.774, 0.949304}, {0.775, 0.953593}, {0.776, 
			  0.957895}, {0.777, 0.962209}, {0.778, 0.966537}, {0.779, 
			  0.970877}, {0.78, 0.97523}, {0.781, 0.979597}, {0.782, 
			  0.983977}, {0.783, 0.98837}, {0.784, 0.992778}, {0.785, 
			  0.997199}, {0.786, 1.00163}, {0.787, 1.00608}, {0.788, 
			  1.01055}, {0.789, 1.01502}, {0.79, 1.01952}, {0.791, 
			  1.02402}, {0.792, 1.02855}, {0.793, 1.03308}, {0.794, 
			  1.03764}, {0.795, 1.04221}, {0.796, 1.04679}, {0.797, 
			  1.05139}, {0.798, 1.05601}, {0.799, 1.06064}, {0.8, 
			  1.06529}, {0.801, 1.06995}, {0.802, 1.07464}, {0.803, 
			  1.07934}, {0.804, 1.08405}, {0.805, 1.08879}, {0.806, 
			  1.09354}, {0.807, 1.09831}, {0.808, 1.1031}, {0.809, 
			  1.10791}, {0.81, 1.11273}, {0.811, 1.11758}, {0.812, 
			  1.12244}, {0.813, 1.12732}, {0.814, 1.13223}, {0.815, 
			  1.13715}, {0.816, 1.1421}, {0.817, 1.14706}, {0.818, 
			  1.15204}, {0.819, 1.15705}, {0.82, 1.16208}, {0.821, 
			  1.16713}, {0.822, 1.1722}, {0.823, 1.17729}, {0.824, 
			  1.18241}, {0.825, 1.18755}, {0.826, 1.19271}, {0.827, 
			  1.1979}, {0.828, 1.20311}, {0.829, 1.20834}, {0.83, 1.2136}, {0.831,
			   1.21888}, {0.832, 1.22419}, {0.833, 1.22953}, {0.834, 
			  1.23489}, {0.835, 1.24028}, {0.836, 1.24569}, {0.837, 
			  1.25114}, {0.838, 1.25661}, {0.839, 1.2621}, {0.84, 
			  1.26763}, {0.841, 1.27319}, {0.842, 1.27878}, {0.843, 
			  1.28439}, {0.844, 1.29004}, {0.845, 1.29572}, {0.846, 
			  1.30143}, {0.847, 1.30717}, {0.848, 1.31295}, {0.849, 
			  1.31876}, {0.85, 1.3246}, {0.851, 1.33048}, {0.852, 
			  1.33639}, {0.853, 1.34234}, {0.854, 1.34832}, {0.855, 
			  1.35435}, {0.856, 1.36041}, {0.857, 1.3665}, {0.858, 
			  1.37264}, {0.859, 1.37882}, {0.86, 1.38504}, {0.861, 
			  1.3913}, {0.862, 1.3976}, {0.863, 1.40395}, {0.864, 
			  1.41034}, {0.865, 1.41677}, {0.866, 1.42326}, {0.867, 
			  1.42978}, {0.868, 1.43636}, {0.869, 1.44298}, {0.87, 
			  1.44966}, {0.871, 1.45638}, {0.872, 1.46316}, {0.873, 
			  1.46999}, {0.874, 1.47688}, {0.875, 1.48382}, {0.876, 
			  1.49082}, {0.877, 1.49787}, {0.878, 1.50499}, {0.879, 
			  1.51217}, {0.88, 1.51941}, {0.881, 1.52671}, {0.882, 
			  1.53408}, {0.883, 1.54152}, {0.884, 1.54903}, {0.885, 
			  1.5566}, {0.886, 1.56425}, {0.887, 1.57198}, {0.888, 
			  1.57978}, {0.889, 1.58766}, {0.89, 1.59562}, {0.891, 
			  1.60367}, {0.892, 1.6118}, {0.893, 1.62002}, {0.894, 
			  1.62833}, {0.895, 1.63674}, {0.896, 1.64524}, {0.897, 
			  1.65384}, {0.898, 1.66254}, {0.899, 1.67136}, {0.9, 
			  1.68028}, {0.901, 1.68931}, {0.902, 1.69846}, {0.903, 
			  1.70774}, {0.904, 1.71714}, {0.905, 1.72667}, {0.906, 
			  1.73633}, {0.907, 1.74614}, {0.908, 1.75609}, {0.909, 
			  1.7662}, {0.91, 1.77646}, {0.911, 1.78689}, {0.912, 1.7975}, {0.913,
			   1.80828}, {0.914, 1.81925}, {0.915, 1.83041}, {0.916, 
			  1.84179}, {0.917, 1.85338}, {0.918, 1.86519}, {0.919, 
			  1.87725}, {0.92, 1.88956}, {0.921, 1.90213}, {0.922, 
			  1.91499}, {0.923, 1.92814}, {0.924, 1.94161}, {0.925, 
			  1.95541}, {0.926, 1.96958}, {0.927, 1.98412}, {0.928, 
			  1.99908}, {0.929, 2.01448}, {0.93, 2.03035}, {0.931, 
			  2.04673}, {0.932, 2.06368}, {0.933, 2.08123}, {0.934, 
			  2.09945}, {0.935, 2.1184}, {0.936, 2.13816}, {0.937, 
			  2.15882}, {0.938, 2.1805}, {0.939, 2.20332}, {0.94, 
			  2.22745}, {0.941, 2.25309}, {0.942, 2.28049}, {0.943, 2.31}, {0.944,
			   2.34206}, {0.945, 2.37728}, {0.946, 2.41656}, {0.947, 
			  2.46129}, {0.948, 2.51376}, {0.949, 2.57839}, {0.95, 
			  2.66574}, {0.951, 2.81991}, {0.952, 3.54973}, {0.953, 
			  3.73131}, {0.954, 3.85437}, {0.955, 3.95473}, {0.956, 
			  4.04268}, {0.957, 4.12288}, {0.958, 4.19792}, {0.959, 
			  4.26943}, {0.96, 4.33855}, {0.961, 4.40615}, {0.962, 
			  4.47295}, {0.963, 4.53956}, {0.964, 4.60657}, {0.965, 
			  4.6746}, {0.966, 4.7443}, {0.967, 4.81645}, {0.968, 
			  4.89202}, {0.969, 4.97228}, {0.97, 5.05908}, {0.971, 
			  5.15528}, {0.972, 5.26583}, {0.973, 5.40089}, {0.974, 
			  5.58911}, {0.975, 6.51235}, {0.976, 7.08855}, {0.977, 
			  7.3432}, {0.978, 7.55055}, {0.979, 7.74296}, {0.98, 
			  7.93667}, {0.981, 8.14789}, {0.982, 8.40776}, {0.983, 
			  8.87415}, {0.984, 10.4016}, {0.985, 10.8192}, {0.986, 
			  11.2048}, {0.987, 11.733}, {0.988, 13.6705}, {0.989, 
			  14.3209}, {0.99, 16.3796}, {0.991, 17.4971}, {0.992, 20.16}, {0.993,
			   23.1256}, {0.994, 26.6235}, {0.995, 32.3138}, {0.996, 
			  39.5406}, {0.997, 52.5532}, {0.998, 79.8397}, {0.999, 158.91}, {1., 
			  2.88012e15}};
	
	/**
	 * @param P
	 * @return	x(P), the inverse cumulative probability function
	 */
	public static double calculateX(double P)
	{
		double x = 0;
		try {
			x = InterpolatedFunction1D.calculateY(P, PxTABLE);
		} catch (InconsistencyException e) {
			// this should not happen!
			e.printStackTrace();
			System.exit(-1);
		}
		return x;
	}
	
	/**
	 * @param P
	 * @return
	 */
	public static double calculateSinTheta(double lambda, double w, double P)
	{
		// first calculate x
		double x = calculateX(P);
		
		// then use the equation x = w pi / lambda sin(theta) to calculate sin(theta) from x:
		// sin(theta) = (x lambda) / (w pi)
		return (x*lambda) / (w*Math.PI);
	}
	
	public static double getRandomSinTheta(double lambda, double w)
	{
		return calculateSinTheta(lambda, w, Math.random());
	}
	
	public static Vector3D getTangentialDirectionComponentChange(
			double lambda,
			double pixelSideLengthU,
			double pixelSideLengthV,
			Vector3D uHat,
			Vector3D vHat
		)
	{
		return Vector3D.sum(
				uHat.getProductWith(getRandomSinTheta(lambda, pixelSideLengthU)),
				vHat.getProductWith(getRandomSinTheta(lambda, pixelSideLengthV))
				// The first diffraction minimum corresponds to a direction in which light rays that
				// have passed through the slit a transverse distance w/2 apart receive phase shifts
				// that differ by pi.  Such phase shifts are precisely achieved with a phase hologram
				// that corresponds to a transverse phase gradient of pi / (w / 2) = 2 pi / w.
				// So the direction of the first diffraction minimum can be calculated by simulating
				// transmission through a phase hologram with this phase gradient.
				// In the PhaseHologram class, phase gradients are given in units of (2 pi/lambda),
				// and so a phase gradient of 2 pi / w becomes (2 pi / w) / (2 pi / lambda) = lambda / w.
				// Here, a uniformly distributed random phase-gradient in the range +/- lambda/w is added
				// in each transverse dimension.
				// (2.*(Math.random()-0.5) gives a uniformly distributes random number in the range -1 to 1.)
//				surfaceCoordinate1Axis.getProductWith(lambda/pixelSideLength*2.*(Math.random()-0.5)),
//				surfaceCoordinate2Axis.getProductWith(lambda/pixelSideLength*2.*(Math.random()-0.5))
			);
	
	}
	
	//Same method as above but now with the vHat and vlength being large i.e akin to a single slit.
	public static Vector3D getTangentialDirectionComponentChange(
			double lambda,
			double pixelSideLengthU,
			Vector3D uHat
		)
	{
		return 	uHat.getProductWith(getRandomSinTheta(lambda, pixelSideLengthU)
				// The first diffraction minimum corresponds to a direction in which light rays that
				// have passed through the slit a transverse distance w/2 apart receive phase shifts
				// that differ by pi.  Such phase shifts are precisely achieved with a phase hologram
				// that corresponds to a transverse phase gradient of pi / (w / 2) = 2 pi / w.
				// So the direction of the first diffraction minimum can be calculated by simulating
				// transmission through a phase hologram with this phase gradient.
				// In the PhaseHologram class, phase gradients are given in units of (2 pi/lambda),
				// and so a phase gradient of 2 pi / w becomes (2 pi / w) / (2 pi / lambda) = lambda / w.
				// Here, a uniformly distributed random phase-gradient in the range +/- lambda/w is added
				// in each transverse dimension.
				// (2.*(Math.random()-0.5) gives a uniformly distributes random number in the range -1 to 1.)
//				surfaceCoordinate1Axis.getProductWith(lambda/pixelSideLength*2.*(Math.random()-0.5)),
//				surfaceCoordinate2Axis.getProductWith(lambda/pixelSideLength*2.*(Math.random()-0.5))
			);
	
	}
	
	/**
	 * @param lightRayDirectionBeforeDiffraction
	 * @param lambda
	 * @param pixelSideLengthU
	 * @param pixelSideLengthV
	 * @param uHat	unit direction of two of the sides of the rectangular pixels
	 * @param vHat	unit direction of the other two sides of the rectangular pixels
	 * @param normalisedApertureNormal	normalised vector normal to the plane of the rectangular aperture
	 * @return	the diffracted light-ray direction, i.e. the light-ray direction before diffraction with a randomised tangential component, given by the sin^2 single-slit diffraction function in each direction, added to it
	 * @throws EvanescentException	if the light ray becomes evanescent after addition of the tangential component
	 */
	public static Vector3D getDiffractedLightRayDirection(
			Vector3D lightRayDirectionBeforeDiffraction,
			double lambda,
			double pixelSideLengthU,
			double pixelSideLengthV,
			Vector3D uHat,
			Vector3D vHat,
			Vector3D normalisedApertureNormal
		)
	throws EvanescentException
	{
			// simulate diffractive blur
			
			return PhaseHologram.getOutgoingNormalisedRayDirection(
						lightRayDirectionBeforeDiffraction.getNormalised(),	// incidentNormalisedRayDirection
						getTangentialDirectionComponentChange(
								lambda,
								pixelSideLengthU,
								pixelSideLengthV,
								uHat,
								vHat
							),	// tangentialDirectionComponentChange
						normalisedApertureNormal,	// normalisedOutwardsSurfaceNormal
						false	// isReflective
					);
	}
	
	/**
	 * @param lightRayDirectionBeforeDiffraction
	 * @param lambda
	 * @param pixelSideLengthU
	 * @param uHat	unit direction of two of the sides of the rectangular pixels
	 * @param normalisedApertureNormal	normalised vector normal to the plane of the rectangular aperture
	 * @return	the diffracted light-ray direction, i.e. the light-ray direction before diffraction with a randomised tangential component, given by the sin^2 single-slit diffraction function, added to it
	 * @throws EvanescentException	if the light ray becomes evanescent after addition of the tangential component
	 */
	public static Vector3D getDiffractedLightRayDirection(
			Vector3D lightRayDirectionBeforeDiffraction,
			double lambda,
			double pixelSideLengthU,
			Vector3D uHat,
			Vector3D normalisedApertureNormal
		)
	throws EvanescentException
	{
			// simulate diffractive blur
			
			return PhaseHologram.getOutgoingNormalisedRayDirection(
						lightRayDirectionBeforeDiffraction.getNormalised(),	// incidentNormalisedRayDirection
						getTangentialDirectionComponentChange(
								lambda,
								pixelSideLengthU,
								uHat
							),	// tangentialDirectionComponentChange
						normalisedApertureNormal,	// normalisedOutwardsSurfaceNormal
						false	// isReflective
					);
	}
}
