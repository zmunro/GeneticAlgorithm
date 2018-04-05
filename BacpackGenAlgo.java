/* package whatever; // don't place package name! */

import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

class Box {
    public int weight;
    public int value;

    public Box(int startWeight, int startValue) {
        weight = startWeight;
        value = startValue;
    }
    public Box(Box another) {
    	this.weight = another.weight;
    	this.value = another.value;
    }
}

class Backpack {
	// order boxes will be put into bag
	public ArrayList<Box> box_env;

	// boxes put into the bag
    public ArrayList<Box> boxes;

    public int total_value;
    public int total_weight;

	private final int MAX_WEIGHT = 120;
    private final int NUM_BOXES = 7;

    public int getNumBoxes() {
    	return NUM_BOXES;
    }

    // constructor
   	public Backpack(ArrayList<Box> initBoxes) {
    	this.box_env = new ArrayList<Box>(NUM_BOXES);
    	for(int i = 0; i < NUM_BOXES; i++) {
    		 this.box_env.add(initBoxes.get(i));
    	}
    	this.fillBag();
    }

    public Backpack() {
    	this.box_env = new ArrayList<Box>();
    }

    // copy constructor
    public Backpack(Backpack another) {
    	this.box_env = new ArrayList<Box>(another.box_env);
   		this.fillBag();
    }

    // stores the boxes in bag in order they appear in environment
    public void fillBag() {
    	this.boxes = new ArrayList<Box>();
    	int weight_sum = 0;
    	int value_sum = 0;
    	for (int i = 0; i < this.box_env.size(); i++) {
    		if (weight_sum < MAX_WEIGHT && (weight_sum + this.box_env.get(i).weight <= MAX_WEIGHT)) {
    			value_sum +=box_env.get(i).value;
    			weight_sum += box_env.get(i).weight;
    			this.boxes.add(box_env.get(i));
    		}
    		else {
    			this.total_weight = weight_sum;
    			this.total_value = value_sum;
    			break;
    		}
    	}
    }

    // Implementing Fisher–Yates shuffle
    public void shuffleBoxes() {
        Collections.shuffle(box_env);
        this.fillBag();
    }

    public void printBoxes() {
    	System.out.print("Environment: ");
		for(int j = 0; j < this.box_env.size(); j++) {
			if(j + 1 < this.box_env.size()) {
				System.out.print("("+this.box_env.get(j).weight +","+this.box_env.get(j).value + ") - ");    			
			}
			else {
				System.out.print("("+this.box_env.get(j).weight +","+this.box_env.get(j).value + ")");	
			}
		}
		System.out.println();
		System.out.print("Backpack: ");
		for(int j = 0; j < this.boxes.size(); j++) {
			if(j + 1 < this.boxes.size()) {
				System.out.print("("+this.boxes.get(j).weight +","+this.boxes.get(j).value + ") - ");
			}
			else {
				System.out.print("("+this.boxes.get(j).weight +","+this.boxes.get(j).value + ")");
			}
		}
		System.out.println();
		System.out.println("Value: "+ this.total_value+"   Weight: "+this.total_weight);
		System.out.println();
    }
}

class GeneticAlgoEnvironment {
	public ArrayList<Backpack> population;
	public int bestIndex;
	public static final int POPULATION_SIZE = 4;

	private static final Box BOX_ONE = new Box(20, 6); 
	private static final Box BOX_TWO = new Box(30, 5);
	private static final Box BOX_THREE = new Box(60, 8);
	private static final Box BOX_FOUR = new Box(90, 7);
	private static final Box BOX_FIVE = new Box(50, 6);
	private static final Box BOX_SIX = new Box(70, 9);
	private static final Box BOX_SEVEN = new Box(30, 4);
	private ArrayList<Box> initPack = new ArrayList<Box>(Arrays.asList(
        		BOX_ONE, BOX_TWO, BOX_THREE, BOX_FOUR, 
        		BOX_FIVE, BOX_SIX, BOX_SEVEN));

	public GeneticAlgoEnvironment() {
		this.population = new ArrayList<Backpack>(POPULATION_SIZE);
		
		// creates POPULATION_SIZE random configurations of backpacks
		for (int i = 0; i < POPULATION_SIZE; i++) {
        	Backpack pack = new Backpack(initPack);
        	pack.shuffleBoxes();
        	this.population.add(pack);
        }
        this.bestIndex = this.indexOfBestSolution();
	}

	// returns index of backpack with highest value in the population
	public int indexOfBestSolution() {
		int max_value = 0;
		int index = 0;
		for(int i = 0; i < this.population.size(); i++) {
			if (this.population.get(i).total_value >= max_value) {
				max_value = this.population.get(i).total_value;
				index = i;
			}
		}
		return index;
	}

	public int indexOfWorstSolution() {
		int min_value = 0;
		int index = 0;
		for(int i = 0; i < this.population.size(); i++) {
			if (this.population.get(i).total_value <= min_value) {
				min_value = this.population.get(i).total_value;
				index = i;
			}
		}
		return index;
	}

