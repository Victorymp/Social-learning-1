package objects;

import animat.Animat;

import map.Map;

import java.util.*;
/**
 * This class represents a collection of objects in a grid.
 * It manages the objects' locations and their interactions with each other.
 * It also manages the activation map of neurons associated with the objects.
 *
 * The grid is represented as a 2D array, and each cell in the grid can contain an object.
 * The objects can be of different types (Grass, Water, Stone, etc.).
 *
 * The class also provides methods for adding and removing objects from the grid,
 * checking if an object is in the grid, and getting the neighborhood of an object.
 *
 * The activation map is a list of neurons, each associated with an object in the grid.
 * The class provides methods for activating neurons, setting their values, weights, and biases,
 * and getting their receptive fields.
 */

public class ObjectCollection {

	private static final int GRID_SIZE = 20;

	private final Object[][] objectLocation;
	private final Neuron[][] configurationSpace;

	private int currentMap;
	private Map map;

	public ObjectCollection() {
		objectLocation = new Object[GRID_SIZE + 1][GRID_SIZE + 1];
		configurationSpace = new Neuron[GRID_SIZE + 1][GRID_SIZE + 1];
		currentMap = 1;
		this.map = new Map(20, currentMap);
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public void setMap(int map_no) {
		this.currentMap = map_no;
		this.map = new Map(20, map_no);
	}

	public void setMap(ArrayList<Object> objects, int map_no) {
		this.currentMap = map_no;
		for (Object object : objects) {
			objectLocation[object.getX()][object.getY()] = object;
			Neuron neuron = new Neuron(0, 0, object.getX(), object.getY());
			neuron.setObject(object);
			configurationSpace[object.getX()][object.getY()] = neuron;
		}
	}

	public void removeObject(Object ob) {

		objectLocation[ob.getX()][ob.getY()] = null;
	}

	/**
	 * Adds an object to the object collection
	 *
	 * @param ob
	 */
	public void addObject(Object ob) {
		if (isOutOfBounds(ob.getX(), ob.getY()) || objectLocation[ob.getX()][ob.getY()] != null || configurationSpace[ob.getX()][ob.getY()] != null) {
			return;
		}
		if (currentMap == 1) {
			ob = map.map1(ob);
		} else if (currentMap == 2) {
			ob = map.map2(ob);
		} else if (currentMap == 3) {
			ob = map.map3(ob);
		}
		Neuron neuron = new Neuron(0, 0, ob.getX(), ob.getY());
		neuron.setObject(ob);
		objectLocation[ob.getX()][ob.getY()] = ob;
		// The configuration space is a 2D array of neurons associated with the objects which represent the Cartesian grid
		configurationSpace[ob.getX()][ob.getY()] = neuron;

	}

	private boolean isOutOfBounds(int x, int y) {
		return x < 0 || x > GRID_SIZE || y < 0 || y > GRID_SIZE;
	}

	public Object inList(int x, int y) {
		if (objectLocation[x][y] != null) {
			return objectLocation[x][y];
		}
		if (isOutOfBounds(x, y)) {
			return null;
		}
		return createAndAddNewObject(x, y);
	}

	private Object createAndAddNewObject(int x, int y) {
		// If y is not 2, create a grass object, otherwise create a water object
		Object obj = new Grass(x, y);
		addObject(obj);
		return objectLocation[x][y];
	}

	public ArrayList<Object> getNeighborhood(int x, int y) {
		ArrayList<Object> neighborhood = new ArrayList<>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// Check if the neighbor is within the grid
				if (x + i >= 0 && x + i < GRID_SIZE && y + j >= 0 && y + j < GRID_SIZE) {
					// neighborhood is a objects touching the object at x, y
					neighborhood.add(objectLocation[x + i][y + j]);
				}
			}
		}
		return neighborhood;
	}

	/*
	 * Sets the weight of the neuron at x, y to the given value
	 * Ai implemented this method
	 **/
	public void setIota(Class type, double value, int x, int y) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				int x_pos = x + i;
				int y_pos = y + j;
				// Check if the neighbor is within the grid
				if (x_pos >= 0 && x_pos < GRID_SIZE && y_pos >= 0 && y_pos < GRID_SIZE) {
					Object obj = objectLocation[x_pos][y_pos];
					// check if the object is of the same type
					if (obj != null && obj.getClass() == type) {
						obj.setIota(value);
						objectLocation[x_pos][y_pos] = obj;
						configurationSpace[x_pos][y_pos].setObject(obj);
						configurationSpace[x_pos][y_pos].setIota(value);
					}
				}
			}
		}
	}

	public void setNeuronValue(int x, int y, double value) {
		configurationSpace[x][y].setCurrentValue(value);
	}

	public ObjectCollection printAnimatJourney(Animat animat) {
		Stack<Object> stack = animat.getStack();
		ObjectCollection objectCollection = animat.map();
		while (!stack.isEmpty()) {
			Object ob = stack.pop();
			objectCollection.removeObject(ob);
			objectCollection.addObject(new Path(ob.getX(), ob.getY()));
		}
		return objectCollection;
	}

	public Object getClass(Object object) {

		if (object.getClass() == Grass.class) {
			return new Grass(object.getX(), object.getY());
		} else if (object.getClass() == Water.class) {
			return new Water(object.getX(), object.getY());
		} else if (object.getClass() == Stone.class) {
			return new Stone(object.getX(), object.getY());
		} else if (object.getClass() == Path.class) {
			return new Path(object.getX(), object.getY());
		} else if (object.getClass() == Trap.class) {
			return new Trap(object.getX(), object.getY());
		} else if (object.getClass() == Resource.class) {
			return new Resource(object.getX(), object.getY());
		} else {
			return null;
		}
	}

	/*
	 * Generates the receptive field which is locations reachable by the animat
	 */
	public Neuron generateReceptiveField(Neuron neuron) {
		// Get the x and y coordinates of the neuron
		int x = neuron.getX();
		int y = neuron.getY();
		// Loop through the 3x3 grid around the neuron
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// Check if the neighbor is within the grid and not the neuron itself
				if (x + i >= 0 && x + i < GRID_SIZE && y + j >= 0 && y + j < GRID_SIZE && !(i == 0 && j == 0)) {
					// Add the neuron to the receptive field
					neuron.addReceptiveField(getNeuron(x + i, y + j));
				}
				// Check if the receptive field has 8 neurons. Terminates the loop if it does
				if (neuron.getReceptiveField().size() == 8) {
					return neuron;
				}
			}
		}
		return neuron;
	}


	public Neuron getNeuron(int x, int y) {
		return configurationSpace[x][y];
	}


	public void setNeuronBias(int x, int y, double bias) {
		configurationSpace[x][y].setBias(bias);
	}

	public void activateNeuron(int x, int y) {
		configurationSpace[x][y].activate();
	}

	/*
	 * Sets the weight of the neuron at x, y to the given value
	 */
	public void setObjectLocation(Object object) {
		objectLocation[object.getX()][object.getY()] = object;
		configurationSpace[object.getX()][object.getY()].setObject(object);
	}
}