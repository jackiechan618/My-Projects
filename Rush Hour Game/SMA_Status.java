import java.util.*;


public class SMA_Status {
	List<SMA_Vehicle> vehicleList; 
	char[][] statusMap;
	SMA_Status parent;      // parent status
	SMA_Status next; 	    // next status in open list or closed list
	int fvalue; 		// F value
	int gvalue; 		// G value
	int hvalue; 		// H value
	
	public SMA_Status() {
		vehicleList = new ArrayList<SMA_Vehicle>();
		parent = next = null;
		fvalue = gvalue = hvalue = 0;
		statusMap = new char[6][6];
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				statusMap[i][j] = '0';
			}
		}
	}
	
	public void updateMap() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				statusMap[i][j] = '0';
			}
		}
		
		for (SMA_Vehicle v : vehicleList) {
			if (v.direct == 'H') {
				for (int i = 0; i < v.vehicleLen; i++) {
					statusMap[v.row][v.col + i] = v.vehicleTag;
				}
			} else {
				for (int i = 0; i < v.vehicleLen; i++) {
					statusMap[v.row + i][v.col] = v.vehicleTag;
				}
			}
		}
	}
	
	public void displayStatusMap() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				System.out.print(statusMap[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public boolean canMoveForward(int index) {
		SMA_Vehicle v = vehicleList.get(index);
		updateMap();
		
		if (v.direct == 'H') {
			if (v.col > 0 && statusMap[v.row][v.col - 1] == '0') {
				return true;
			} else {
				return false;
			}
		} else {			
			if (v.row > 0 && statusMap[v.row - 1][v.col] == '0') {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean moveForward(int index) {
		SMA_Vehicle v = vehicleList.get(index);
		
		if (v.direct == 'H') {
			if (v.col > 0 && statusMap[v.row][v.col - 1] == '0') {
				v.col--;
				vehicleList.set(index, v);
				updateMap();
				return true;
			} else {
				return false;
			}
		} else {
			if (v.row > 0 && statusMap[v.row - 1][v.col] == '0') {
				v.row--;
				vehicleList.set(index, v);
				updateMap();
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean canMoveBackward(int index) {
		SMA_Vehicle v = vehicleList.get(index);
		updateMap();
		
		if (v.direct == 'H') {
			if (v.col + v.vehicleLen - 1 < 5 && statusMap[v.row][v.col + v.vehicleLen] == '0') {
				return true;
			} else {
				return false;
			}
		} else {			
			if (v.row + v.vehicleLen - 1 < 5 && statusMap[v.row + v.vehicleLen][v.col] == '0') {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean moveBackward(int index) {
		SMA_Vehicle v = vehicleList.get(index);
		
		if (v.direct == 'H') {
			if (v.col + v.vehicleLen - 1 < 5 && statusMap[v.row][v.col + v.vehicleLen] == '0') {
				v.col++;
				vehicleList.set(index, v);
				updateMap();
				return true;
			} else {
				return false;
			}
		} else {
			if (v.row + v.vehicleLen - 1 < 5 && statusMap[v.row + v.vehicleLen][v.col] == '0') {
				v.row++;
				vehicleList.set(index, v);
				updateMap();
				return true;
			} else {
				return false;
			}
		}
	}
	
	public void copyFromOtherStatus(SMA_Status other) {
		this.vehicleList = new ArrayList<SMA_Vehicle>();
		
		for (SMA_Vehicle v : other.vehicleList) {
			SMA_Vehicle cur = new SMA_Vehicle(v.vehicleTag, v.direct, v.row, v.col, v.vehicleLen);
			this.vehicleList.add(cur);
		}
		
		this.parent = other.parent;
		this.next = other.next;
		this.fvalue = other.fvalue;
		this.gvalue = other.gvalue;
		this.hvalue = other.hvalue;
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				this.statusMap[i][j] = other.statusMap[i][j];
			}
		}
	}
	
	@Override
	public int hashCode() {
		return this.hvalue;
	}

	@Override
	public boolean equals(Object obj) {
		Boolean flag = true;
		
		if (obj instanceof SMA_Status) {
			SMA_Status p = (SMA_Status) obj;
			
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 6; j++) {
					if (p.statusMap[i][j] != this.statusMap[i][j]) {
						return false;
					}
				}
			}
		} else {
			flag = false;
		}

		return flag;
	}
}
