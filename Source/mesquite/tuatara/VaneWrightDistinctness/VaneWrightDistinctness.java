/* Mesquite source code, Tuatara package.  Copyright 2007 W. Maddison & A. Mooers. Version 1.01, August 2009Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.tuatara.VaneWrightDistinctness;/*~~  */import mesquite.lib.*;import mesquite.tuatara.lib.*;/** ======================================================================== */public class VaneWrightDistinctness extends ConsValueForTaxon {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		return true;  	}	 public boolean needExtProbByDefault(){  		 return false;  	 }	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*/	public   void upPassMax(int node, Tree tree, int fromRoot, MesquiteNumber max) {		if (tree.nodeIsTerminal(node)){			max.setMeIfIAmLessThan(fromRoot);		}		else {			for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)){				//credit to descendant is proportional to the size of its clade				upPassMax(d, tree, fromRoot+1, max);			}		}	}	/*.................................................................................................................*/	public   void upPass(int node, Tree tree, int fromRoot, NumberArray results, MesquiteNumber max) {		if (tree.nodeIsTerminal(node)){			results.setValue(tree.taxonNumberOfNode(node), max.getDoubleValue()/fromRoot);		}		else {			for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)){				//credit to descendant is proportional to the size of its clade				upPass(d, tree, fromRoot+1, results, max);			}		}	}	/*.................................................................................................................*/	public void calculatePriorities(Taxa taxa, Tree tree, NumberArray results, MesquiteString resultsString){		if (tree == null)			return;		clearResultAndLastResult(results);		results.zeroArray();		MesquiteNumber max = new MesquiteNumber();		upPassMax(tree.getRoot(), tree, 0, max);		//calculate credit to each top		upPass(tree.getRoot(), tree, 0, results, max);		saveLastResult(results);		saveLastResultString(resultsString);	}	/*.................................................................................................................*/	public String getVeryShortName() {		if (riskWeight.getValue())			return "Vane-Wright (GE)";		return "Vane-Wright et al.";	}	/*.................................................................................................................*/	public String getName() {		return "Vane-Wright et al.'s Distinctness";	}	/*.................................................................................................................*/	public String getVersion() {		return null;	}	/*.................................................................................................................*/	public boolean isPrerelease() {		return false;	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Measures the number of branches a species is from the root, ignoring their lengths, used by Vane-Wright et al. 1991 as a conservation priority measure";	}}