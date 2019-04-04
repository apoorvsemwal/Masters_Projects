import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Main Driver Class for the algorithm.
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 *
 */
class GeneticAlgoAssignment2 implements ActionListener{

	private static int MAX_GENERATIONS = 0;

	private JFrame      frame;
	private JTextField  population_size_txt;
	private JTextField  mutation_rate_txt;
	private JTextField  max_generations_txt;
	private JTextArea   result_area;
	private JTextArea   notes_area;
	private JScrollPane scroll_result;
	private JScrollPane scroll_notes;

	private static boolean stoppingCondition(int generation_count) {
		if (generation_count == MAX_GENERATIONS - 1) {
			return true;
		}else {
			return false;
		}
	}

	public GeneticAlgoAssignment2() {

		String text = "";
		String newline = "\n";

		frame = new JFrame("Genetic Algo for Project Selection");
		frame.setBounds(100, 100, 730, 489);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);    	
		frame.setResizable(false);

		JLabel heading = new JLabel("Below given Hyper-Parameters can be changed to run different Simulations");
		heading.setBounds(150, 5, 430, 20);
		frame.getContentPane().add(heading);		

		JLabel population_size = new JLabel("Population Size");
		population_size.setBounds(65, 30, 90, 20);
		frame.getContentPane().add(population_size);		
		population_size_txt = new JTextField();
		population_size_txt.setBounds(160, 30, 60, 20);
		population_size_txt.setText("4");
		frame.getContentPane().add(population_size_txt);
		population_size_txt.setColumns(10);

		JLabel mutation_rate = new JLabel("Mutation Rate");
		mutation_rate.setBounds(65, 60, 90, 20);
		frame.getContentPane().add(mutation_rate);
		mutation_rate_txt = new JTextField();
		mutation_rate_txt.setBounds(160, 60, 60, 20);
		mutation_rate_txt.setText("0.1");
		frame.getContentPane().add(mutation_rate_txt);
		mutation_rate_txt.setColumns(10);

		JLabel max_generations = new JLabel("Maximum Generations");
		max_generations.setBounds(65, 90, 150, 20);
		frame.getContentPane().add(max_generations);
		max_generations_txt = new JTextField();
		max_generations_txt.setBounds(200, 90, 60, 20);
		max_generations_txt.setText("30");
		frame.getContentPane().add(max_generations_txt);
		max_generations_txt.setColumns(10);

		JButton evolve_btn = new JButton("Evolve");
		evolve_btn.setBackground(Color.BLACK);
		evolve_btn.setForeground(Color.WHITE);
		evolve_btn.setBounds(65, 150, 89, 23);
		evolve_btn.addActionListener(this);		
		frame.getContentPane().add(evolve_btn);

		JButton clear_btn = new JButton("Clear");
		clear_btn.setBackground(Color.BLACK);
		clear_btn.setForeground(Color.WHITE);
		clear_btn.setBounds(160, 150, 89, 23);
		clear_btn.addActionListener(this);		
		frame.getContentPane().add(clear_btn);

		notes_area = new JTextArea();
		notes_area.setEditable(false);

		text = "*==================== ASSUMPTIONS ====================*";
		notes_area.append(text + newline);
		text = "1. Population size for any simulation has to be between 2 - 8.";
		notes_area.append(text + newline);
		text = "As we have only 8 valid solutions in our scenario.";
		notes_area.append(text + newline);
		text = "Minimum 2 Parents required for a crossover.";
		notes_area.append(text + newline);
		text = "Usually with a large sample population we do not put such constraints.";
		notes_area.append(text + newline);
		text = "2. Mutation Probablity has to be between 0 - 1.";
		notes_area.append(text + newline);
		text = "3. Elitism has been followed and so the fittest member,";
		notes_area.append(text + newline);
		text = "is always carried to the next generation.";
		notes_area.append(text);
		scroll_notes = new JScrollPane(notes_area);
		scroll_notes.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll_notes.setBounds(300, 30, 400, 150);
		frame.getContentPane().add(scroll_notes);

