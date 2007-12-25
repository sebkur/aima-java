package aima.test.learningtest.neural;

import junit.framework.TestCase;
import aima.learning.neural.Layer;
import aima.learning.neural.LogSigActivationFunction;
import aima.learning.neural.PureLinearActivationFunction;
import aima.learning.neural.Vector;
import aima.util.Matrix;

public class LayerTests extends TestCase {
	public void testFeedForward() {
		// example 11.14 of Neural Network Design by Hagan, Demuth and Beale
		// lots of tedious tests necessary to ensure nn is fundamentally correct
		Matrix weightMatrix1 = new Matrix(2, 1);
		weightMatrix1.set(0, 0, -0.27);
		weightMatrix1.set(1, 0, -0.41);

		Vector biasVector1 = new Vector(2);
		biasVector1.setValue(0, -0.48);
		biasVector1.setValue(1, -0.13);

		Layer layer1 = new Layer(weightMatrix1, biasVector1,
				new LogSigActivationFunction());

		Vector inputVector1 = new Vector(1);
		inputVector1.setValue(0, 1);

		Vector expected = new Vector(2);
		expected.setValue(0, 0.321);
		expected.setValue(1, 0.368);

		Vector result1 = layer1.feedForward(inputVector1);
		assertEquals(expected.getValue(0), result1.getValue(0), 0.001);
		assertEquals(expected.getValue(1), result1.getValue(1), 0.001);

		Matrix weightMatrix2 = new Matrix(1, 2);
		weightMatrix2.set(0, 0, 0.09);
		weightMatrix2.set(0, 1, -0.17);

		Vector biasVector2 = new Vector(1);
		biasVector2.setValue(0, 0.48);

		Layer layer2 = new Layer(weightMatrix2, biasVector2,
				new PureLinearActivationFunction());
		Vector inputVector2 = layer1.getLastActivationValues();
		Vector result2 = layer2.feedForward(inputVector2);
		assertEquals(0.446, result2.getValue(0), 0.001);

	}

	public void testSensitivityMatrixCalculationFromErrorVector() {
		Matrix weightMatrix1 = new Matrix(2, 1);
		weightMatrix1.set(0, 0, -0.27);
		weightMatrix1.set(1, 0, -0.41);

		Vector biasVector1 = new Vector(2);
		biasVector1.setValue(0, -0.48);
		biasVector1.setValue(1, -0.13);

		Layer layer1 = new Layer(weightMatrix1, biasVector1,
				new LogSigActivationFunction());

		Vector inputVector1 = new Vector(1);
		inputVector1.setValue(0, 1);

		layer1.feedForward(inputVector1);

		Matrix weightMatrix2 = new Matrix(1, 2);
		weightMatrix2.set(0, 0, 0.09);
		weightMatrix2.set(0, 1, -0.17);

		Vector biasVector2 = new Vector(1);
		biasVector2.setValue(0, 0.48);

		Layer layer2 = new Layer(weightMatrix2, biasVector2,
				new PureLinearActivationFunction());
		Vector inputVector2 = layer1.getLastActivationValues();
		layer2.feedForward(inputVector2);

		Vector errorVector = new Vector(1);
		errorVector.setValue(0, 1.261);
		Matrix sensistivityMatrix = layer2
				.sensitivityMatrixFromErrorMatrix(errorVector);
		assertEquals(-2.522, sensistivityMatrix.get(0, 0));
		// System.out.println(sensistivityMatrix);

	}

	public void testSensitivityMatrixCalculationFromSucceedingLayer() {
		Matrix weightMatrix1 = new Matrix(2, 1);
		weightMatrix1.set(0, 0, -0.27);
		weightMatrix1.set(1, 0, -0.41);

		Vector biasVector1 = new Vector(2);
		biasVector1.setValue(0, -0.48);
		biasVector1.setValue(1, -0.13);

		Layer layer1 = new Layer(weightMatrix1, biasVector1,
				new LogSigActivationFunction());

		Vector inputVector1 = new Vector(1);
		inputVector1.setValue(0, 1);

		layer1.feedForward(inputVector1);

		Matrix weightMatrix2 = new Matrix(1, 2);
		weightMatrix2.set(0, 0, 0.09);
		weightMatrix2.set(0, 1, -0.17);

		Vector biasVector2 = new Vector(1);
		biasVector2.setValue(0, 0.48);

		Layer layer2 = new Layer(weightMatrix2, biasVector2,
				new PureLinearActivationFunction());
		Vector inputVector2 = layer1.getLastActivationValues();
		layer2.feedForward(inputVector2);

		Vector errorVector = new Vector(1);
		errorVector.setValue(0, 1.261);
		layer2.sensitivityMatrixFromErrorMatrix(errorVector);
		Matrix sensitivityMatrix = layer1
				.sensitivityMatrixFromSucceedingLayer(layer2);
		assertEquals(2, sensitivityMatrix.getRowDimension());
		assertEquals(1, sensitivityMatrix.getColumnDimension());
		assertEquals(-0.0495, sensitivityMatrix.get(0, 0), 0.001);
		assertEquals(0.0997, sensitivityMatrix.get(1, 0), 0.001);

	}

