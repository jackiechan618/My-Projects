import java.util.*;

/*********************************************************************************************************
 * Using SMA* algorithm to solve Rush Hour Game
 * 
 * Classes: this project contains 4 classes, the class SMA_Problem1_RushHour_Main which is the main class, 
 * 			class SMA_Status which used to keep the information of each status,
 * 			class SMA_Vehicle which used to keep the information of each vehicle,
 * 	 		and class SMA_NewComparator. 
 * Input:   start status and target status.
 * Output:  each statuses and the path from start status to target status.
 * 
 * The input to the program is a list of n-tuples, one per vehicle, each with 4 characters: VXYZ.
 * 		(1). V indicates the type: (C)ar, (B)uses or (I)ce-cream truck).
 * 		(2). X indicates the direction: (H)orizontal or (V)ertical.
 * 		(3). Y indicates the row: A : : : F.
 * 		(4). Z indicated the column: 1 : : : 6. 
 * 
 **********************************************************************************************************/

public class SMA_Problem1_RushHour_Main {
	private SMA_Status startStatus = new SMA_Status();
	private SMA_Status targetStatus = new SMA_Status();
	private List<SMA_Status> openStatusList = new ArrayList<SMA_Status>();
	private List<SMA_Status> closeStatusList = new ArrayList<SMA_Status>();
	private List<SMA_Status> springStatusList = new ArrayList<SMA_Status>();