		result_area = new JTextArea();
		result_area.setEditable(false);
		scroll_result = new JScrollPane(result_area);
		scroll_result.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll_result.setBounds(65, 200, 250, 250);
		frame.getContentPane().add(scroll_result);

	}	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GeneticAlgoAssignment2 window = new GeneticAlgoAssignment2();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Map<Integer, Double> points_gen_fitness = new HashMap<>();

		if (e.getActionCommand().equalsIgnoreCase("Evolve")) {
			String text = "";
			String newline = "\n";

			int pop_size          = 0;
			double mutation_rate  = 0.0;

			text = "";
			result_area.setText(text);
			try {
				pop_size = Integer.parseInt(population_size_txt.getText());
				if (pop_size<=1||pop_size>8) {
					text = "Population size has to be an Integer Value between 2 - 8";
					JOptionPane.showMessageDialog(new JFrame(), text, "Dialog",JOptionPane.ERROR_MESSAGE);
					return;					
				}
			} catch (NumberFormatException nfe) {
				text = "Population size has to be an Integer Value between 2 - 8";
				JOptionPane.showMessageDialog(new JFrame(), text, "Dialog",JOptionPane.ERROR_MESSAGE);
				return;			
			}	

			try {
				mutation_rate = Double.parseDouble(mutation_rate_txt.getText());
				if (mutation_rate<=0 || mutation_rate>1) {
					text = "Mutation rate has to be a Decimal Value between 0 - 1";
					JOptionPane.showMessageDialog(new JFrame(), text, "Dialog",JOptionPane.ERROR_MESSAGE);
					return;					
				}
			} catch (NumberFormatException nfe) {
				text = "Mutation rate has to be a Deecimal Value between 0 - 1";
				JOptionPane.showMessageDialog(new JFrame(), text, "Dialog",JOptionPane.ERROR_MESSAGE);
				return;			
			}		

			try {
				MAX_GENERATIONS = Integer.parseInt(max_generations_txt.getText());
				if (MAX_GENERATIONS<=0) {
					text = "Max Generations has to be a positive non zero value.";
					JOptionPane.showMessageDialog(new JFrame(), text, "Dialog",JOptionPane.ERROR_MESSAGE);
					return;					
				}
			} catch (NumberFormatException nfe) {
				text = "Max Generations has to be a positive non zero value.";
				JOptionPane.showMessageDialog(new JFrame(), text, "Dialog",JOptionPane.ERROR_MESSAGE);
				return;			
			}		

			if (mutation_rate > 1.00 || mutation_rate < 0) {
				text = "Mutation rate should be between 0 and 1.";
				JOptionPane.showMessageDialog(new JFrame(), text, "Dialog",JOptionPane.ERROR_MESSAGE);
				return;			
			}

			Population p = new Population();
			p.setMutation_rate(mutation_rate);
			p.setSample_size(pop_size);

			/**
			 * Generate an initial population.
			 */
			p.generateInitialPopulation();

			/**
			 * Calculate an overall fitness for this population i.e. sum of fitness values of all the chromosomes.
			 */
			p.calcTotalFitness();

			int generation_count = 0;

			/**
			 * Stopping Criteria is Maximum Generation Count
			 */
			while(stoppingCondition(generation_count)!=true) {
				generation_count++;

				text = "";
				result_area.append(text + newline);

				text = "================================";
				result_area.append(text + newline);

				text = "Generation: "+generation_count;
				result_area.append(text + newline);

				text = "================================";
				result_area.append(text + newline);

				text = "Population Sample:";
				result_area.append(text + newline);

				for (Chromosome chromosome : p.getSample_set()) {
					text = chromosome.getChromosome()+" - Fitness: "+String.format("%.2f", chromosome.getFitness_val());
					result_area.append(text + newline);
				}

				text = "";
				result_area.append(text + newline);

				text = "Fittest Chromosome:";
				result_area.append(text + newline);
				
				Chromosome fittest = p.findFittest();
				points_gen_fitness.put(generation_count, fittest.getFitness_val());
				text = fittest.getChromosome()+" - Fitness: "+String.format("%.2f", fittest.getFitness_val());
				result_area.append(text + newline);

				text = "";
				result_area.append(text + newline);

				text = "";
				result_area.append(text + newline);

				/**
				 * This is where we generate new generations
				 */
				p = p.evolvePopulation(result_area);

				/**
				 * Updating mutation rate for the new generation
				 */
				p.setMutation_rate(mutation_rate);

				/**
				 * Updating sample size for the new generation
				 */
				p.setSample_size(pop_size);

				/**
				 * Updating total fitness value for the new generation
				 */				
				p.calcTotalFitness();
			}
			text = "";
			result_area.append(text + newline);

			text = "================================";
			result_area.append(text + newline);

			text = "Generation: "+ ++generation_count;
			result_area.append(text + newline);

			text = "================================";
			result_area.append(text + newline);

			text = "Population Sample:";
			result_area.append(text + newline);

			for (Chromosome chromosome : p.getSample_set()) {
				text = chromosome.getChromosome()+" - Fitness: "+String.format("%.2f", chromosome.getFitness_val());
				result_area.append(text + newline);
			}

			text = "";
			result_area.append(text + newline);

			text = "Fittest Chromosome:";
			result_area.append(text + newline);
			
			Chromosome fittest = p.findFittest();
			points_gen_fitness.put(generation_count, fittest.getFitness_val());
			text = fittest.getChromosome()+" - Fitness: "+String.format("%.2f", fittest.getFitness_val());
			result_area.append(text + newline);

			text = "Solution is Chrmosome: "+p.findFittest().getChromosome()+" with fitness: "+p.findFittest().getFitness_val();
			JOptionPane.showMessageDialog(new JFrame(), text, "Dialog",JOptionPane.INFORMATION_MESSAGE);
						
		}else if (e.getActionCommand().equalsIgnoreCase("Clear")) {
			population_size_txt.setText("");
			mutation_rate_txt.setText("");
			max_generations_txt.setText("");
			result_area.setText("");
		}
	}
}

