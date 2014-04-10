/* Mesquite source code, Tuatara package.  Copyright 2007 W. Maddison & A. Mooers. Version 1.01, August 2009Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.tuatara.ExpPhylContribution;/*~~  */import mesquite.cont.lib.*;import mesquite.lib.*;import mesquite.tuatara.lib.*;/** ======================================================================== */public class ExpPhylContribution extends ConsValueForTaxon {	double [] cladeExtinctionProbs,expectedConnectionLengthUp;	boolean addRoot = false;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		return true;  	}	 public boolean needExtProbByDefault(){  		 return true;  	 }//	THIS IS ROOTED	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*///	first, do downpass and count clade extinction probabilities	public   void downPass(int node, Tree tree, ContinuousDistribution states, double [] cladeExtinctionProbs) {		if (tree.nodeIsTerminal(node))			cladeExtinctionProbs[node] = getProbabilityOfExtinction(tree.getTaxa(), tree.taxonNumberOfNode(node), states);   //getting the probability of exctinction as a continuous character		else {			cladeExtinctionProbs[node] = 1.0;			for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)){				downPass(d, tree, states, cladeExtinctionProbs);				cladeExtinctionProbs[node] *= cladeExtinctionProbs[d];  //this clade goes extinct only if all daughters do			}		}	}	/*.................................................................................................................*/	public   void upPass(int node, Tree tree, double lengthFromRoot, ContinuousDistribution states, double [] cladeExtinctionProbs, double[] expectedConnectionLengthUp, NumberArray results) {		if (tree.getRoot() != node || addRoot)			lengthFromRoot += getBranchLength(tree, node);		if (tree.getRoot() != node){  //NOT the root			//first, calculate probability all sisters go extinct			double probAllSistersGoExtinct  = 1.0;			for (int sister = tree.firstDaughterOfNode(tree.motherOfNode(node)); tree.nodeExists(sister); sister = tree.nextSisterOfNode(sister))				if (sister != node) 					probAllSistersGoExtinct  *= cladeExtinctionProbs[sister];			if (tree.getRoot() ==tree.motherOfNode(node)){//if daughter of root, get expected connection length from sister only				//We are calculating this as rooted; hence the root must be included in any subtree				//Two possibilities: 				//1. At least one sister survived, thus count length from root, but not further, because sister would have its root connection counted				expectedConnectionLengthUp[node] = (1-probAllSistersGoExtinct)*getBranchLength(tree, node);				//2. No sisters survive; connection length is from root, possibly including root if addRoot is on				expectedConnectionLengthUp[node] += probAllSistersGoExtinct*lengthFromRoot;			}			else { //not root and not daughter of root				//Two possibilities				//1. At least one sister remains.  Thus, the expected connection added here is just the length of this branch				expectedConnectionLengthUp[node] = (1-probAllSistersGoExtinct)*(getBranchLength(tree, node));				//2. All sisters went extinct.  Thus, we consider the expect connection coming from below				expectedConnectionLengthUp[node] += probAllSistersGoExtinct*(getBranchLength(tree, node) + expectedConnectionLengthUp[tree.motherOfNode(node)]);			}		}		if (tree.nodeIsTerminal(node))			results.setValue(tree.taxonNumberOfNode(node), expectedConnectionLengthUp[node]); //harvest results		else {			for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				upPass(d, tree, lengthFromRoot, states, cladeExtinctionProbs, expectedConnectionLengthUp, results);		}	}	/*.................................................................................................................*/	private double getBranchLength(Tree tree, int node){		return tree.getBranchLength(node, 0);	}		/*.................................................................................................................*/	public void calculatePriorities(Taxa taxa, Tree tree, NumberArray results, MesquiteString resultsString){		if (tree == null)			return;		if (cladeExtinctionProbs == null || cladeExtinctionProbs.length < tree.getNumNodeSpaces())			cladeExtinctionProbs = new double[tree.getNumNodeSpaces()];		if (expectedConnectionLengthUp == null || expectedConnectionLengthUp.length < tree.getNumNodeSpaces())			expectedConnectionLengthUp = new double[tree.getNumNodeSpaces()];		DoubleArray.zeroArray(cladeExtinctionProbs);		DoubleArray.zeroArray(expectedConnectionLengthUp);		clearResultAndLastResult(results);		observedStates = (ContinuousDistribution)characterSourceTask.getCurrentCharacter(tree);		//calculate probs of different clade sizes		downPass(tree.getRoot(), tree, observedStates, cladeExtinctionProbs);		//calculate credit to each top		upPass(tree.getRoot(), tree, 0.0, observedStates, cladeExtinctionProbs, expectedConnectionLengthUp, results);		saveLastResult(results);		saveLastResultString(resultsString);	}	/*.................................................................................................................*/	public String getVeryShortName() {		if (riskWeight.getValue())			return "HEDGE";		return "HED";	}	/*.................................................................................................................*/	public String getName() {		return "Expected Terminal Branch Length [HED and HEDGE]";	}	/*.................................................................................................................*/	public String getVersion() {		return null;	}	/*.................................................................................................................*/	public boolean isPrerelease() {		return false;	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Measures the expected (after probabilistic extinction) length of the terminal branch belonging to the species (the pendant edge length), introduced as HED and EDGE by Steel, Mimoto and Mooers 2007.";	}}