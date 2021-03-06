package ann;

@SuppressWarnings("serial")
public class AccruingWeight implements Weight {
	
	private double weight, newWeight, lastWgtChg, blameFromOutput, cumSqrChg;
	private boolean frozen = false;

	public AccruingWeight() {
		this(DefaultParameters.RANDOM_WEIGHT());
	}
	public AccruingWeight(double w) {
		this(w, false);
	}
	public AccruingWeight(double w, boolean freeze) {
		this.weight = w;
		this.newWeight = w;
		this.lastWgtChg = 0;
		this.frozen = freeze;
	}
	public void enactWeightChange(double decay) {
		if (!frozen) this.weight = this.newWeight * (1 - decay);
	}
	@Override
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.newWeight = weight;
	}
	public void chgWeight(double inc) {
		this.newWeight += inc;
		if (!Double.isFinite(newWeight)) {
			System.out.println();
		}
	}
	public void multWeight(double mult) {
		this.newWeight *= mult;
	}
	public double getBlameFromOutput() {
		return blameFromOutput;
	}
	public void propagateError(double delta, double inputActivation, double learningRate, double momentum, boolean isFrozen) {
		this.blameFromOutput = delta * weight;
		double change = learningRate * delta * inputActivation + momentum * this.lastWgtChg;
		if (!isFrozen) chgWeight(change);
		this.cumSqrChg += inputActivation;// * (1 - delta * delta);///(change*change+1);
		this.lastWgtChg = change;
	}

	public void frieze() {
		this.frozen = true;
	}

	public void unFrieze() {
		this.frozen = false;
	}
	public double getCumSqrChg() {
		return cumSqrChg;
	}
}
