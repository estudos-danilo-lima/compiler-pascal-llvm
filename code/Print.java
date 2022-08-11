package code;

import java.util.Comparator;

public enum Print {
	INT {
		public String toString() {
			return String.format("@%d = private constant [3 x i8] c\"%%d\\00\"\n", index);
		}
	},
	REAL {
		public String toString() {
			return String.format("@%d = private constant [4 x i8] c\"%%lf\\00\"\n", index);
		}
	},
	BOOL {
		public String toString() {
			return String.format("@%d = private constant [3 x i8] c\"%%d\\00\"\n", index);
		}
	},
	CHAR {
		public String toString() {
			return String.format("@%d = private constant [3 x i8] c\"%%c\\00\"\n", index);
		}
	},
	STR {
		public String toString() {
			return String.format("@%d = private constant [3 x i8] c\"%%s\\00\"\n", index);
		}
	};

	public int index;

	public Print setIndex(int idx) {
		this.index = idx;
		return this;
	}
}

class PrintComparator implements Comparator<Print> {
	public int compare(Print p1, Print p2) {
		return p1.index - p2.index;
	}
}