/**
 * This class represents a Chromosome - which is the binary string encoding if a project is selected or not.
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 *
 */
class Chromosome implements Comparable<Chromosome>{

	String chromosome;
	Double fitness_val;

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public Double getFitness_val() {
		return fitness_val;
	}

	public void setFitness_val(Double fitness_val) {
		this.fitness_val = fitness_val;
	}

	/**
	 * Calculates and sets the fitness value for any particular Chromosome.
	 */
	public void calulteFitness() {

		double fitness_value = 0;

		int i = 0;

		while(i<4) {
			if (i==0) {
				fitness_value += ( 0.2 * Character.getNumericValue(this.getChromosome().charAt(i)));
				i++;
			}else if (i==1) {
				fitness_value += ( 0.3 * Character.getNumericValue(this.getChromosome().charAt(i)));
				i++;
			}else if (i==2){
				fitness_value += ( 0.5 * Character.getNumericValue(this.getChromosome().charAt(i)));
				i++;
			}else if (i==3) {
				fitness_value += ( 0.1 * Character.getNumericValue(this.getChromosome().charAt(i)));
				i++;				
			}
		}

		fitness_value = Math.round(fitness_value * 100d) / 100d;
		this.setFitness_val(fitness_value);
	}

	/**
	 * This method check if any chomosome satisfies the established constraints. 
	 * @return true - if a chomosome satisfies the established constraints , false -otherwise
	 */
	public boolean validateChromosome() {

		int i, year_count = 1; double invested_capi = 0;
		double cap_proj1 = 0, cap_proj2 = 0, cap_proj3 = 0, cap_proj4 = 0, tot_cap = 0;

		boolean flag_valid_chromosome = true;

		while(year_count<4){
			if (year_count==1) {
				cap_proj1 = 0.5; 
				cap_proj2 = 1.0; 
				cap_proj3 = 1.5; 
				cap_proj4 = 0.1;
				tot_cap   = 3.1;
				invested_capi = 0;
			}else if (year_count==2) {
				cap_proj1 = 0.3; 
				cap_proj2 = 0.8; 
				cap_proj3 = 1.5; 
				cap_proj4 = 0.4;
				tot_cap   = 2.5;
				invested_capi = 0;
			}else if (year_count==3) {
				cap_proj1 = 0.2; 
				cap_proj2 = 0.2; 
				cap_proj3 = 0.3; 
				cap_proj4 = 0.1;
				tot_cap   = 0.4;
				invested_capi = 0;				
			}
			i = 0;
			invested_capi = 0;
			while(i<4) {

				if (i==0) {
					invested_capi += ( cap_proj1 * Character.getNumericValue(this.getChromosome().charAt(i)));
					i++;					
				}else if (i==1) {
					invested_capi += ( cap_proj2 * Character.getNumericValue(this.getChromosome().charAt(i)));
					i++;					
				}else if (i==2) {
					invested_capi += ( cap_proj3 * Character.getNumericValue(this.getChromosome().charAt(i)));
					i++;					
				}else if (i==3) {
					invested_capi += ( cap_proj4 * Character.getNumericValue(this.getChromosome().charAt(i)));
					i++;					
				}
			}

			if (invested_capi>0 && invested_capi>tot_cap) {
				flag_valid_chromosome = false;
				break;
			}else if (invested_capi==0) {
				flag_valid_chromosome = false;
				break;				
			}
			year_count++;
		}

		return flag_valid_chromosome;
	}