	public void init() {
		Scanner io = new Scanner(System.in);

		System.out.println("Please input the start status: [for example: IHC3 CVA3 BHB4 CVC5 CVE5]");
		String inputLine1 = io.nextLine();
		String[] vehiclesInfoArray1 = inputLine1.split("\\s{1,}");

		System.out.println("Please input the target status: [for example: IHC5 CVD3 BHB2 CVA5 CVE5]");
		String inputLine2 = io.nextLine();
		String[] vehiclesInfoArray2 = inputLine2.split("\\s{1,}");

		Map<Character, Integer> vehicleLenMap = new HashMap<Character, Integer>();
		vehicleLenMap.put('I', 2);
		vehicleLenMap.put('B', 3);
		vehicleLenMap.put('C', 2);

		for (String info : vehiclesInfoArray1) {
			int vehicleLen = vehicleLenMap.get(info.charAt(0));
			int rowIndex = info.charAt(2) - 'A';
			int colIndex = info.charAt(3) - '0' - 1;
			char vehicleTag = info.charAt(0);
			char direct = info.charAt(1);

			SMA_Vehicle v = new SMA_Vehicle(vehicleTag, direct, rowIndex, colIndex, vehicleLen);
			startStatus.vehicleList.add(v);
		}

		for (String info : vehiclesInfoArray2) {
			int vehicleLen = vehicleLenMap.get(info.charAt(0));
			int rowIndex = info.charAt(2) - 'A';
			int colIndex = info.charAt(3) - '0' - 1;
			char vehicleTag = info.charAt(0);
			char direct = info.charAt(1);

			SMA_Vehicle v = new SMA_Vehicle(vehicleTag, direct, rowIndex, colIndex, vehicleLen);
			targetStatus.vehicleList.add(v);
		}

		startStatus.hvalue = getHvalue(startStatus);
		startStatus.fvalue = startStatus.hvalue + startStatus.gvalue;
		openStatusList.add(0, startStatus);
		targetStatus.hvalue = getHvalue(targetStatus);

		System.out.println();
		System.out.println("*******************************************************");
		System.out.println("Start Status:");	
		startStatus.updateMap();
		startStatus.displayStatusMap();
		
		System.out.println();

		System.out.println("Target Status:");
		targetStatus.updateMap();
		targetStatus.displayStatusMap();
		
		System.out.println();
	}

	
	// Get the path
	public void getPath(SMA_Status status) {
		int deepnum = status.gvalue;

		if (status.parent != null) {
			getPath(status.parent);
		}

		System.out.println("The status of level [" + deepnum + "] is:");
		deepnum--;
		status.displayStatusMap();
		System.out.println();
	}

	
	// Add a status into the open list
	public void add(SMA_Status status, List<SMA_Status> list) {
		list.add(status);
		Collections.sort(list, new SMA_NewComparator());
	}

	
	// Get the succeed status
	public void getNexts(SMA_Status curStatus) {
		for (int i = 0; i < curStatus.vehicleList.size(); i++) {			
			if (curStatus.canMoveForward(i) == true) {
				getShift(curStatus, i, 1);
			}

			if (curStatus.canMoveBackward(i) == true) {
				getShift(curStatus, i, -1);
			}
		}
	}

	
	// Move to get the succeed status
	public void getShift(SMA_Status curStatus, int index, int flag) {
		SMA_Status tempStatus = new SMA_Status();
		tempStatus.copyFromOtherStatus(curStatus);
		SMA_Vehicle v = tempStatus.vehicleList.get(index);

		if (flag == 1 && tempStatus.canMoveForward(index)) {
			tempStatus.moveForward(index);
			
			if (!hasAnceSameStatus(tempStatus, curStatus.parent)) {				
				tempStatus.gvalue = curStatus.gvalue + 1;
				tempStatus.hvalue = getHvalue(tempStatus);
				tempStatus.fvalue = tempStatus.gvalue + tempStatus.hvalue;
				tempStatus.parent = curStatus;
				tempStatus.next = null;
				springStatusList.add(0, tempStatus); 
			} 
		} else if (flag == -1 && tempStatus.canMoveBackward(index)) {
			tempStatus.moveBackward(index);
	
			if (!hasAnceSameStatus(tempStatus, curStatus.parent)) {				
				tempStatus.gvalue = curStatus.gvalue + 1;
				tempStatus.hvalue = getHvalue(tempStatus);
				tempStatus.fvalue = tempStatus.gvalue + tempStatus.hvalue;
				tempStatus.parent = curStatus;
				tempStatus.next = null;
				springStatusList.add(0, tempStatus); 
			} 
		}
	}

	
	// Get the H value
	public int getHvalue(SMA_Status status) {
		int num = 0;

		for (int i = 0; i < status.vehicleList.size(); i++) {
			SMA_Vehicle v1 = status.vehicleList.get(i);
			SMA_Vehicle v2 = targetStatus.vehicleList.get(i);

			if (!v1.equals(v2)) {
				if (v1.direct == 'H') {
					num += Math.abs(v1.col - v2.col);
				} else {
					num += Math.abs(v1.row - v2.row);
				}
			}
		}

		status.hvalue = num;
		return status.hvalue;
	}

	
	// Check whether the status is the same as it ancester
	public Boolean hasAnceSameStatus(SMA_Status origin, SMA_Status ancester) {
		boolean flag = false;

		while (ancester != null) {
			if (hasSameStatus(origin, ancester)) {
				flag = true;
				return flag;
			}

			ancester = ancester.parent;
		}

		return flag;
	}

	
	// Check whether two status have the same status
	public Boolean hasSameStatus(SMA_Status s1, SMA_Status s2) {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (s1.statusMap[i][j] != s2.statusMap[i][j]) {
					return false;
				}
			}
		}

		return true;
	}
	
	
	// Reslove the problem
	public void reslove() {
		int numcount = 1;
		SMA_Status getOfOpen = null;
		boolean flag = false;
		init();
		
		System.out.println();
		System.out.println("*******************************************************");
		System.out.println("Value of visited status:");

		while (!openStatusList.isEmpty()) {
			getOfOpen = openStatusList.get(0);
			closeStatusList.add(getOfOpen);
			openStatusList.remove(0);

			System.out.println("Status [" + numcount++ + "]:");
			
			getOfOpen.displayStatusMap();
			System.out.println();
			
			if (hasSameStatus(getOfOpen, targetStatus)) {
				flag = true;
				break;
			}

			getNexts(getOfOpen);

			while (!springStatusList.isEmpty()) {
				SMA_Status curStatus = springStatusList.get(0);

				if (openStatusList.contains(curStatus)) {
					SMA_Status statusInOpen = openStatusList.get(openStatusList.indexOf(curStatus));

					if (curStatus.gvalue < statusInOpen.gvalue) {
						statusInOpen.parent = curStatus.parent;
						statusInOpen.fvalue = curStatus.fvalue;
						statusInOpen.gvalue = curStatus.gvalue;
						Collections.sort(openStatusList, new SMA_NewComparator());
					}

					springStatusList.remove(curStatus);
				} else if (closeStatusList.contains(curStatus)) {
					SMA_Status statusInClosed = closeStatusList.get(closeStatusList.indexOf(curStatus));
					
					if (curStatus.gvalue < statusInClosed.gvalue) {
						statusInClosed.parent = curStatus.parent;
						statusInClosed.fvalue = curStatus.fvalue;
						statusInClosed.gvalue = curStatus.gvalue;
						add(statusInClosed, openStatusList);
					}
					springStatusList.remove(curStatus);
				} else {
					add(curStatus, openStatusList);
					springStatusList.remove(curStatus);
				}
			}
		}
		
		if (flag) {
			System.out.println("*******************************************************");
			System.out.println("The length of the path is: " + getOfOpen.gvalue);
			System.out.println();
			getPath(getOfOpen);
		}
	}
	
	
	// Main function
	public static void main(String[] args) {
		SMA_Problem1_RushHour_Main t = new SMA_Problem1_RushHour_Main();
		t.reslove();
	}
}
