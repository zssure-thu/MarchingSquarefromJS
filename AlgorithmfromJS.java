package marchingsquares;

import java.util.ArrayList;

public class AlgorithmfromJS {
	private static final class Result {
		private transient String info;
		public ArrayList<Double> path;

		Result(ArrayList<Double> contours, String str) {
			super();
			this.info = str;
			this.path = contours;
		}

		@Override
		public String toString() {
			String str = new StringBuilder("Result{info=").append(info).append(", path=").append(path.toString())
					.append('}').toString();
			return str;
		}
	}

	/**
	 * <p>
	 * Pad data with a given 'guard' value.
	 * </p>
	 *
	 * @param data
	 *            matrix to pad.
	 * @param guard
	 *            the value to use for padding. It's expected to be less than
	 *            the minimum of all data cell values.
	 * @return the resulting padded matrix which will be larger by 2 in both
	 *         directions.
	 */
	private static double[][] pad(double[][] data, double guard) {
		final int rowCount = data.length;
		final int colCount = data[0].length;
		double[][] result = new double[rowCount + 2][colCount + 2];

		// top and bottom rows
		for (int j = 0; j < colCount + 2; j++) {
			result[0][j] = guard;
			result[rowCount + 1][j] = guard;
		}

		// left- and right-most columns excl. top and bottom rows
		for (int i = 1; i < rowCount + 1; i++) {
			result[i][0] = guard;
			result[i][colCount + 1] = guard;
		}

		// the middle
		for (int i = 0; i < rowCount; i++) {
			System.arraycopy(data[i], 0, result[i + 1], 1, colCount);
		}

		return result;
	}

	public ArrayList<ArrayList<Double>> buildContours(final double[][] data, final double level) {
		Grid grid = contour(data, level);
		return contourGrid2Paths(grid);

	}

	public Result tracePath(final Grid grid, int j, int i) {
		int maxj = grid.rowCount * grid.colCount;
		ArrayList<Double> p = new ArrayList<Double>();
		int[] dxContour = { 0, 0, 1, 1, 0, 0, 0, 0, -1, 0, 1, 1, -1, 0, -1, 0 };
		int[] dyContour = { 0, -1, 0, 0, 1, 1, 1, 1, 0, -1, 0, 0, 0, -1, 0, 0 };
		int dx, dy;
		Cell.Side[] startEdge = { Cell.Side.NONE, Cell.Side.LEFT, Cell.Side.BOTTOM, Cell.Side.LEFT, Cell.Side.RIGHT,
				Cell.Side.NONE, Cell.Side.BOTTOM, Cell.Side.LEFT, Cell.Side.TOP, Cell.Side.TOP, Cell.Side.NONE,
				Cell.Side.TOP, Cell.Side.RIGHT, Cell.Side.RIGHT, Cell.Side.BOTTOM, Cell.Side.NONE };
		// {"none", "left", "bottom", "left", "right", "none", "bottom", "left",
		// "top", "top", "none", "top", "right", "right", "bottom", "none"};
		Cell.Side[] nextEdge = { Cell.Side.NONE, Cell.Side.BOTTOM, Cell.Side.RIGHT, Cell.Side.RIGHT, Cell.Side.TOP,
				Cell.Side.TOP, Cell.Side.TOP, Cell.Side.TOP, Cell.Side.LEFT, Cell.Side.BOTTOM, Cell.Side.RIGHT,
				Cell.Side.RIGHT, Cell.Side.LEFT, Cell.Side.BOTTOM, Cell.Side.LEFT, Cell.Side.NONE };
		// {"none", "bottom", "right", "right", "top", "top", "top", "top",
		// "left", "bottom", "right", "right", "left", "bottom", "left",
		// "none"};
		Cell.Side edge;

		Cell currentCell = grid.getCellAt(j, i);

		byte cval = currentCell.getCellNdx();
		edge = startEdge[cval];
		float[] pt = currentCell.getXY(edge);

		/* push initial segment */
		p.add((double) (i + pt[0]));
		p.add((double) (j + pt[1]));
		edge = nextEdge[cval];
		pt = currentCell.getXY(edge);
		p.add((double) (i + pt[0]));
		p.add((double) (j + pt[1]));
		currentCell.clear();

		/* now walk arround the enclosed area in clockwise-direction */
		int k = i + dxContour[cval];
		int l = j + dyContour[cval];
		byte prev_cval = cval;

		while ((k >= 0) && (l >= 0) && (l < maxj) && ((k != i) || (l != j))) {
			currentCell = grid.getCellAt(l, k);
			if (currentCell == null) { /* path ends here */
				// console.log(k + " " + l + " is undefined, stopping path!");
				break;
			}
			cval = currentCell.getCellNdx();
			if ((cval == 0) || (cval == 15)) {
				Result result = new Result(p, "mergeable");
				return result;
				// return { path: p, info: "mergeable" };
			}
			edge = nextEdge[cval];
			dx = dxContour[cval];
			dy = dyContour[cval];
			if ((cval == 5) || (cval == 10)) {
				/*
				 * select upper or lower band, depending on previous cells cval
				 */
				if (cval == 5) {
					if (currentCell.isFlipped()) { /*
													 * this is actually a
													 * flipped case 10
													 */
						if (dyContour[prev_cval] == -1) {
							edge = Cell.Side.LEFT;
							dx = -1;
							dy = 0;
						} else {
							edge = Cell.Side.RIGHT;
							dx = 1;
							dy = 0;
						}
					} else { /* real case 5 */
						if (dxContour[prev_cval] == -1) {
							edge = Cell.Side.BOTTOM;
							dx = 0;
							dy = -1;
						}
					}
				} else if (cval == 10) {
					if (currentCell
							.isFlipped()) { /*
											 * this is actually a flipped case 5
											 */
						if (dxContour[prev_cval] == -1) {
							edge = Cell.Side.TOP;
							dx = 0;
							dy = 1;
						} else {
							edge = Cell.Side.BOTTOM;
							dx = 0;
							dy = -1;
						}
					} else { /* real case 10 */
						if (dyContour[prev_cval] == 1) {
							edge = Cell.Side.LEFT;
							dx = -1;
							dy = 0;
						}
					}
				}
			}
			pt = currentCell.getXY(edge);
			p.add((double) k + pt[0]);
			p.add((double) l + pt[1]);
			currentCell.clear();
			k += dx;
			l += dy;
			prev_cval = cval;
		}

		// return { path: p, info: "closed" };
		Result result = new Result(p, "closed");
		return result;
	}