	public void testWeightUpdateMatrixesFormedCorrectly() {
		Matrix weightMatrix1 = new Matrix(2, 1);
		weightMatrix1.set(0, 0, -0.27);
		weightMatrix1.set(1, 0, -0.41);

		Vector biasVector1 = new Vector(2);
		biasVector1.setValue(0, -0.48);
		biasVector1.setValue(1, -0.13);

		Layer layer1 = new Layer(weightMatrix1, biasVector1,
				new LogSigActivationFunction());

		Vector inputVector1 = new Vector(1);
		inputVector1.setValue(0, 1);

		layer1.feedForward(inputVector1);

		Matrix weightMatrix2 = new Matrix(1, 2);
		weightMatrix2.set(0, 0, 0.09);
		weightMatrix2.set(0, 1, -0.17);

		Vector biasVector2 = new Vector(1);
		biasVector2.setValue(0, 0.48);

		Layer layer2 = new Layer(weightMatrix2, biasVector2,
				new PureLinearActivationFunction());
		Vector inputVector2 = layer1.getLastActivationValues();
		layer2.feedForward(inputVector2);

		Vector errorVector = new Vector(1);
		errorVector.setValue(0, 1.261);
		layer2.sensitivityMatrixFromErrorMatrix(errorVector);
		layer1.sensitivityMatrixFromSucceedingLayer(layer2);

		Matrix weightUpdateMatrix2 = layer2.calculateWeightUpdates(layer1
				.getLastActivationValues(), 0.1);
		assertEquals(0.0809, weightUpdateMatrix2.get(0, 0), 0.001);
		assertEquals(0.0928, weightUpdateMatrix2.get(0, 1), 0.001);

		Matrix lastWeightUpdateMatrix2 = layer2.getLastWeightUpdateMatrix();
		assertEquals(0.0809, lastWeightUpdateMatrix2.get(0, 0), 0.001);
		assertEquals(0.0928, lastWeightUpdateMatrix2.get(0, 1), 0.001);

		Matrix penultimateWeightUpdatematrix2 = layer2
				.getPenultimateWeightUpdateMatrix();
		assertEquals(0.0, penultimateWeightUpdatematrix2.get(0, 0), 0.001);
		assertEquals(0.0, penultimateWeightUpdatematrix2.get(0, 1), 0.001);

		Matrix weightUpdateMatrix1 = layer1.calculateWeightUpdates(
				inputVector1, 0.1);
		assertEquals(0.0049, weightUpdateMatrix1.get(0, 0), 0.001);
		assertEquals(-0.00997, weightUpdateMatrix1.get(1, 0), 0.001);

		Matrix lastWeightUpdateMatrix1 = layer1.getLastWeightUpdateMatrix();
		assertEquals(0.0049, lastWeightUpdateMatrix1.get(0, 0), 0.001);
		assertEquals(-0.00997, lastWeightUpdateMatrix1.get(1, 0), 0.001);
		Matrix penultimateWeightUpdatematrix1 = layer1
				.getPenultimateWeightUpdateMatrix();
		assertEquals(0.0, penultimateWeightUpdatematrix1.get(0, 0), 0.001);
		assertEquals(0.0, penultimateWeightUpdatematrix1.get(1, 0), 0.001);
		// System.out.println(weightUpdateMatrix1);

	}

