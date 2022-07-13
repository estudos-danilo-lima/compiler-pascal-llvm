package checker;

import static ast.NodeKind.ASSIGN_NODE;
import static ast.NodeKind.BLOCK_NODE;
import static ast.NodeKind.BOOL_VAL_NODE;
import static ast.NodeKind.EQ_NODE;
import static ast.NodeKind.NOT_EQ_NODE;
import static ast.NodeKind.IF_NODE;
import static ast.NodeKind.ELSE_NODE;
import static ast.NodeKind.INT_VAL_NODE;
import static ast.NodeKind.LT_NODE;
import static ast.NodeKind.LE_NODE;
import static ast.NodeKind.GT_NODE;
import static ast.NodeKind.GE_NODE;
import static ast.NodeKind.MINUS_NODE;
import static ast.NodeKind.OVER_NODE;
import static ast.NodeKind.PLUS_NODE;
import static ast.NodeKind.PROGRAM_NODE;
import static ast.NodeKind.READ_NODE;
import static ast.NodeKind.REAL_VAL_NODE;
import static ast.NodeKind.WHILE_NODE;
import static ast.NodeKind.STR_VAL_NODE;
import static ast.NodeKind.TIMES_NODE;
import static ast.NodeKind.VAR_DECL_NODE;
import static ast.NodeKind.VAR_DECL_PART_NODE;
import static ast.NodeKind.VAR_USE_NODE;
import static ast.NodeKind.WRITE_NODE;
import static ast.NodeKind.AND_NODE;
import static ast.NodeKind.OR_NODE;
import static typing.Conv.I2R;
import static typing.Type.BOOL_TYPE;
import static typing.Type.INT_TYPE;
import static typing.Type.NO_TYPE;
import static typing.Type.REAL_TYPE;
import static typing.Type.STR_TYPE;
import static typing.Type.ARRAY_TYPE;

