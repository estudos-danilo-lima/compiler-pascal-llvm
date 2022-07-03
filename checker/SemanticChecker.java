package checker;

import static ast.NodeKind.ASSIGN_NODE;
import static ast.NodeKind.BLOCK_NODE;
import static ast.NodeKind.BOOL_VAL_NODE;
import static ast.NodeKind.EQ_NODE;
import static ast.NodeKind.IF_NODE;
import static ast.NodeKind.INT_VAL_NODE;
import static ast.NodeKind.LT_NODE;
import static ast.NodeKind.MINUS_NODE;
import static ast.NodeKind.OVER_NODE;
import static ast.NodeKind.PLUS_NODE;
import static ast.NodeKind.PROGRAM_NODE;
import static ast.NodeKind.READ_NODE;
import static ast.NodeKind.REAL_VAL_NODE;
import static ast.NodeKind.REPEAT_NODE;
import static ast.NodeKind.STR_VAL_NODE;
import static ast.NodeKind.TIMES_NODE;
import static ast.NodeKind.VAR_DECL_NODE;
import static ast.NodeKind.VAR_LIST_NODE;
import static ast.NodeKind.VAR_USE_NODE;
import static ast.NodeKind.WRITE_NODE;
import static typing.Conv.I2R;
import static typing.Type.BOOL_TYPE;
import static typing.Type.INT_TYPE;
import static typing.Type.NO_TYPE;
import static typing.Type.REAL_TYPE;
import static typing.Type.STR_TYPE;

import ast.AST;
import org.antlr.v4.runtime.Token;
import tables.StrTable;
import tables.VarTable;

import typing.Conv;
import typing.Conv.Unif;
import typing.Type;




/*
 * Analisador semântico de EZLang implementado como um visitor
 * da ParseTree do ANTLR. A classe EZParserBaseVisitor é gerada
 * automaticamente e já possui métodos padrão aonde o comportamento
 * é só visitar todos os filhos. Por conta disto, basta sobreescrever
 * os métodos que a gente quer alterar. Neste caso, todos foram sobreescritos.
 *
 * No laboratório anterior, foi usado Type no tipo genérico do
 * EZParserBaseVisitor porque a gente só estava fazendo uma verificação
 * simples dos tipos primitivos. Agora o tipo declarado é AST, pois o
 * analisador semântico também realiza a construção da AST na mesma passada.
 * Assim, se a análise semântica (uso de variáveis e tipos) terminar sem erros,
 * então temos no final uma AST que representa o programa de entrada.
 * Em linguagens mais complexas é provável que sejam necessárias mais passadas,
 * por exemplo, uma para análise semântica e outra para a construção da AST.
 * Neste caso, talvez você tenha de implementar dois visitadores diferentes.
 *
 * Lembre que o caminhamento pela Parse Tree é top-down. Assim, é preciso sempre
 * visitar os filhos de um nó primeiro para construir as subárvores dos filhos.
 * No Bison isso já acontecia automaticamente porque o parsing lá é bottom-up e
 * as ações semânticas do parser já faziam a construção da AST junto com a análise
 * sintática. Aqui, é o inverso, por isso temos que visitar os filhos primeiro.
 */
public class SemanticChecker extends pascalBaseVisitor<AST> {

	private StrTable st = new StrTable();   // Tabela de strings.
    private VarTable vt = new VarTable();   // Tabela de variáveis.

    Type lastDeclType;  // Variável "global" com o último tipo declarado.

    AST root; // Nó raiz da AST sendo construída.

    // Testa se o dado token foi declarado antes.
    // Se sim, cria e retorna um nó de 'var use'.
    AST checkVar(Token token) {
    	String text = token.getText();
    	int line = token.getLine();
   		int idx = vt.lookupVar(text);
    	if (idx == -1) {
    		System.err.printf("SEMANTIC ERROR (%d): variable '%s' was not declared.\n", line, text);
    		// A partir de agora vou abortar no primeiro erro para facilitar.
    		System.exit(1);
            return null; // Never reached.
        }
    	return new AST(VAR_USE_NODE, idx, vt.getType(idx));
    }

    // Cria uma nova variável a partir do dado token.
    // Retorna um nó do tipo 'var declaration'.
    AST newVar(Token token) {
    	String text = token.getText();
    	int line = token.getLine();
   		int idx = vt.lookupVar(text);
        if (idx != -1) {
        	System.err.printf("SEMANTIC ERROR (%d): variable '%s' already declared at line %d.\n", line, text, vt.getLine(idx));
        	// A partir de agora vou abortar no primeiro erro para facilitar.
        	System.exit(1);
            return null; // Never reached.
        }
        idx = vt.addVar(text, line, lastDeclType);
        return new AST(VAR_DECL_NODE, idx, lastDeclType);
    }

    // ----------------------------------------------------------------------------
    // Type checking and inference.

    private static void typeError(int lineNo, String op, Type t1, Type t2) {
    	System.out.printf("SEMANTIC ERROR (%d): incompatible types for operator '%s', LHS is '%s' and RHS is '%s'.\n",
    			lineNo, op, t1.toString(), t2.toString());
    	System.exit(1);
    }

