package math;

 /**
  * 
  * @author Johannes
  *
  * A few basic data related to Platonic solids, centred on the origin, of circumradius (i.e. distance of the vertices from the centre) 1
  */

public enum PlatonicSolidType
{	
	// data calculated using Mathematica, using e.g. "PolyhedronData["Tetrahedron", "Edges"] // N"
	TETRAHEDRON(
			"Tetrahedron",
			new Vector3D[] {
					new Vector3D(0., 0., 1.), new Vector3D(-0.471405, -0.816497, -0.333333),
					new Vector3D(-0.471405, 0.816497, -0.333333), new Vector3D(0.942809, 0., -0.333333)
				},
			// new Vector3D[] {new Vector3D(0., 0., 0.612372), new Vector3D(-0.288675, -0.5, -0.204124), new Vector3D(-0.288675, 0.5, -0.204124), new Vector3D(0.57735, 0., -0.204124)},
			new int[][] {{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3}},
			new int[][] {{1, 2, 3}, {2, 1, 0}, {3, 0, 1}, {0, 3, 2}}
		),
	CUBE(
			"Cube",
			new Vector3D[] {
					new Vector3D(-0.57735, -0.57735, -0.57735), new Vector3D(-0.57735, -0.57735, 0.57735),
					new Vector3D(-0.57735, 0.57735, -0.57735), new Vector3D(-0.57735, 0.57735, 0.57735),
					new Vector3D(0.57735, -0.57735, -0.57735), new Vector3D(0.57735, -0.57735, 0.57735),
					new Vector3D(0.57735, 0.57735, -0.57735), new Vector3D(0.57735, 0.57735, 0.57735)
				},
			// new Vector3D[] {new Vector3D(-0.5, -0.5, -0.5), new Vector3D(-0.5, -0.5, 0.5), new Vector3D(-0.5, 0.5, -0.5), new Vector3D(-0.5, 0.5, 0.5), new Vector3D(0.5, -0.5, -0.5), new Vector3D(0.5, -0.5, 0.5), new Vector3D(0.5, 0.5, -0.5), new Vector3D(0.5, 0.5, 0.5)},
			new int[][] {{0, 1}, {0, 2}, {0, 4}, {1, 3}, {1, 5}, {2, 3}, {2, 6}, {3, 7}, {4, 5}, {4, 6}, {5, 7}, {6, 7}},
			new int[][] {{7, 3, 1, 5}, {7, 5, 4, 6}, {7, 6, 2, 3}, {3, 2, 0, 1}, {0, 2, 6, 4}, {1, 0, 4, 5}}
		),
	OCTAHEDRON(
			"Octahedron",
			new Vector3D[] {
					new Vector3D(-1., 0., 0.), new Vector3D(0., 1., 0.),
					new Vector3D(0., 0., -1.), new Vector3D(0., 0., 1.),
					new Vector3D(0., -1., 0.), new Vector3D(1., 0., 0.)
				},
			// new Vector3D[] {new Vector3D(-0.707107, 0., 0.), new Vector3D(0., 0.707107, 0.), new Vector3D(0., 0., -0.707107), new Vector3D(0., 0., 0.707107), new Vector3D(0., -0.707107, 0.), new Vector3D(0.707107, 0., 0.)},
			new int[][] {{0, 1}, {0, 2}, {0, 3}, {0, 4}, {1, 2}, {1, 3}, {1, 5}, {2, 4}, {2, 5}, {3, 4}, {3, 5}, {4, 5}},
			new int[][] {{3, 4, 5}, {3, 5, 1}, {3, 1, 0}, {3, 0, 4}, {4, 0, 2}, {4, 2, 5}, {2, 0, 1}, {5, 2, 1}}
		),
	DODECAHEDRON(
			"Dodecahedron",
			new Vector3D[] {
					new Vector3D(-0.982247, 0., 0.187592), new Vector3D(0.982247, 0., -0.187592),
					new Vector3D(-0.303531, -0.934172, 0.187592), new Vector3D(-0.303531, 0.934172, 0.187592),
					new Vector3D(0.794654, -0.57735, 0.187592), new Vector3D(0.794654, 0.57735, 0.187592),
					new Vector3D(-0.187592, -0.57735, 0.794654), new Vector3D(-0.187592, 0.57735, 0.794654),
					new Vector3D(-0.491123, -0.356822, -0.794654), new Vector3D(-0.491123, 0.356822, -0.794654),
					new Vector3D(0.491123, -0.356822, 0.794654), new Vector3D(0.491123, 0.356822, 0.794654),
					new Vector3D(0.607062, 0., -0.794654), new Vector3D(-0.794654, -0.57735, -0.187592),
					new Vector3D(-0.794654, 0.57735, -0.187592), new Vector3D(-0.607062, 0., 0.794654),
					new Vector3D(0.187592, -0.57735, -0.794654), new Vector3D(0.187592, 0.57735, -0.794654),
					new Vector3D(0.303531, -0.934172, -0.187592), new Vector3D(0.303531, 0.934172, -0.187592)
				},
			// new Vector3D[] {new Vector3D(-1.37638, 0., 0.262866), new Vector3D(1.37638, 0., -0.262866), new Vector3D(-0.425325, -1.30902, 0.262866), new Vector3D(-0.425325, 1.30902, 0.262866), new Vector3D(1.11352, -0.809017, 0.262866), new Vector3D(1.11352, 0.809017, 0.262866), new Vector3D(-0.262866, -0.809017, 1.11352), new Vector3D(-0.262866, 0.809017, 1.11352), new Vector3D(-0.688191, -0.5, -1.11352), new Vector3D(-0.688191, 0.5, -1.11352), new Vector3D(0.688191, -0.5, 1.11352), new Vector3D(0.688191, 0.5, 1.11352), new Vector3D(0.850651, 0., -1.11352), new Vector3D(-1.11352, -0.809017, -0.262866), new Vector3D(-1.11352, 0.809017, -0.262866), new Vector3D(-0.850651, 0., 1.11352), new Vector3D(0.262866, -0.809017, -1.11352), new Vector3D(0.262866, 0.809017, -1.11352), new Vector3D(0.425325, -1.30902, -0.262866), new Vector3D(0.425325, 1.30902, -0.262866)},
			new int[][] {{0, 13}, {0, 14}, {0, 15}, {1, 4}, {1, 5}, {1, 12}, {2, 6}, {2, 13}, {2, 18}, {3, 7}, {3, 14}, {3, 19}, {4, 10}, {4, 18}, {5, 11}, {5, 19}, {6, 10}, {6, 15}, {7, 11}, {7, 15}, {8, 9}, {8, 13}, {8, 16}, {9, 14}, {9, 17}, {10, 11}, {12, 16}, {12, 17}, {16, 18}, {17, 19}},
			new int[][] {{14, 9, 8, 13, 0}, {1, 5, 11, 10, 4}, {4, 10, 6, 2, 18}, {10, 11, 7, 15, 6}, {11, 5, 19, 3, 7}, {5, 1, 12, 17, 19}, {1, 4, 18, 16, 12}, {3, 19, 17, 9, 14}, {17, 12, 16, 8, 9}, {16, 18, 2, 13, 8}, {2, 6, 15, 0, 13}, {15, 7, 3, 14, 0}}
		),
	ICOSAHEDRON(
			"Icosahedron",
			new Vector3D[] {
					new Vector3D(0., 0., -1.), new Vector3D(0., 0., 1.),
					new Vector3D(-0.894427, 0., -0.447214), new Vector3D(0.894427, 0., 0.447214),
					new Vector3D(0.723607, -0.525731, -0.447214), new Vector3D(0.723607, 0.525731, -0.447214),
					new Vector3D(-0.723607, -0.525731, 0.447214), new Vector3D(-0.723607, 0.525731, 0.447214),
					new Vector3D(-0.276393, -0.850651, -0.447214), new Vector3D(-0.276393, 0.850651, -0.447214),
					new Vector3D(0.276393, -0.850651, 0.447214), new Vector3D(0.276393, 0.850651, 0.447214)
				},
			// new Vector3D[] {new Vector3D(0., 0., -0.951057), new Vector3D(0., 0., 0.951057), new Vector3D(-0.850651, 0., -0.425325), new Vector3D(0.850651, 0., 0.425325), new Vector3D(0.688191, -0.5, -0.425325), new Vector3D(0.688191, 0.5, -0.425325), new Vector3D(-0.688191, -0.5, 0.425325), new Vector3D(-0.688191, 0.5, 0.425325), new Vector3D(-0.262866, -0.809017, -0.425325), new Vector3D(-0.262866, 0.809017, -0.425325), new Vector3D(0.262866, -0.809017, 0.425325), new Vector3D(0.262866, 0.809017, 0.425325)},
			new int[][] {{0, 2}, {0, 4}, {0, 5}, {0, 8}, {0, 9}, {1, 3}, {1, 6}, {1, 7}, {1, 10}, {1, 11}, {2, 6}, {2, 7}, {2, 8}, {2, 9}, {3, 4}, {3, 5}, {3, 10}, {3, 11}, {4, 5}, {4, 8}, {4, 10}, {5, 9}, {5, 11}, {6, 7}, {6, 8}, {6, 10}, {7, 9}, {7, 11}, {8, 10}, {9, 11}},
			new int[][] {{1, 11, 7}, {1, 7, 6}, {1, 6, 10}, {1, 10, 3}, {1, 3, 11}, {4, 8, 0}, {5, 4, 0}, {9, 5, 0}, {2, 9, 0}, {8, 2, 0}, {11, 9, 7}, {7, 2, 6}, {6, 8, 10}, {10, 4, 3}, {3, 5, 11}, {4, 10, 8}, {5, 3, 4}, {9, 11, 5}, {2, 7, 9}, {8, 6, 2}}
		);
		
