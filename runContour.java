package marchingsquares;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class runContour {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Algorithm alg = new Algorithm();
		AlgorithmfromJS algJS = new AlgorithmfromJS();
		double[] thresholds = { 0.5 };
		try {
			ArrayList<ArrayList<Double>> contours = alg.buildContours2(buildTest2(), thresholds);
			ArrayList<ArrayList<Double>> paths = algJS.buildContours(buildTest3(), thresholds[0]);
			System.out.println(paths);
			System.out.print(contours);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		List<double[]> pointList = new ArrayList<double[]>();
//		try {
//			GeneralPath[] paths = alg.buildContours(buildTest(), thresholds);
//			double[] coords = new double[6];
//			int numSubPaths = 0;
//			for (int i = 0; i < paths.length; i++) {
//				PathIterator pathIterator = paths[i].getPathIterator(null);
//
//				do {
//					float[] cureentSegment = new float[2];
//					//int type = pathIterator.currentSegment(cureentSegment);
//					switch (pathIterator.currentSegment(coords)) {
//			        case PathIterator.SEG_MOVETO:
//			            pointList.add(Arrays.copyOf(coords, 2));
//			            ++ numSubPaths;
//			            break;
//			        case PathIterator.SEG_LINETO:
//			            pointList.add(Arrays.copyOf(coords, 2));
//			            break;
//			        case PathIterator.SEG_CLOSE:
//			            if (numSubPaths > 1) {
//			                throw new IllegalArgumentException("Path contains multiple subpaths");
//			            }
//			            System.out.print(pointList.toArray(new double[pointList.size()][]));
//	                case PathIterator.SEG_QUADTO:
//	                	System.out.print("quadto: "+Arrays.copyOf(coords,2));
//	                case PathIterator.SEG_CUBICTO:
//	                	System.out.print("cubicto: "+Arrays.copyOf(coords,2));
//			        }
//					pathIterator.next();
//				} while (!pathIterator.isDone());
//
//				System.out.println("test");
//			}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	public static double[][] buildTest() {
		double[][] test = { { 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 1, 1, 1, 0, 0 }, { 0, 0, 1, 1, 1, 0, 0 },
				{ 0, 0, 1, 1, 1, 0, 0 }, { 0, 0, 1, 1, 1, 0, 0 }, { 0, 0, 1, 1, 1, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0 } };
		return test;
	}
	public static double[][] buildTest3(){
		double[][] test = { { 0, 0, 0, 0, 0, 0, 0 }, { 0, 1, 1, 0, 1,1, 0 }, { 0, 1, 1, 0, 1,1, 0 },
				{ 0, 1, 1, 0, 1,1, 0 }, { 0, 1, 1, 0, 1,1, 0 },{ 0, 1, 1, 0, 1,1, 0 }, { 0, 0, 0, 0, 0, 0, 0 } };
		return test;
	}
	public static double[][] buildTest2() {
//		double[][] test = { { 0, 0, 0, 0, 0, 0, 0 }, { 0, 1, 1, 0, 1,1, 0 }, { 0, 1, 1, 0, 1,1, 0 },
//				{ 0, 1, 1, 0, 1,1, 0 }, { 0, 1, 1, 0, 1,1, 0 },{ 0, 1, 1, 0, 1,1, 0 }, { 0, 0, 0, 0, 0, 0, 0 } };
		double[][] test = {  { 1, 1, 0, 1,1},  { 1, 1, 0, 1,1},
				 { 1, 1, 0, 1,1}, { 1, 1, 0, 1,1}, { 1, 1, 0, 1,1}};
		return test;
	}
}