	@Override
	public int compareTo(Chromosome chromosome) {
		return (int)(this.getFitness_val() - chromosome.getFitness_val());
	}

	@Override
	public boolean equals(Object object) {
		boolean is_Equals = false;
		if (object!=null && object instanceof Chromosome) {
			Chromosome chromosome = (Chromosome)object;
			is_Equals = this.getChromosome().equalsIgnoreCase(chromosome.getChromosome());
		}
		return is_Equals;
	}

}

/**
 * This class represents a Population comprising of a List of chromosomes, having a size, a crossover/mutation rate,
 * along with an overall fitness value.
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 *
 */
class Population{

	private List<Chromosome> sample_set;
	private int    sample_size;
	private double sample_fitness;
	private double mutation_rate;

	public List<Chromosome> getSample_set() {
		return sample_set;
	}

	public void setSample_set(List<Chromosome> sample_set) {
		this.sample_set = sample_set;
	}

	public int getSample_size() {
		return sample_size;
	}

	public void setSample_size(int sample_size) {
		this.sample_size = sample_size;
	}

	public double getSample_fitness() {
		return sample_fitness;
	}

	public void setSample_fitness(double sample_fitness) {
		this.sample_fitness = sample_fitness;
	}

	public double getMutation_rate() {
		return mutation_rate;
	}

	public void setMutation_rate(double mutation_rate) {
		this.mutation_rate = mutation_rate;
	}

	public void generateInitialPopulation() {

		List<Chromosome> population_sample = new ArrayList<>();

		Random rand = new Random();

		int init_sample_count = this.getSample_size();

		this.setSample_size(init_sample_count);

		while (population_sample.size() < init_sample_count) {

			int chromosome_val_int = rand.nextInt(16);

			String chromosome_val = Integer.toString(chromosome_val_int, 2);

			while (chromosome_val.length()<4) {
				chromosome_val = "0".concat(chromosome_val);
			}

			Chromosome chromosome = new Chromosome();

			chromosome.setChromosome(chromosome_val);

			if ( !(population_sample.contains(chromosome)) && chromosome.validateChromosome()) {
				chromosome.calulteFitness();
				population_sample.add(chromosome);
			}
		}

		this.setSample_set(population_sample);
	}

