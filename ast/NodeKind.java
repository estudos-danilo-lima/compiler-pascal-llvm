package ast;

// Enumeração dos tipos de nós de uma AST.
// Adaptado da versão original em C.
// Algumas pessoas podem preferir criar uma hierarquia de herança para os
// nós para deixar o código "mais OO". Particularmente eu não sou muito
// fã, acho que só complica mais as coisas. Imagine uma classe abstrata AST
// com mais de 20 classes herdando dela, uma classe para cada tipo de nó...
public enum NodeKind {
	ASSIGN_NODE {
		public String toString() {
            return ":=";
        }
	},
    EQ_NODE {
		public String toString() {
            return "=";
        }
	},
    NOT_EQ_NODE {
        public String toString() {
            return "<>";
        }
    },
    BOOL_VAL_NODE {
		public String toString() {
            return "";
        }
	},
    IF_NODE {
		public String toString() {
            return "if";
        }
	},
    ELSE_NODE {
		public String toString() {
            return "else";
        }
	},
    INT_VAL_NODE {
		public String toString() {
            return "";
        }
	},
    LT_NODE {
		public String toString() {
            return "<";
        }
	},
    LE_NODE {
		public String toString() {
            return "<=";
        }
	},
    GT_NODE {
		public String toString() {
            return ">";
        }
	},
    GE_NODE {
		public String toString() {
            return ">=";
        }
	},
    MINUS_NODE {
		public String toString() {
            return "-";
        }
	},
    OVER_NODE {
		public String toString() {
            return "/";
        }
	},
    PLUS_NODE {
		public String toString() {
            return "+";
        }
	},
    PROGRAM_NODE {
		public String toString() {
            return "program";
        }
	},
    PROGRAM_HEADING_NODE {
		public String toString() {
            return "programHeading";
        }
	},
    BLOCK_NODE {
		public String toString() {
            return "block";
        }
	},
    PROC_FUNCT_DECL_PART_NODE {
		public String toString() {
            return "procedureAndFunctionDeclarationPart";
        }
	},
    PROC_FUNCT_DECL_NODE {
		public String toString() {
            return "procedureOrFunctionDeclaration";
        }
	},
    FUNC_DECL_NODE {
		public String toString() {
            return "functionDeclaration";
        }
	},
    IDENTIFIER_NODE {
		public String toString() {
            return "identifier";
        }
	},
    IDENTIFIER_LIST_NODE {
		public String toString() {
            return "identifierList";
        }
	},
    STATEMENT_LIST_NODE {
		public String toString() {
            return "statements";
        }
	},
    STATEMENT_NODE {
		public String toString() {
            return "statement";
        }
	},
    EXPRESSION_NODE {
		public String toString() {
            return "expresion";
        }
	},
    READ_NODE {
		public String toString() {
            return "read";
        }
	},
    REAL_VAL_NODE {
		public String toString() {
            return "";
        }
	},
    WHILE_NODE {
		public String toString() {
            return "while";
        }
	},
    STR_VAL_NODE {
		public String toString() {
            return "";
        }
	},
    TIMES_NODE {
		public String toString() {
            return "*";
        }
	},
    AND_NODE {
        public String toString() {
            return "AND";
        }
    },
    OR_NODE {
        public String toString() {
            return "OR";
        }
    },
    VAR_DECL_NODE {
		public String toString() {
            return "variableDeclaration";
        }
	},
    VAR_DECL_PART_NODE {
		public String toString() {
            return "variableDeclarationPart";
        }
	},
    VAR_USE_NODE {
		public String toString() {
            return "var_use";
        }
	},
    WRITE_NODE {
		public String toString() {
            return "write";
        }
	},

    B2I_NODE { // Type conversion.
		public String toString() {
            return "B2I";
        }
	},
    B2R_NODE {
		public String toString() {
            return "B2R";
        }
	},
    B2S_NODE {
		public String toString() {
            return "B2S";
        }
	},
    I2R_NODE {
		public String toString() {
            return "I2R";
        }
	},
    I2S_NODE {
		public String toString() {
            return "I2S";
        }
	},
    R2S_NODE {
		public String toString() {
            return "R2S";
        }
	};
	
	public static boolean hasData(NodeKind kind) {
		switch(kind) {
	        case BOOL_VAL_NODE:
	        case INT_VAL_NODE:
	        case REAL_VAL_NODE:
	        case STR_VAL_NODE:
	        case VAR_DECL_NODE:
	        case VAR_USE_NODE: 
	            return true;
	        default:
	            return false;
		}
	}
}