	public void printPopulation() {
		for(int i = 0; i < POPULATION_SIZE; i++) {
        	System.out.println("Pack "+(i+1));
        	this.population.get(i).printBoxes();
        }
	}

	public void printBestSolution() {
		System.out.println("Best Solution:");
		int index = this.indexOfBestSolution();
        this.population.get(index).printBoxes();
	}

	public void cull() {
		int culled = this.population.size() / 2;
		for (int i = 0; i < culled; i++) {
			int index = indexOfWorstSolution();
			this.population.remove(index);
		}
	}
	
	public void mate() {
		Collections.sort(this.population, new Comparator<Backpack>() {
			@Override
		    public int compare(Backpack lhs, Backpack rhs) {
		        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
		        return lhs.total_value > rhs.total_value ? -1 : (lhs.total_value < rhs.total_value) ? 1 : 0;
		    }
		});

		ArrayList<Backpack> newPop = new ArrayList<Backpack>();
		Random rand = new Random();

		for(int i = 0; i < this.population.size(); i+=2) {

			int cross_pointA = rand.nextInt(this.population.get(0).getNumBoxes());
			int cross_pointB = rand.nextInt(this.population.get(0).getNumBoxes());

			Backpack parentOne = this.population.get(i);
			Backpack parentTwo = this.population.get(i + 1);

			Backpack childOneA = new Backpack();
			Backpack childTwoA = new Backpack();

			childOneA.box_env = new ArrayList<Box>(parentOne.box_env);
			childTwoA.box_env = new ArrayList<Box>(parentTwo.box_env);

			for(int j = cross_pointA; j < parentOne.box_env.size(); j++) {
				Box someBox = parentOne.box_env.get(j);
				Box swapBox = parentTwo.box_env.get(j);

				int indexOneSome = childOneA.box_env.indexOf(someBox);
				int indexOneSwap = childOneA.box_env.indexOf(swapBox);
				int indexTwoSome = childTwoA.box_env.indexOf(someBox);
				int indexTwoSwap = childTwoA.box_env.indexOf(swapBox);
				childOneA.box_env.set(indexOneSome, swapBox);
				childOneA.box_env.set(indexOneSwap, someBox);
				childTwoA.box_env.set(indexTwoSome, swapBox);
				childTwoA.box_env.set(indexTwoSwap, someBox);
			}


			Backpack childOneB = new Backpack();
			Backpack childTwoB = new Backpack();

			childOneB.box_env = new ArrayList<Box>(parentTwo.box_env);
			childTwoB.box_env = new ArrayList<Box>(parentOne.box_env);

			for(int j = 0; j < cross_pointB; j++) {
				Box someBox = parentOne.box_env.get(j);
				Box swapBox = parentTwo.box_env.get(j);

				int indexOneSome = childOneB.box_env.indexOf(someBox);
				int indexOneSwap = childOneB.box_env.indexOf(swapBox);
				int indexTwoSome = childTwoB.box_env.indexOf(someBox);
				int indexTwoSwap = childTwoB.box_env.indexOf(swapBox);
				childOneB.box_env.set(indexOneSome, swapBox);
				childOneB.box_env.set(indexOneSwap, someBox);
				childTwoB.box_env.set(indexTwoSome, swapBox);
				childTwoB.box_env.set(indexTwoSwap, someBox);
			}

			newPop.add(childOneA);
			newPop.add(childTwoA);
			newPop.add(childOneB);
			newPop.add(childTwoB);
			newPop.get(i).fillBag();
			newPop.get(i + 1).fillBag();
			newPop.get(i + 2).fillBag();
			newPop.get(i + 3).fillBag();
		}
		this.population = newPop;
	}


	public void mutate(){
		for(int i = 0; i < POPULATION_SIZE; i++) {
			Random rand = new Random();
			int mutation_prob = rand.nextInt(100) + 1;
			if (mutation_prob < 5) {
				int indexA = rand.nextInt(this.population.get(0).getNumBoxes());
				int indexB = rand.nextInt(this.population.get(0).getNumBoxes());

				Box temp = population.get(i).box_env.get(indexA);
				population.get(i).box_env.set(indexA, population.get(i).box_env.get(indexB));
				population.get(i).box_env.set(indexB, temp);
			}
		}
	}
}


/* Name of the class has to be "Main" only if the class is public. */
class Ideone
{

	public static void main (String[] args) throws java.lang.Exception
	{           
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		int generations = 0;
		do {
			GeneticAlgoEnvironment SearchEnv = new GeneticAlgoEnvironment();
			System.out.println("Enter a number of generations: ");
			generations = reader.nextInt();

			for(int i = 0; i < generations; i++) {
			 	SearchEnv.cull();
			 	SearchEnv.mate();
			 	SearchEnv.mutate();	
			}

			SearchEnv.printBestSolution();
			System.out.println();
		} while (generations != 0);
		reader.close();
	}
}