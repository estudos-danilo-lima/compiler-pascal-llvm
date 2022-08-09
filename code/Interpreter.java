package code;

import static typing.Type.INT_TYPE;
import static typing.Type.REAL_TYPE;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Scanner;

import ast.AST;
import ast.ASTBaseVisitor;
import tables.StrTable;
import tables.VarTable;
import typing.Type;

/*
 * Interpretador de código para EZLang, implementado como
 * um visitador da AST gerada pelo front-end. Tipo genérico
 * foi instanciado para Void porque a gente não precisa de
 * um valor de retorno na visitação. Para o gerador de código
 * do próximo laboratório isso vai mudar.
 *
 * Para rodar, chame o método 'execute' da superclasse.
 */
public class Interpreter extends ASTBaseVisitor<Void> {

	// Tudo privado e final para simplificar.
	private final DataStack stack;
	private final Memory memory;
	private final StrTable st;
	private final VarTable vt;
	private final Scanner in; // Para leitura de stdin

	// Construtor basicão.
	public Interpreter(StrTable st, VarTable vt) {
		this.stack = new DataStack();
		this.memory = new Memory(vt);
		this.st = st;
		this.vt = vt;
		this.in = new Scanner(System.in);
	}

	@Override
    protected Void visitProgram(AST node){
		visit(node.getChild(0)); // run program heading
		visit(node.getChild(1)); // run block
		in.close(); // Fim do programa, não precisa mais de ler de stdin.
	
		return null;
	}

	@Override
	protected Void visitProgramHeading(AST node){
		// Nothing to do.
		return null;
	}

	@Override
	protected Void visitIdentifier(AST node){
		// Nothing to do.
		return null;
	}

	@Override
    protected Void visitBlock(AST node){
		// Visita todos os filhos do bloco na ordem.
		// Function se tiver, declaration part e depois statements
		for (int i = 0; i < node.getChildCount(); i++) {
			visit(node.getChild(i));
		}

		return null;
	}

	@Override
	protected Void visitVarDeclPart(AST node){
		// Nothing to do.
		return null;
	}

	@Override
	protected Void visitIdentifierList(AST node){
		// Nothing to do.
		return null;		
	}

	@Override
	protected Void visitStatementList(AST node){
		// Visita todos os filhos do bloco na ordem.
		for (int i = 0; i < node.getChildCount(); i++) {
			visit(node.getChild(i));
		}

		return null;
	}

	@Override
    protected Void visitAssign(AST node){
		// Visita recursivamente a expressão da direita para
		// calcular o seu valor, que vai ficar no topo da pilha.
		AST rexpr = node.getChild(1);
		visit(rexpr);
		// Armazena o valor da pilha na memória, conforme o tipo
		// da variável.
		int varIdx = node.getChild(0).intData;
		Type varType = vt.getType(varIdx);
		if (varType == REAL_TYPE) {
			memory.storef(varIdx, stack.popf());
		} else {
			memory.storei(varIdx, stack.popi());
		}
		return null; // Java exige um valor de retorno mesmo para Void... :/
	}

	@Override
    protected Void visitEq(AST node){return null;}

	@Override
    protected Void visitIf(AST node){return null;}

	@Override
    protected Void visitLt(AST node){return null;}

	@Override
    protected Void visitMinus(AST node){return null;}

	@Override
    protected Void visitOver(AST node){return null;}

	@Override
    protected Void visitPlus(AST node){
		// Poderia fazer tudo em um método só mas como o '+'
		// é sobrecarregado, preferi dividir em métodos auxiliares.
		switch(node.type) {
			case INT_TYPE:  plusInt(node);    break;
	        case REAL_TYPE: plusReal(node);   break;
	        case BOOL_TYPE: plusBool(node);   break;
	        case STR_TYPE:  plusStr(node);    break;
			case NO_TYPE:
		    default:
	            System.err.printf("Invalid type: %s!\n",node.type.toString());
	            System.exit(1);
		}
		return null; // Java exige um valor de retorno mesmo para Void... :/
	}

	private Void plusInt(AST node) {
		visit(node.getChild(0));
		visit(node.getChild(1));
	    int r = stack.popi();
	    int l = stack.popi();
	    stack.pushi(l + r);
	    return null; // Java exige um valor de retorno mesmo para Void... :/
	}

	private Void plusReal(AST node) {
		visit(node.getChild(0));
		visit(node.getChild(1));
	    float r = stack.popf();
	    float l = stack.popf();
	    stack.pushf(l + r);
	    return null; // Java exige um valor de retorno mesmo para Void... :/
	}

	private Void plusBool(AST node) {
		visit(node.getChild(0));
		visit(node.getChild(1));
	    int r = stack.popi();
	    int l = stack.popi();
	    if (l == 1 || r == 1) {
	    	stack.pushi(1); // true
	    } else {
	    	stack.pushi(0); // false
	    }
	    return null; // Java exige um valor de retorno mesmo para Void... :/
	}

	private Void plusStr(AST node) {
		visit(node.getChild(0));
		visit(node.getChild(1));
	    int r = stack.popi();
	    int l = stack.popi();
	    String ls = st.get(l);
	    String rs = st.get(r);
	    StringBuilder sb = new StringBuilder();
	    // Todas as strings ficam envoltas por ", por isso,
	    // na hora de concatenar, precisamos retirar o último
	    // caractere da substring da esquerda...
	    sb.append(ls.substring(0, ls.length() - 1));
	    // ...e o primeiro caractere da substring da direita.
	    sb.append(rs.substring(1));
	    // Adiciona a nova string na tabela.
	    int newStrIdx = st.addStr(sb.toString());
	    // Retorna o índice da nova string pela pilha.
	    stack.pushi(newStrIdx);
	    return null; // Java exige um valor de retorno mesmo para Void... :/
    }

	@Override
    protected Void visitRead(AST node){return null;}

	@Override
    protected Void visitRepeat(AST node){return null;}

	@Override
    protected Void visitTimes(AST node){return null;}

	@Override
    protected Void visitVarDecl(AST node){return null;}

	@Override
    protected Void visitWrite(AST node){return null;}

	@Override
    protected Void visitVarUse(AST node){
		int varIdx = node.intData;
		if (node.type == REAL_TYPE) {
			stack.pushf(memory.loadf(varIdx));
		} else {
			stack.pushi(memory.loadi(varIdx));
		}
		return null; // Java exige um valor de retorno mesmo para Void... :/
	}

	@Override
    protected Void visitIntVal(AST node){
		stack.pushi(node.intData);
		return null; // Java exige um valor de retorno mesmo para Void... :/
	}

	@Override
    protected Void visitStrVal(AST node){
		stack.pushi(node.intData);
		return null; // Java exige um valor de retorno mesmo para Void... :/
	}

	@Override
    protected Void visitBoolVal(AST node){
		stack.pushi(node.intData);
		return null; // Java exige um valor de retorno mesmo para Void... :/
	}

	@Override
    protected Void visitRealVal(AST node){
		stack.pushf(node.floatData);
		return null; // Java exige um valor de retorno mesmo para Void... :/
	}

	@Override
    protected Void visitB2I(AST node){return null;}

	@Override
    protected Void visitB2R(AST node){return null;}

	@Override
    protected Void visitB2S(AST node){return null;}

	@Override
    protected Void visitI2R(AST node){return null;}

	@Override
    protected Void visitI2S(AST node){return null;}

	@Override
    protected Void visitR2S(AST node){return null;}

}