import ast.AST;
import ast.NodeKind;
import parser.pascalBaseVisitor;
import parser.pascalParser;
import parser.pascalParser.SimpleExpressionContext;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

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

    Type lastDeclType = NO_TYPE;  // Variável "global" com o último tipo declarado.

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
        return new AST(NodeKind.VAR_DECL_NODE, idx, lastDeclType);
    }

    private static AST checkAssign(int lineNo, AST l, AST r) {
    	Type lt = l.type;
    	Type rt = r.type;

        if (lt == BOOL_TYPE && rt != BOOL_TYPE) typeError(lineNo, ":=", lt, rt);
        if (lt == STR_TYPE  && rt != STR_TYPE)  typeError(lineNo, ":=", lt, rt);
        if (lt == INT_TYPE  && rt != INT_TYPE)  typeError(lineNo, ":=", lt, rt);

        if (lt == REAL_TYPE) {
        	if (rt == INT_TYPE) {
        		r = Conv.createConvNode(I2R, r);
        	} else if (rt != REAL_TYPE) {
        		typeError(lineNo, ":=", lt, rt);
            }
        }

        return AST.newSubtree(ASSIGN_NODE, NO_TYPE, l, r);
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
        this.root = AST.newSubtree(NodeKind.PROGRAM_NODE, NO_TYPE, programHeading, block);
        return this.root;
    }

    @Override
    public AST visitProgramHeading(pascalParser.ProgramHeadingContext ctx) {
        AST node = AST.newSubtree(NodeKind.PROGRAM_HEADING_NODE, NO_TYPE);

        AST identifier = visit(ctx.identifier());

        node.addChild(identifier);
        
        if(ctx.identifierList() != null){
            AST identifierList = visit(ctx.identifierList());

            node.addChild(identifierList);
        }
        return node;
    }

    @Override public AST visitBlock(pascalParser.BlockContext ctx) { 
        AST node = AST.newSubtree(NodeKind.BLOCK_NODE, NO_TYPE);
        //AST procedureAndFunctionDeclarationPart = AST.newSubtree(NodeKind.FUNC_LIST_NODE, NO_TYPE);
        /*for(var funcList : ctx.procedureAndFunctionDeclarationPart()){
            if (funcList.procedureOrFunctionDeclaration() != null){
                procedureAndFunctionDeclarationPart.addChild(visit(funcList.procedureOrFunctionDeclaration()));
            }
        }*/

        for (int i = 0; i < ctx.procedureAndFunctionDeclarationPart().size(); i++) {
            // Visita um por um, com o 0 sendo o primeiro (fora do fecho), e
            // os demais dentro do fecho.
            AST child = visit(ctx.procedureAndFunctionDeclarationPart(i));
            node.addChild(child);
        }

        if (ctx.variableDeclarationPart().size() > 0){
            node.addChild(visit(ctx.variableDeclarationPart(0)));
        }

        if (ctx.compoundStatement().statements().statement().size() > 1){
            node.addChild(visit(ctx.compoundStatement().statements()));
        }
        
        return node;
    }

    @Override public AST visitProcedureAndFunctionDeclarationPart(pascalParser.ProcedureAndFunctionDeclarationPartContext ctx) {
        return visit(ctx.procedureOrFunctionDeclaration().functionDeclaration());
    }

    @Override public AST visitFunctionDeclaration(pascalParser.FunctionDeclarationContext ctx) {

        visit(ctx.resultType());

        AST identifier = visit(ctx.identifier());

        AST parameterList = null;
        if(ctx.formalParameterList() != null){
            parameterList = visit(ctx.formalParameterList());
        }

        AST block = visit(ctx.block());


        if(ctx.formalParameterList() != null){
            return AST.newSubtree(NodeKind.FUNCTION_NODE, NO_TYPE, identifier, parameterList, block);
        }

        return AST.newSubtree(NodeKind.FUNCTION_NODE, NO_TYPE, identifier, block);
    }

    @Override public AST visitFormalParameterList(pascalParser.FormalParameterListContext ctx) {
        AST node = AST.newSubtree(NodeKind.PARAMETER_LIST_NODE, NO_TYPE);
        // Basta pegar o size do formalParameterSection pra saber quantos tem.
        for (int i = 0; i < ctx.formalParameterSection().size(); i++) {
            // Visita um por um, com o 0 sendo o primeiro (fora do fecho), e
            // os demais dentro do fecho.
            AST child = visit(ctx.formalParameterSection(i).parameterGroup());
            node.addChild(child);
        }
        // Aqui deveria retornar um nó da AST.
        return node;
    }

    @Override public AST visitParameterGroup(pascalParser.ParameterGroupContext ctx) {
        
        visit(ctx.typeIdentifier());

        return visit(ctx.identifierList());
    }

    @Override public AST visitVariableDeclarationPart(pascalParser.VariableDeclarationPartContext ctx) { 
        AST node = AST.newSubtree(NodeKind.VAR_DECL_PART_NODE, NO_TYPE);
        for (int i = 0; i < ctx.variableDeclaration().size(); i++) {
            // Visita um por um, com o 0 sendo o primeiro (fora do fecho), e
            // os demais dentro do fecho.
            node.addChild(visit(ctx.variableDeclaration(i)));
        }
        return node;
    }
    
	@Override 
    public AST visitVariableDeclaration(pascalParser.VariableDeclarationContext ctx) {
        //Reset
        this.lastDeclType = NO_TYPE;
        AST type = visit(ctx.type_());

        AST node = visit(ctx.identifierList());

        if(type != null){
            node.addChild(type);
        }

    	return node;
    }

    @Override
    public AST visitIdentifierList(pascalParser.IdentifierListContext ctx) {
        AST node = AST.newSubtree(NodeKind.IDENTIFIER_LIST_NODE, NO_TYPE);
        // Basta pegar o size do identifier pra saber quantos tem.
        for (int i = 0; i < ctx.identifier().size(); i++) {
            // Visita um por um, com o 0 sendo o primeiro (fora do fecho), e
            // os demais dentro do fecho.
            AST child = visit(ctx.identifier(i));
            node.addChild(child);
        }
        // Aqui deveria retornar um nó da AST.
        return node;
    }

    @Override
    public AST visitIdentifier(pascalParser.IdentifierContext ctx) {
        AST node;
        System.out.println(ctx.IDENT().getSymbol());
        if (this.lastDeclType == NO_TYPE){
            node = AST.newSubtree(NodeKind.IDENTIFIER_NODE, NO_TYPE);
        }
        else{
            node = newVar(ctx.IDENT().getSymbol());
        }
        return node;
    }

    @Override 
    public AST visitBoolType(pascalParser.BoolTypeContext ctx) { 
        this.lastDeclType = Type.BOOL_TYPE;
    	return null;
    }

	@Override 
    public AST visitIntType(pascalParser.IntTypeContext ctx) { 
        this.lastDeclType = Type.INT_TYPE;
    	return null;
    }

	@Override 
    public AST visitRealType(pascalParser.RealTypeContext ctx) { 
        this.lastDeclType = Type.REAL_TYPE;
    	return null;
    }

    @Override public AST visitArrayType(pascalParser.ArrayTypeContext ctx) {

        visit(ctx.componentType().type_());

        return visit(ctx.typeList().indexType(0).simpleType().subrangeType());
    }

    @Override public AST visitSubrangeType(pascalParser.SubrangeTypeContext ctx) {
        AST left = visit(ctx.constant(0).unsignedNumber());
        AST right = visit(ctx.constant(1).unsignedNumber());

        return AST.newSubtree(NodeKind.SUBRANGE_NODE, NO_TYPE, left, right);
    }

	@Override public AST visitStringType(pascalParser.StringTypeContext ctx) {
        this.lastDeclType = Type.STR_TYPE;
    	return null; 
    }

    @Override public AST visitStatements(pascalParser.StatementsContext ctx) {
        AST node = AST.newSubtree(NodeKind.STATEMENT_LIST_NODE, NO_TYPE);

        for (int i = 0; i < ctx.statement().size()-1; i++) {
            // Visita um por um, com o 0 sendo o primeiro (fora do fecho), e
            // os demais dentro do fecho.
            AST child = visit(ctx.statement(i));
            node.addChild(child);
        }

        return node;
    }

    @Override public AST visitStatement(pascalParser.StatementContext ctx) {
        return visit(ctx.unlabelledStatement());
    }

    
    @Override public AST visitUnlabelledStatement(pascalParser.UnlabelledStatementContext ctx) {
        if (ctx.structuredStatement() != null)
            return visit(ctx.structuredStatement());
        else
            return visit(ctx.simpleStatement());
    }
    
    @Override public AST visitStructuredStatement(pascalParser.StructuredStatementContext ctx) {
        if(ctx.compoundStatement() != null){
            return visit(ctx.compoundStatement());
        }
        else if(ctx.conditionalStatement() != null){
            return visit(ctx.conditionalStatement());
        }
        else if(ctx.repetetiveStatement() != null){
            return visit(ctx.repetetiveStatement());
        }
        return null;
    }

    @Override public AST visitCompoundStatement(pascalParser.CompoundStatementContext ctx) {
        return visit(ctx.statements());
    }

    @Override public AST visitSimpleStatement(pascalParser.SimpleStatementContext ctx) {
        return visit(ctx.assignmentStatement());
    }

	@Override public AST visitParameterList(pascalParser.ParameterListContext ctx) {
        AST node = AST.newSubtree(NodeKind.PARAMETER_LIST_NODE, NO_TYPE);
        // Basta pegar o size do formalParameterSection pra saber quantos tem.
        for (int i = 0; i < ctx.actualParameter().size(); i++) {
            // Visita um por um, com o 0 sendo o primeiro (fora do fecho), e
            // os demais dentro do fecho.
            AST child = visit(ctx.actualParameter(i).expression());
            node.addChild(child);
        }
        return node;
    }

    @Override 
    public AST visitAssignmentStatement(pascalParser.AssignmentStatementContext ctx) { 
        // Visita a expressão da direita.
		AST exprNode = visit(ctx.expression());
		// Visita o identificador da esquerda.
		Token idToken = ctx.variable().identifier(0).IDENT().getSymbol();
		AST idNode = checkVar(idToken);
		// Faz as verificações de tipos.
		return checkAssign(idToken.getLine(), idNode, exprNode);
    }

    @Override public AST visitExpression(pascalParser.ExpressionContext ctx) {
        if (ctx.relationaloperator() != null){
            AST simpleExpression = visit(ctx.simpleExpression());
            AST Expression = visit(ctx.expression());
            Unif unif = simpleExpression.type.unifyComp(Expression.type);

            if (unif.type == NO_TYPE) {
                typeError(
                    ctx.relationaloperator().operator.getLine(),
                    ctx.relationaloperator().operator.getText().toLowerCase(),
                    simpleExpression.type, Expression.type);
            }

            simpleExpression = Conv.createConvNode(unif.lc, simpleExpression);
            Expression = Conv.createConvNode(unif.rc, Expression);

            if (ctx.relationaloperator().operator.getType() == pascalParser.EQUAL)
                return AST.newSubtree(EQ_NODE, unif.type, simpleExpression, Expression);
            else if(ctx.relationaloperator().operator.getType() == pascalParser.NOT_EQUAL)
                return AST.newSubtree(NOT_EQ_NODE, unif.type, simpleExpression, Expression);
            else if(ctx.relationaloperator().operator.getType() == pascalParser.LT)
                return AST.newSubtree(LT_NODE, unif.type, simpleExpression, Expression);
            else if(ctx.relationaloperator().operator.getType() == pascalParser.LE)
                return AST.newSubtree(LE_NODE, unif.type, simpleExpression, Expression);
            else if(ctx.relationaloperator().operator.getType() == pascalParser.GT)
                return AST.newSubtree(GT_NODE, unif.type, simpleExpression, Expression);
            else if(ctx.relationaloperator().operator.getType() == pascalParser.GE)
                return AST.newSubtree(GE_NODE, unif.type, simpleExpression, Expression);
        }
        
        return visit(ctx.simpleExpression());
    }

    @Override public AST visitSimpleExpression(pascalParser.SimpleExpressionContext ctx) {
        if (ctx.additiveoperator() != null){
            AST term = visit(ctx.term());
            AST simpleExpression = visit(ctx.simpleExpression());
            Unif unif = null;

            if(ctx.additiveoperator().operator.getType() == pascalParser.PLUS){
                unif = term.type.unifyPlus(term.type);
            }
            else if(ctx.additiveoperator().operator.getType() == pascalParser.OR){
                unif = term.type.unifyComp(term.type);
            }
            else if(ctx.additiveoperator().operator.getType() == pascalParser.MINUS){
                unif = term.type.unifyOtherArith(term.type);
            }

            if (unif.type == NO_TYPE) {
                typeError(
                    ctx.additiveoperator().operator.getLine(),
                    ctx.additiveoperator().operator.getText().toLowerCase(),
                    term.type, simpleExpression.type);
            }

            term = Conv.createConvNode(unif.lc, term);
            simpleExpression = Conv.createConvNode(unif.rc, simpleExpression);

            if (ctx.additiveoperator().operator.getType() == pascalParser.PLUS)
                return AST.newSubtree(PLUS_NODE, unif.type, term, simpleExpression);
            else if(ctx.additiveoperator().operator.getType() == pascalParser.MINUS)
                return AST.newSubtree(MINUS_NODE, unif.type, term, simpleExpression);
            else if(ctx.additiveoperator().operator.getType() == pascalParser.OR)
                return AST.newSubtree(OR_NODE, unif.type, term, simpleExpression);
        }
        
        return visitTerm(ctx.term());
    }

    @Override public AST visitTerm(pascalParser.TermContext ctx) {
        if (ctx.multiplicativeoperator() != null){
            AST signedFactor = visit(ctx.signedFactor());
            AST term = visit(ctx.term());
            Unif unif = null;

            if(ctx.multiplicativeoperator().operator.getType() == pascalParser.STAR){
                unif = signedFactor.type.unifyPlus(term.type);
            }
            else if(ctx.multiplicativeoperator().operator.getType() == pascalParser.SLASH){
                unif = signedFactor.type.unifyPlus(term.type);
            }
            else if(ctx.multiplicativeoperator().operator.getType() == pascalParser.DIV){
                unif = signedFactor.type.unifyPlus(term.type);
            }
            else if(ctx.multiplicativeoperator().operator.getType() == pascalParser.AND){
                unif = signedFactor.type.unifyComp(term.type);
            }

            if (unif.type == NO_TYPE) {
                typeError(
                    ctx.multiplicativeoperator().operator.getLine(),
                    ctx.multiplicativeoperator().operator.getText().toLowerCase(),
                    signedFactor.type, term.type);
            }

            signedFactor = Conv.createConvNode(unif.lc, signedFactor);
            term = Conv.createConvNode(unif.rc, term);

            if (ctx.multiplicativeoperator().operator.getType() == pascalParser.STAR)
                return AST.newSubtree(TIMES_NODE, unif.type, signedFactor, term);
            else if(ctx.multiplicativeoperator().operator.getType() == pascalParser.SLASH)
                return AST.newSubtree(OVER_NODE, unif.type, signedFactor, term);
            else if(ctx.multiplicativeoperator().operator.getType() == pascalParser.DIV)
                return AST.newSubtree(OVER_NODE, unif.type, signedFactor, term);
            else if(ctx.multiplicativeoperator().operator.getType() == pascalParser.AND)
                return AST.newSubtree(AND_NODE, unif.type, signedFactor, term);
        }
        
        return visitSignedFactor(ctx.signedFactor());
    }

    @Override public AST visitSignedFactor(pascalParser.SignedFactorContext ctx) {
        AST exprNode = visit(ctx.factor());

        if(ctx.MINUS() != null){
            if (exprNode.type == Type.REAL_TYPE){
                return new AST(exprNode.kind, exprNode.floatData*(-1), Type.REAL_TYPE);
            }
            else{
                return new AST(exprNode.kind, exprNode.intData*(-1), Type.INT_TYPE);
            }
        }

        return exprNode;
    }

    @Override public AST visitFactor(pascalParser.FactorContext ctx) {
        if (ctx.variable() != null) {
            return visit(ctx.variable());
        }

        if (ctx.functionDesignator() != null) {
            return visit(ctx.functionDesignator());
        }

        return super.visitFactor(ctx);
    }

	@Override public AST visitFunctionDesignator(pascalParser.FunctionDesignatorContext ctx) {
        // Checar se ctx.identifier esta na FUNCTION TABLE

        // Caso a função exista, checar se PARAMETER_LIST bate

        // Caso alguma das condições acima não seja verdadeira, raise error
        AST identifier = checkVar(ctx.identifier().IDENT().getSymbol());
        AST parameterList = visit(ctx.parameterList());

        // Estou passando o tipo da função na mão, tem que pegar o valor de FUNCTION TABLE e colocar na subtree.

        return AST.newSubtree(NodeKind.FUNCTION_DESIGN_NODE, Type.INT_TYPE, identifier, parameterList);
    }

    @Override public AST visitVariable(pascalParser.VariableContext ctx) {
        if (ctx.LBRACK(0) != null) {
            int index = vt.getIndex(ctx.identifier(0).getText().toLowerCase());
            AST node = AST.newSubtree(VAR_USE_NODE, vt.getType(index));
            Token token = ctx.identifier(0).IDENT().getSymbol();

            // Adiciona o array como primeiro elemento do subscript
            node.addChild(new AST(VAR_USE_NODE,index,ARRAY_TYPE));
            
            // Adiciona os índices como elementos do subscript
            for (var range : ctx.expression()) {
                AST exprNode = visit(range);

                if (exprNode.type != INT_TYPE) {
                    String msg = String.format(
                        "line %d: array index type('%s') is incompatible, index must be an integer.",
                        token.getLine(), exprNode.type);
                    System.err.printf("%s",msg);
                }

                node.addChild(exprNode);
            }

            //checkSubscriptDimension(ctx.expression().size(), var, token);
            
            return node;
        }
        
        return checkVar(ctx.identifier(0).IDENT().getSymbol());
    }
