package ast;

/*
 * Classe abstrata que define a interface do visitor para a AST.
 * Implementa o despacho do método 'visit' conforme o 'kind' do nó.
 * Com isso, basta herdar desta classe para criar um interpretador
 * ou gerador de código.
 */
public abstract class ASTBaseVisitor<T> {

	// Único método público. Começa a visita a partir do nó raiz
	// passado. Precisa ter outro nome porque tem a mesma assinatura
	// que o método "genérico" 'visit'.
	public void execute(AST root) {
		visit(root);
	}
	
	// Método "genérico" que despacha a visitação para os métodos
	// especializados conforme o 'kind' do nó atual. Igual ao código
	// em C. Novamente fica o argumento sobre usar OO ou não aqui.
	// Se tivéssemos trocentas classes especializando o nó da AST
	// esse despacho seria feito pela JVM. Aqui precisa fazer na mão.
	// Por outro lado, assim não precisa de trocentas classes com o
	// código todo espalhado entre elas...
	protected T visit(AST node) {
		switch(node.kind) {
			case PROGRAM_NODE:  		return visitProgram(node);
			case PROGRAM_HEADING_NODE:	return visitProgramHeading(node);
			case IDENTIFIER_NODE:		return visitIdentifier(node);
	        case BLOCK_NODE:    		return visitBlock(node);
	        case VAR_DECL_PART_NODE:	return visitVarDeclPart(node);
	        case IDENTIFIER_LIST_NODE:	return visitIdentifierList(node);
	        case STATEMENT_LIST_NODE:  	return visitStatementList(node);
			case PROCEDURE_DESIGN_NODE: return visitProcedureDesignator(node);
			case FUNC_IDENT_NODE: 		return visitFuncIdentifier(node);
			case PARAMETER_LIST_NODE: 	return visitParameterList(node);
	        case ASSIGN_NODE:   		return visitAssign(node);

	        case IF_NODE:       		return visitIf(node);
			case ELSE_NODE:       		return visitElse(node);
	        case EQ_NODE:       		return visitEq(node);
			case GT_NODE:       		return visitGt(node);
	        case LT_NODE:       		return visitLt(node);
	        case REPEAT_NODE:			return visitRepeat(node);

	        case MINUS_NODE:    		return visitMinus(node);
	        case OVER_NODE:     		return visitOver(node);
	        case PLUS_NODE:     		return visitPlus(node);
	        case TIMES_NODE:    		return visitTimes(node);

	        case VAR_USE_NODE:  		return visitVarUse(node);
	
	        case INT_VAL_NODE:  		return visitIntVal(node);
			case STR_VAL_NODE:  		return visitStrVal(node);
			case BOOL_VAL_NODE: 		return visitBoolVal(node);
			case REAL_VAL_NODE: 		return visitRealVal(node);

	        case B2I_NODE:      		return visitB2I(node);
	        case B2R_NODE:      		return visitB2R(node);
	        case B2S_NODE:      		return visitB2S(node);
	        case I2R_NODE:      		return visitI2R(node);
	        case I2S_NODE:      		return visitI2S(node);
	        case R2S_NODE:      		return visitR2S(node);
	
	        default:
	            System.err.printf("Invalid kind: %s!\n", node.kind.toString());
	            System.exit(1);
	            return null;
		}
	}
	
	// Métodos especializados para visitar um nó com um certo 'kind'.

	protected abstract T visitProgram(AST node);

	protected abstract T visitProgramHeading(AST node);

	protected abstract T visitIdentifier(AST node);

	protected abstract T visitBlock(AST node);

	protected abstract T visitVarDeclPart(AST node);

	protected abstract T visitIdentifierList(AST node);

	protected abstract T visitStatementList(AST node);

	protected abstract T visitProcedureDesignator(AST node);

	protected abstract T visitFuncIdentifier(AST node);

	protected abstract T visitParameterList(AST node);

	protected abstract T visitAssign(AST node);

	protected abstract T visitIf(AST node);

	protected abstract T visitElse(AST node);

	protected abstract T visitEq(AST node);

	protected abstract T visitGt(AST node);

	protected abstract T visitLt(AST node);

	protected abstract T visitRepeat(AST node);

	protected abstract T visitMinus(AST node);

	protected abstract T visitOver(AST node);

	protected abstract T visitPlus(AST node);

	protected abstract T visitTimes(AST node);

	protected abstract T visitVarUse(AST node);

	protected abstract T visitIntVal(AST node);

	protected abstract T visitStrVal(AST node);

	protected abstract T visitBoolVal(AST node);

	protected abstract T visitRealVal(AST node);

	protected abstract T visitB2I(AST node);

	protected abstract T visitB2R(AST node);

	protected abstract T visitB2S(AST node);

	protected abstract T visitI2R(AST node);

	protected abstract T visitI2S(AST node);

	protected abstract T visitR2S(AST node);
	
}