	/**
	 * Finds the fittest chromosome during any given generation.
	 * @return Fittest Chromosome.
	 */
	public Chromosome findFittest() {
		double max_fitness = 0;
		int max_fitness_index = 0;
		for (int j = 0; j < this.getSample_set().size(); j++) {
			if (this.getSample_set().get(j).getFitness_val() > max_fitness) {
				max_fitness = this.getSample_set().get(j).getFitness_val();
				max_fitness_index = j;
			}			
		}
		return this.getSample_set().get(max_fitness_index);
	}

	/**
	 * Evolves a population and creates new generations.
	 * @param result_area Text Area where the results are displayed.
	 * @return New generation.
	 */
	public Population evolvePopulation(JTextArea result_area) {

		String text = "";
		String newline = "\n";

		List<Chromosome> new_population_sample = new ArrayList<>();
		Population       new_population        = new Population();		
		Chromosome       offspring_mutation    = new Chromosome();
		Chromosome[]     parents_crossover     = new Chromosome[2];
		Chromosome[]     offsprings_crossover  = new Chromosome[2];

		Map<Chromosome, Double[]> roulette    = new HashMap<>();		
		roulette = this.prepare_roulette();

		/**
		 * Elitism - Carry Forwarding the fittest member of old generation to the new one.
		 */
		new_population_sample.add(this.findFittest());

		while (new_population_sample.size()!=this.getSample_set().size()) {

			parents_crossover    = this.findCrossoverParents(roulette);
			if (parents_crossover[0]!=null && parents_crossover[0].getChromosome()!=null && parents_crossover[1]!=null && parents_crossover[1].getChromosome()!=null) {
				offsprings_crossover = this.crossover(parents_crossover);
				for (Chromosome chromosome : offsprings_crossover) {
					if (chromosome!=null && chromosome.getChromosome()!=null && !new_population_sample.contains(chromosome) && chromosome.validateChromosome()) {

						/**
						 * Mutation based on mutation rate
						 */
						double decision_value = Math.random();
						if (this.getMutation_rate() >= decision_value) {
							offspring_mutation   = this.mutation(chromosome);								
							if (offspring_mutation.validateChromosome() && !new_population_sample.contains(offspring_mutation)) {
								text = "Crossovered Parents: "+parents_crossover[0].getChromosome()+"-"+parents_crossover[1].getChromosome();
								result_area.append(text + newline);
								text = "Mutated Offspring: "+chromosome.getChromosome();
								result_area.append(text + newline);
								new_population_sample.add(offspring_mutation);
								text = "New Offsrpring: "+offspring_mutation.getChromosome();
								result_area.append(text + newline);
								if (new_population_sample.size()==this.getSample_set().size()) {
									break;
								}								
							}
						}else {
							new_population_sample.add(chromosome);
							text = "Crossovered Parents: "+parents_crossover[0].getChromosome()+"-"+parents_crossover[1].getChromosome();
							result_area.append(text + newline);
							text = "Offsrpring: "+chromosome.getChromosome();
							result_area.append(text + newline);
							if (new_population_sample.size()==this.getSample_set().size()) {
								break;
							}								
						}
					}					
				}
			}
		}
		
		new_population.setSample_set(new_population_sample);
		return new_population;
	}

	/**
	 * This method here prepares the roulette in form of a HashMap where each chromosome is the key and its value
	 * as an array of two decimal values low,high preparing a selection range for this chromosome.
	 * Check write-up for further details.
	 * @return Roulette Wheel in form of a HashMap.
	 */
	private Map<Chromosome, Double[]> prepare_roulette() {

		Map<Chromosome, Double[]> roulette = new HashMap<>();		
		double high    = 0.0; 
		double low     = 0.0;

		for (int i = 0; i < this.getSample_set().size(); i++) {
			Double[] range = new Double[2];
			low      = high + 0.01;
			high     = low  + ((this.getSample_set().get(i).getFitness_val() / this.getSample_fitness()) * 100);
			low      = Math.round(low  * 100d) / 100d;
			high     = Math.round(high * 100d) / 100d;
			range[0] = low;
			range[1] = high;
			roulette.put(this.getSample_set().get(i), range);
		}

		return roulette;
	}

