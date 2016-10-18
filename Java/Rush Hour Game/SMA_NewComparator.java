import java.util.*;

public class SMA_NewComparator implements Comparator<SMA_Status> {
	@Override
	public int compare(SMA_Status o1, SMA_Status o2) {
		if (o1.fvalue > o2.fvalue) {
			return 1;
		} else if (o1.fvalue < o2.fvalue) {
			return -1;
		} else {
			return 0;
		}
	}
}
