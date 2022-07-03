package typing;

import static typing.Conv.B2I;
import static typing.Conv.B2R;
import static typing.Conv.B2S;
import static typing.Conv.I2R;
import static typing.Conv.I2S;
import static typing.Conv.NONE;
import static typing.Conv.R2S;

import typing.Conv.Unif;

// Enumeração dos tipos primitivos que podem existir em EZLang.
public enum Type {
	INT_TYPE {
		public String toString() {
            return "int";
        }
	},
    REAL_TYPE {
		public String toString() {
			return "real";
		}
	},
    BOOL_TYPE {
		public String toString() {
            return "bool";
        }
	},
    STR_TYPE {
		public String toString() {
            return "string";
        }
	},
	NO_TYPE { // Indica um erro de tipos.
		public String toString() {
            return "no_type";
        }
	};

	// Tabela de unificação de tipos primitivos para o operador '+'.
	private static Unif plus[][] ={
		{ new Unif(INT_TYPE, NONE, NONE), new Unif(REAL_TYPE, I2R, NONE),  new Unif(INT_TYPE, NONE, B2I),   new Unif(STR_TYPE, I2S, NONE)  },
		{ new Unif(REAL_TYPE, NONE, I2R), new Unif(REAL_TYPE, NONE, NONE), new Unif(REAL_TYPE, NONE, B2R),  new Unif(STR_TYPE, R2S, NONE)  },
		{ new Unif(INT_TYPE, B2I, NONE),  new Unif(REAL_TYPE, B2R, NONE),  new Unif(BOOL_TYPE, NONE, NONE), new Unif(STR_TYPE, B2S, NONE)  },
		{ new Unif(STR_TYPE, NONE, I2S),  new Unif(STR_TYPE, NONE, R2S),   new Unif(STR_TYPE, NONE, B2S),   new Unif(STR_TYPE, NONE, NONE) }
	};
	
	public Unif unifyPlus(Type that) {
		return plus[this.ordinal()][that.ordinal()];
	}
	
	// Tabela de unificação de tipos primitivos para os demais operadores aritméticos.
	private static Unif other[][] = {
		{ new Unif(INT_TYPE, NONE, NONE), new Unif(REAL_TYPE, I2R, NONE),  new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) },
		{ new Unif(REAL_TYPE, NONE, I2R), new Unif(REAL_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) },
		{ new Unif(NO_TYPE, NONE, NONE),  new Unif(NO_TYPE, NONE, NONE),   new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) },
		{ new Unif(NO_TYPE, NONE, NONE),  new Unif(NO_TYPE, NONE, NONE),   new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE) }
	};

	public Unif unifyOtherArith(Type that) {
	    return other[this.ordinal()][that.ordinal()];
	}

	// Tabela de unificação de tipos primitivos para os operadores de comparação.
	private static Unif comp[][] = {
		{ new Unif(BOOL_TYPE, NONE, NONE), new Unif(BOOL_TYPE, I2R, NONE),  new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE)   },
		{ new Unif(BOOL_TYPE, NONE, I2R),  new Unif(BOOL_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE)   },
		{ new Unif(NO_TYPE, NONE, NONE),   new Unif(NO_TYPE, NONE, NONE),   new Unif(NO_TYPE, NONE, NONE), new Unif(NO_TYPE, NONE, NONE)   },
		{ new Unif(NO_TYPE, NONE, NONE),   new Unif(NO_TYPE, NONE, NONE),   new Unif(NO_TYPE, NONE, NONE), new Unif(BOOL_TYPE, NONE, NONE) }
	};

	public Unif unifyComp(Type that) {
		return comp[this.ordinal()][that.ordinal()];
	}
}