	/**
	 * Executes Mutation Process
	 * @param parent_mutation on which mutation will happen
	 * @return mutated offspring
	 */
	private Chromosome mutation(Chromosome parent_mutation) {
		Random rand = new Random();
		Chromosome offspring_mutation  = new Chromosome();

		int mutation_idx = rand.nextInt(4);
		if (parent_mutation.getChromosome().charAt(mutation_idx) == '1') {
			offspring_mutation.setChromosome(parent_mutation.getChromosome().substring(0, mutation_idx).concat("0").concat(parent_mutation.getChromosome().substring(mutation_idx+1)));
		}else {
			offspring_mutation.setChromosome(parent_mutation.getChromosome().substring(0, mutation_idx).concat("1").concat(parent_mutation.getChromosome().substring(mutation_idx+1)));
		}
		offspring_mutation.calulteFitness();
		return offspring_mutation;
	}

	/**
	 * Executes Crossover Process
	 * @param parents_crossover array of parents on which crossover will happen
	 * @return cross-overed offsprings in an array 
	 */
	private Chromosome[] crossover(Chromosome[] parents_crossover) {

		Random rand = new Random();
		Chromosome[] offsprings_crossover  = new Chromosome[2]; 
		String part_offspring = "";
		int split   = rand.nextInt(3) + 1;
		offsprings_crossover[0] = new Chromosome();

		part_offspring = part_offspring.concat(parents_crossover[0].getChromosome().substring(0, split));
		part_offspring = part_offspring.concat(parents_crossover[1].getChromosome().substring(split));
		offsprings_crossover[0].setChromosome(part_offspring);

		offsprings_crossover[1] = new Chromosome();

		part_offspring = "";
		part_offspring = part_offspring.concat(parents_crossover[1].getChromosome().substring(0, split));
		part_offspring = part_offspring.concat(parents_crossover[0].getChromosome().substring(split));

		offsprings_crossover[1].setChromosome(part_offspring);

		offsprings_crossover[0].calulteFitness();
		offsprings_crossover[1].calulteFitness();

		return offsprings_crossover;

	}

	/**
	 * Find Parents for Crossover using Roulette.
	 * @param roulette Roulette Wheel
	 * @return Selected Parents Chromosomes for crossover
	 */	
	private Chromosome[] findCrossoverParents(Map<Chromosome, Double[]> roulette) {

		Chromosome[] parents_crossover  = new Chromosome[2]; 

		double spinner = Math.random() * 100;
		spinner        = Math.round(spinner * 100d) / 100d;
		for (Entry<Chromosome, Double[]> roulette_val : roulette.entrySet()) {
			if (spinner >= roulette_val.getValue()[0] && spinner <= roulette_val.getValue()[1]) {
				parents_crossover[0] = roulette_val.getKey();
				break;
			}
		}

		spinner = Math.random() * 100;
		spinner = Math.round(spinner * 100d) / 100d;
		for (Entry<Chromosome, Double[]> roulette_val : roulette.entrySet()) {
			if (spinner >= roulette_val.getValue()[0] && spinner <= roulette_val.getValue()[1]) {
				parents_crossover[1] = roulette_val.getKey();
				break;
			}
		}		

		return parents_crossover;
	}

	/**
	 * Calculates total fitness of the population.
	 */
	public void calcTotalFitness() {

		double total_fitness = 0;
		for (Chromosome chromosome : this.getSample_set()) {
			total_fitness = total_fitness + chromosome.getFitness_val();
		}
		total_fitness = Math.round(total_fitness * 100d) / 100d;
		this.setSample_fitness(total_fitness);
	}

}