    private static void checkBoolExpr(int lineNo, String cmd, Type t) {
        if (t != BOOL_TYPE) {
            System.out.printf("SEMANTIC ERROR (%d): conditional expression in '%s' is '%s' instead of '%s'.\n",
               lineNo, cmd, t.toString(), BOOL_TYPE.toString());
            System.exit(1);
        }
    }

    // ----------------------------------------------------------------------------

    // Exibe o conteúdo das tabelas em stdout.
    void printTables() {
        System.out.print("\n\n");
        System.out.print(st);
        System.out.print("\n\n");
    	System.out.print(vt);
    	System.out.print("\n\n");
    }

    // Exibe a AST no formato DOT em stderr.
    void printAST() {
    	AST.printDot(root, vt);
    }

    // ----------------------------------------------------------------------------
    // Visitadores.
	

	//Pegar visitadores que o CP2 pede

    @Override 
    public AST visitProgram(pascalParser.ProgramContext ctx) {
        AST programHeading = visit(ctx.programHeading());
        AST block          = visit(ctx.block());
        this.root = AST.newSubtree(PROGRAM_NODE, NO_TYPE, programHeading, block);
        return this.root;
    }

    @Override
    public AST visitProgramHeading(pascalParser.ProgramHeadingContext ctx) {
        
        AST identifier = visit(ctx.identifier());
        
        AST identifierList = AST.newSubtree(IDENTIFIER_LIST_NODE, NO_TYPE);

        for (int i = 0; i <ctx.indentifierList().size(); i++){
            AST child = visit(ctx.indentifierList(i));
            identifierList.addChild(child);
        }
        
        this.root = AST.newSubtree(PROGRAM_HEADING_NODE, NO_TYPE, identifier, identifierList);
        return this.root; 
    }

    @Override
    public AST visitIdentifier(pascalParser.IdentifierContext ctx) {
        this.root = AST.newSubtree(IDENTIFIER, NO_TYPE);
        return newVar(ctx.IDENT().getSymbol());
        //return this.root; 
    }
	

	@Override 
    public AST visitVariableDeclaration(pascalParser.VariableDeclarationContext ctx) { 
        visit(ctx.type_());
        AST node = AST.newSubtree(VAR_LIST_NODE, NO_TYPE);
    	for (int i = 0; i < ctx.identifierList().size(); i++) {
    		AST child = visit(ctx.identifierList(i));
            // Cria e retorna um nó para a variável.
    		node.addChild(child);
    	}
    	return node;
    }

    @Override 
    public AST visitBool_(pascalParser.Bool_Context ctx) { 
        this.lastDeclType = Type.BOOL_TYPE;
    	return null;
    }

    @Override 
    public AST visitUnsignedInteger(pascalParser.UnsignedIntegerContext ctx) { 
        this.lastDeclType = Type.INT_TYPE;
    	return null;
    }

	@Override 
    public AST visitUnsignedReal(pascalParser.UnsignedRealContext ctx) { 
        this.lastDeclType = Type.REAL_TYPE;
    	return null;
    }

    @Override 
    public AST visitString(pascalParser.StringContext ctx) { 
        this.lastDeclType = Type.STR_TYPE;
    	return null;
    }
    /*
	@Override 
    public AST visitProcedureAndFunctionDeclarationPart(pascalParser.ProcedureAndFunctionDeclarationPartContext ctx) { 
        return visitChildren(ctx); 
    }

	@Override 
    public AST visitProcedureOrFunctionDeclaration(pascalParser.ProcedureOrFunctionDeclarationContext ctx) {
        return visitChildren(ctx); 
    }

	@Override 
    public AST visitProcedureDeclaration(pascalParser.ProcedureDeclarationContext ctx) {
        return visitChildren(ctx); 
    }

    @Override 
    public T visitMultiplicativeoperator(pascalParser.MultiplicativeoperatorContext ctx) { 
        return visitChildren(ctx); 
    }
    
    @Override 
    public T visitAdditiveoperator(pascalParser.AdditiveoperatorContext ctx) { 
        return visitChildren(ctx); 
    }

    @Override 
    public T visitRelationaloperator(pascalParser.RelationaloperatorContext ctx) { 
        return visitChildren(ctx); 
    }

    @Override 
    public T visitAssignmentStatement(pascalParser.AssignmentStatementContext ctx) { 
        return visitChildren(ctx); 
    }

    @Override 
    public T visitIfStatement(pascalParser.IfStatementContext ctx) { 
        return visitChildren(ctx); 
    }

    @Override 
    public T visitWhileStatement(pascalParser.WhileStatementContext ctx) { 
        return visitChildren(ctx); 
    }

    @Override 
    public T visitForStatement(pascalParser.ForStatementContext ctx) { 
        return visitChildren(ctx); 
    }

    @Override 
    public T visitVariable(pascalParser.VariableContext ctx) {
          return visitChildren(ctx); 
    }*/
}