	public void testBiasUpdateMatrixesFormedCorrectly() {
		Matrix weightMatrix1 = new Matrix(2, 1);
		weightMatrix1.set(0, 0, -0.27);
		weightMatrix1.set(1, 0, -0.41);

		Vector biasVector1 = new Vector(2);
		biasVector1.setValue(0, -0.48);
		biasVector1.setValue(1, -0.13);

		Layer layer1 = new Layer(weightMatrix1, biasVector1,
				new LogSigActivationFunction());

		Vector inputVector1 = new Vector(1);
		inputVector1.setValue(0, 1);

		layer1.feedForward(inputVector1);

		Matrix weightMatrix2 = new Matrix(1, 2);
		weightMatrix2.set(0, 0, 0.09);
		weightMatrix2.set(0, 1, -0.17);

		Vector biasVector2 = new Vector(1);
		biasVector2.setValue(0, 0.48);

		Layer layer2 = new Layer(weightMatrix2, biasVector2,
				new PureLinearActivationFunction());
		Vector inputVector2 = layer1.getLastActivationValues();
		layer2.feedForward(inputVector2);

		Vector errorVector = new Vector(1);
		errorVector.setValue(0, 1.261);
		layer2.sensitivityMatrixFromErrorMatrix(errorVector);
		layer1.sensitivityMatrixFromSucceedingLayer(layer2);

		Vector biasUpdateVector2 = layer2.calculateBiasUpdates(0.1);
		assertEquals(0.2522, biasUpdateVector2.getValue(0), 0.001);

		Vector lastBiasUpdateVector2 = layer2.getLastBiasUpdateVector();
		assertEquals(0.2522, lastBiasUpdateVector2.getValue(0), 0.001);

		Vector penultimateBiasUpdateVector2 = layer2
				.getPenultimateBiasUpdateVector();
		assertEquals(0.0, penultimateBiasUpdateVector2.getValue(0), 0.001);

		Vector biasUpdateVector1 = layer1.calculateBiasUpdates(0.1);
		assertEquals(0.00495, biasUpdateVector1.getValue(0), 0.001);
		assertEquals(-0.00997, biasUpdateVector1.getValue(1), 0.001);

		Vector lastBiasUpdateVector1 = layer1.getLastBiasUpdateVector();

		assertEquals(0.00495, lastBiasUpdateVector1.getValue(0), 0.001);
		assertEquals(-0.00997, lastBiasUpdateVector1.getValue(1), 0.001);

		Vector penultimateBiasUpdateVector1 = layer1
				.getPenultimateBiasUpdateVector();
		assertEquals(0.0, penultimateBiasUpdateVector1.getValue(0), 0.001);
		assertEquals(0.0, penultimateBiasUpdateVector1.getValue(1), 0.001);

	}

	public void testWeightsAndBiasesUpdatedCorrectly() {
		Matrix weightMatrix1 = new Matrix(2, 1);
		weightMatrix1.set(0, 0, -0.27);
		weightMatrix1.set(1, 0, -0.41);

		Vector biasVector1 = new Vector(2);
		biasVector1.setValue(0, -0.48);
		biasVector1.setValue(1, -0.13);

		Layer layer1 = new Layer(weightMatrix1, biasVector1,
				new LogSigActivationFunction());

		Vector inputVector1 = new Vector(1);
		inputVector1.setValue(0, 1);

		layer1.feedForward(inputVector1);

		Matrix weightMatrix2 = new Matrix(1, 2);
		weightMatrix2.set(0, 0, 0.09);
		weightMatrix2.set(0, 1, -0.17);

		Vector biasVector2 = new Vector(1);
		biasVector2.setValue(0, 0.48);

		Layer layer2 = new Layer(weightMatrix2, biasVector2,
				new PureLinearActivationFunction());
		Vector inputVector2 = layer1.getLastActivationValues();
		layer2.feedForward(inputVector2);

		Vector errorVector = new Vector(1);
		errorVector.setValue(0, 1.261);
		layer2.sensitivityMatrixFromErrorMatrix(errorVector);
		layer1.sensitivityMatrixFromSucceedingLayer(layer2);

		// /////////
		layer2.calculateWeightUpdates(layer1.getLastActivationValues(), 0.1);

		layer2.calculateBiasUpdates(0.1);

		layer1.calculateWeightUpdates(inputVector1, 0.1);

		layer1.calculateBiasUpdates(0.1);

		layer2.updateWeights();
		Matrix newWeightMatrix2 = layer2.getWeightMatrix();
		assertEquals(0.171, newWeightMatrix2.get(0, 0), 0.001);
		assertEquals(-0.0772, newWeightMatrix2.get(0, 1), 0.001);

		layer2.updateBiases();
		Vector newBiasVector2 = layer2.getBiasVector();
		assertEquals(0.7322, newBiasVector2.getValue(0));

		layer1.updateWeights();
		Matrix newWeightMatrix1 = layer1.getWeightMatrix();

		assertEquals(-0.265, newWeightMatrix1.get(0, 0), 0.001);
		assertEquals(-0.419, newWeightMatrix1.get(1, 0), 0.001);

		layer1.updateBiases();
		Vector newBiasVector1 = layer1.getBiasVector();

		assertEquals(-0.475, newBiasVector1.getValue(0), 0.001);
		assertEquals(-0.139, newBiasVector1.getValue(1), 0.001);
	}
}