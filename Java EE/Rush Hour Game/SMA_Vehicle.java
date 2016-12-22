
public class SMA_Vehicle {
	char vehicleTag;   // vehicle type
	char direct;       // moving direct
	int row;           // row-coordinate
	int col;           // column-coordinate
	int vehicleLen;    // vehicle length
	
	public SMA_Vehicle(char vehicleTag, char direct, int row, int col, int vehicleLen) {
		this.vehicleTag = vehicleTag;
		this.direct = direct;
		this.row = row;
		this.col = col;
		this.vehicleLen = vehicleLen;
	}
	
	@Override
	public boolean equals(Object obj) {
		Boolean flag = true;
		
		if (obj instanceof SMA_Vehicle) {
			SMA_Vehicle p = (SMA_Vehicle) obj;
			
			if (p.vehicleTag != this.vehicleTag || p.row != this.row || p.col != this.col) {
				flag = false;
			}
		} else {
			flag = false;
		}

		return flag;
	}
}