/*
    private void checkSubscriptDimension(int subscriptDim, Array array, Token token) {
        if (subscriptDim != array.getDimensionSize()) {
            String msg = String.format(
                "line %d: inconsistent number of indice(s) for array '%s'," +
                " informed %d indice(s) being necessary %d.",
                token.getLine(), token.getText().toLowerCase(), subscriptDim, array.getDimensionSize());
            System.err.printf("%s",msg);
        }
    }*/

    @Override public AST visitExprIntegerVal(pascalParser.ExprIntegerValContext ctx) {
        int intData = Integer.parseInt(ctx.getText());
		return new AST(INT_VAL_NODE, intData, INT_TYPE);
    }

	@Override public AST visitExprRealVal(pascalParser.ExprRealValContext ctx) {
        float floatData = Float.parseFloat(ctx.getText());
		return new AST(REAL_VAL_NODE, floatData, REAL_TYPE);
    }

    @Override public AST visitExprTrue(pascalParser.ExprTrueContext ctx) {
        return new AST(BOOL_VAL_NODE, 1, BOOL_TYPE);
    }

	@Override public AST visitExprFalse(pascalParser.ExprFalseContext ctx) {
        return new AST(BOOL_VAL_NODE, 0, BOOL_TYPE);
    }

    @Override public AST visitExprStrVal(pascalParser.ExprStrValContext ctx) {
        int idx = st.addStr(ctx.string().getText());
		// Campo 'data' do nó da AST guarda o índice na tabela.
		return new AST(STR_VAL_NODE, idx, STR_TYPE);
    }

    @Override
    public AST visitIfStatement(pascalParser.IfStatementContext ctx) {
        // Analisa a expressão booleana.
        AST exprNode = visit(ctx.expression());

        AST thenNode = visit(ctx.statement(0).unlabelledStatement());

        checkBoolExpr(ctx.IF().getSymbol().getLine(), "if", exprNode.type);

        // Constrói o bloco da condicional.
        if (ctx.ELSE() == null) {
            return AST.newSubtree(IF_NODE, NO_TYPE, exprNode, thenNode);
        } 
        else {
            AST elseNode = AST.newSubtree(ELSE_NODE, NO_TYPE); 
            elseNode.addChild(visitUnlabelledStatement(ctx.statement(1).unlabelledStatement()));

            return AST.newSubtree(IF_NODE, NO_TYPE, exprNode, thenNode, elseNode);
        }
    }

    @Override public AST visitRepeatStatement(pascalParser.RepeatStatementContext ctx) {
        // Analisa a expressão booleana.

        AST exprNode = visit(ctx.expression());
        checkBoolExpr(ctx.UNTIL().getSymbol().getLine(), "repeat", exprNode.type);

        // Constrói o bloco de código do loop.
        AST statementsNode = visit(ctx.statements());

        return AST.newSubtree(NodeKind.REPEAT_NODE, NO_TYPE, exprNode, statementsNode);
    }
}