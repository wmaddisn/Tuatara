/* Mesquite source code, Tuatara package.  Copyright 2007 W. Maddison & A. Mooers. Version 1.01, August 2009Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.tuatara.EvolDistinct;/*~~  */import mesquite.lib.*;import mesquite.tuatara.lib.*;/** ======================================================================== */public class EvolDistinct extends ConsValueForTaxon {	double [] credit;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		return true;  	}	 public boolean needExtProbByDefault(){  		 return false;  	 }	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	private double getBranchLength(Tree tree, int node){		return tree.getBranchLength( node, 0);	}	/*.................................................................................................................*/	public   void upPass(int node, Tree tree, double[] credit, NumberArray results) {		if (tree.nodeIsTerminal(node)){			results.setValue(tree.taxonNumberOfNode(node), credit[node] + getBranchLength(tree, node));		}		else {			//first, take credit for your own branch			credit[node] += getBranchLength(tree, node);						//this credit has to be distributed among descendants			double toBeDistributed = credit[node];			int total = tree.numberOfTerminalsInClade(node);						for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)){				//credit to descendant is proportional to the size of its clade				credit[d] += tree.numberOfTerminalsInClade(d)*1.0/total*toBeDistributed;				upPass(d, tree, credit, results);			}		}	}	/*.................................................................................................................*/	public void calculatePriorities(Taxa taxa, Tree tree, NumberArray results, MesquiteString resultsString){		if (tree == null)			return;		clearResultAndLastResult(results);		if (credit == null || credit.length < tree.getNumNodeSpaces())			credit = new double[tree.getNumNodeSpaces()];		DoubleArray.zeroArray(credit);		//calculate credit to each top		upPass(tree.getRoot(), tree, credit, results);		saveLastResult(results);		saveLastResultString(resultsString);	}	/*.................................................................................................................*/	public String getVeryShortName() {		if (riskWeight.getValue())			return "EDGE";		return "ED";	}	/*.................................................................................................................*/	public String getName() {		return "Evolutionary Distinctiveness, species [ED and EDGE]";	}	/*.................................................................................................................*/	public String getVersion() {		return null;	}	/*.................................................................................................................*/	public boolean isPrerelease() {		return false;	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Measures the length of the species' terminal branch plus its species-weighted shares of ancestral branches (ED and EDGE of Isaac et al. 2007)";	}}