	private String name;
	private Vector3D[] vertices;
	private int[][] edges;
	private int[][] faces;
	
	private PlatonicSolidType(String name, Vector3D[] vertices, int[][] edges, int[][] faces)
	{
		this.name = name;
		this.vertices = vertices;
		this.edges = edges;
		this.faces = faces;
	}
	
	public Vector3D[] getVertices()
	{
		return vertices;
	}
	
	public Vector3D getVertex(int vertexIndex)
	{
		return vertices[vertexIndex];
	}
		
	public int getNumberOfVertices()
	{
		return vertices.length;
	}
	
	public int[][] getEdges()
	{
		return edges;
	}
	
	public int getNumberOfEdges()
	{
		return edges.length;
	}
	
	public int[][] getFaces()
	{
		return faces;
	}
	
	public int getNumberOfFaces()
	{
		return faces.length;
	}
	
	public int getNumberOfVerticesPerFace()
	{
		return faces[0].length;
	}
	
	public Vector3D getFaceCentre(int faceIndex)
	{
		// add all the vertices of this face together...
		Vector3D sum = new Vector3D(0, 0, 0);
		for(int i=0; i<getNumberOfVerticesPerFace(); i++)
		{
			sum = Vector3D.sum(sum, vertices[faces[faceIndex][i]]);
		}
		
		// ... and divide by the number of vertices that were summed and return
		return sum.getProductWith(1./getNumberOfVerticesPerFace());
	}
	
	/**
	 * @return the inradius of the Platonic solid, i.e. the distance from its centre to the centre of the faces
	 */
	public double getInradius()
	{
		// calculate the distance from the centre to the centre of the 0th face, and return
		return getFaceCentre(0).getLength();
	}

	public Vector3D getOutwardFaceNormal(int faceIndex)
	{
		return getFaceCentre(faceIndex).getNormalised();
		
//		// add all the vertices of this face together...
//		Vector3D sum = new Vector3D(0, 0, 0);
//		for(int i=0; i<getNumberOfVerticesPerFace(); i++)
//		{
//			sum = Vector3D.sum(sum, vertices[faces[faceIndex][i]]);
//		}
//		
//		// ... and normalise and return
//		return sum.getNormalised();
	}
	
	public double getFaceCircumradius()
	{
		// calculate the distance between the centre of the 0th face and the 0th vertex of the 0th face, and return
		return Vector3D.getDistance(getFaceCentre(0), vertices[faces[0][0]]);
	}
	
	@Override
	public String toString() {return name;}
}