	public Grid contour(double[][] data, double isovalue) {
		final int rowCount = data.length;
		final int colCount = data[0].length;

		// Every 2x2 block of pixels in the binary image forms a contouring
		// cell,
		// so the whole image is represented by a grid of such cells. Note that
		// this contouring grid is one cell smaller in each direction than the
		// original 2D field.
		Cell[][] cells = new Cell[rowCount - 1][colCount - 1];
		for (int r = 0; r < rowCount - 1; r++) {
			for (int c = 0; c < colCount - 1; c++) {
				// Compose the 4 bits at the corners of the cell to build a
				// binary
				// index: walk around the cell in a clockwise direction
				// appending
				// the bit to the index, using bitwise OR and left-shift, from
				// most
				// significant bit at the top left, to least significant bit at
				// the
				// bottom left. The resulting 4-bit index can have 16 possible
				// values in the range 0-15.
				int ndx = 0;
				final double tl = data[r + 1][c];
				final double tr = data[r + 1][c + 1];
				final double br = data[r][c + 1];
				final double bl = data[r][c];
				System.out.println("tl,tr,br,bl" + tl + "  " + tr + "  " + br + "  " + bl);
				// ndx |= (tl > isovalue ? 0 : 8);
				// ndx |= (tr > isovalue ? 0 : 4);
				// ndx |= (br > isovalue ? 0 : 2);
				// ndx |= (bl > isovalue ? 0 : 1);
				ndx |= (tl >= isovalue ? 8 : 0);
				ndx |= (tr >= isovalue ? 4 : 0);
				ndx |= (br >= isovalue ? 2 : 0);
				ndx |= (bl >= isovalue ? 1 : 0);
				System.out.println("ndx value: " + ndx);
				boolean flipped = false;
				if (ndx == 5 || ndx == 10) {
					// resolve the ambiguity by using the average data value for
					// the
					// center of the cell to choose between different
					// connections of
					// the interpolated points.
					double center = (tl + tr + br + bl) / 4;
					if (ndx == 5 && center < isovalue) {
						ndx = 10;
						flipped = true;
					} else if (ndx == 10 && center < isovalue) {
						ndx = 5;
						flipped = true;
					}
				}
				// NOTE (rsn) - we only populate the grid w/ non-trivial cells;
				// i.e. those w/ an index different than 0 and 15.
				if (ndx != 0 && ndx != 15) {
					// Apply linear interpolation between the original field
					// data
					// values to find the exact position of the contour line
					// along
					// the edges of the cell.
					float left = 0.5F;
					float top = 0.5F;
					float right = 0.5F;
					float bottom = 0.5F;
					switch (ndx) {
					case 1:
						left = (float) ((isovalue - bl) / (tl - bl));
						bottom = (float) ((isovalue - bl) / (br - bl));
						break;
					case 2:
						bottom = (float) ((isovalue - bl) / (br - bl));
						right = (float) ((isovalue - br) / (tr - br));
						break;
					case 3:
						left = (float) ((isovalue - bl) / (tl - bl));
						right = (float) ((isovalue - br) / (tr - br));
						break;
					case 4:
						top = (float) ((isovalue - tl) / (tr - tl));
						right = (float) ((isovalue - br) / (tr - br));
						break;
					case 5:
						left = (float) ((isovalue - bl) / (tl - bl));
						bottom = (float) ((isovalue - bl) / (br - bl));
						top = (float) ((isovalue - tl) / (tr - tl));
						right = (float) ((isovalue - br) / (tr - br));
						break;
					case 6:
						bottom = (float) ((isovalue - bl) / (br - bl));
						top = (float) ((isovalue - tl) / (tr - tl));
						break;
					case 7:
						left = (float) ((isovalue - bl) / (tl - bl));
						top = (float) ((isovalue - tl) / (tr - tl));
						break;
					case 8:
						left = (float) ((isovalue - bl) / (tl - bl));
						top = (float) ((isovalue - tl) / (tr - tl));
						break;
					case 9:
						bottom = (float) ((isovalue - bl) / (br - bl));
						top = (float) ((isovalue - tl) / (tr - tl));
						break;
					case 10:
						left = (float) ((isovalue - bl) / (tl - bl));
						bottom = (float) ((isovalue - bl) / (br - bl));
						top = (float) ((isovalue - tl) / (tr - tl));
						right = (float) ((isovalue - br) / (tr - br));
						break;
					case 11:
						top = (float) ((isovalue - tl) / (tr - tl));
						right = (float) ((isovalue - br) / (tr - br));
						break;
					case 12:
						left = (float) ((isovalue - bl) / (tl - bl));
						right = (float) ((isovalue - br) / (tr - br));
						break;
					case 13:
						bottom = (float) ((isovalue - bl) / (br - bl));
						right = (float) ((isovalue - br) / (tr - br));
						break;
					case 14:
						left = (float) ((isovalue - bl) / (tl - bl));
						bottom = (float) ((isovalue - bl) / (br - bl));
						break;
					default: // shouldn't happen
						final String m = "Unexpected cell index " + ndx;
						throw new IllegalStateException(m);
					}

					cells[r][c] = new Cell(ndx, flipped, left, top, right, bottom);
				}
			}
		}
		final Grid result = new Grid(cells, isovalue);
		return result;
	}

