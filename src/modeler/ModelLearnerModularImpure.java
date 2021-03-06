package modeler;

import ann.Node;
import ann.indirectencodings.RelationManager;
import modulemanagement.ModuleManagerImpure;

public class ModelLearnerModularImpure extends ModelLearnerHeavy {

	private final ModuleManagerImpure moduleManager;
	private final RelationManager<Node> relMngr;
	private int processTransitions;
	private int processTimes;

	public ModelLearnerModularImpure(ModelLearnerHeavy base, RelationManager<Node> relMngr, ModuleManagerImpure moduleManager,
			int processTransitions, int processTimes) {
		super(base.errorHalfLife, new int[] {}, null, null, base.actFn, base.maxReplaySize);
		this.relMngr = relMngr;
		this.experienceReplay.addExperience(base.getExperience());
		this.processTransitions = processTransitions;
		this.processTimes = processTimes;
		this.getModelVTA().setANN(base.getModelVTA().getNeuralNetwork());
		this.moduleManager = moduleManager;
	}
	
	@Override
	public double[] newStateVars(double[] stateVars, double[] action, int jointAdjustments) {
		return moduleManager.getOutputs(this, relMngr, stateVars, action);
	}
	
	@Override
	public void learnOnline(double lRate, double mRate, double sRate) {
		super.learnOnline(lRate, mRate, sRate);
		moduleManager.processFullModel(this, relMngr, processTransitions, processTimes);
	}
	
	@Override
	public void learnFromMemory(double lRate, double mRate, double sRate,
			boolean resample, int iterations, long displayProgressMs, double stopAtErrThreshold) {
		super.learnFromMemory(lRate, mRate, sRate, resample, iterations, displayProgressMs, stopAtErrThreshold);
		moduleManager.processFullModel(this, relMngr, processTransitions, processTimes);
	}

	public ModuleManagerImpure getModuleManager() {
		return moduleManager;
	}

	public RelationManager<Node> getRelMngr() {
		return relMngr;
	}
}