	public ArrayList<ArrayList<Double>> contourGrid2Paths(final Grid grid) {
		ArrayList<ArrayList<Double>> paths = new ArrayList<ArrayList<Double>>();
		int path_idx = 0;
		double epsilon = 1E-7;
		for (int r = 0; r < grid.rowCount; r++) {
			for (int c = 0; c < grid.colCount; c++) {
				// find a start node...
				final Cell cell = grid.getCellAt(r, c);
				// if(cell != null)
				// System.out.println(cell.getCellNdx());
				if (cell != null && !cell.isTrivial() && !cell.isSaddle()) {
					// complete the [sub-]path and close it
					Result p = tracePath(grid, r, c);
					boolean merged = false;
					/* we may try to merge paths at this point */
					if (p.info == "mergeable") {
						/*
						 * search backwards through the path array to find an
						 * entry that starts with where the current path ends...
						 */
						double x = p.path.get(p.path.size() - 2), y = p.path.get(p.path.size() - 1);

						for (int k = path_idx - 1; k >= 0; k--) {
							if ((Math.abs(paths.get(k).get(0) - x) <= epsilon)
									&& (Math.abs(paths.get(k).get(1) - y) <= epsilon)) {
								for (int l = p.path.size() - 2; l >= 0; --l) {
									//zssure:2017.11.30,there may be some bug
									paths.get(k).add(0, p.path.get(l));
									paths.get(k).add(0, p.path.get(l - 1));
									//zssure:2017.11.30,end
								}
								merged = true;
								break;
							}
						}
					}
					if (!merged)
						paths.add(p.path);

				}
			}
		}
		// console.log(paths[array[1]]) ;
		return paths;
	}
}
