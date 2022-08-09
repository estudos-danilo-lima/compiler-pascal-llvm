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
    protected Void visitAssign(AST node){return null;}

	@Override
    protected Void visitEq(AST node){return null;}

	@Override
    protected Void visitBlock(AST node){return null;}

	@Override
    protected Void visitBoolVal(AST node){return null;}

	@Override
    protected Void visitIf(AST node){return null;}

	@Override
    protected Void visitIntVal(AST node){return null;}

	@Override
    protected Void visitLt(AST node){return null;}

	@Override
    protected Void visitMinus(AST node){return null;}

	@Override
    protected Void visitOver(AST node){return null;}

	@Override
    protected Void visitPlus(AST node){return null;}

	@Override
    protected Void visitProgram(AST node){return null;}

	@Override
    protected Void visitRead(AST node){return null;}

	@Override
    protected Void visitRealVal(AST node){return null;}

	@Override
    protected Void visitRepeat(AST node){return null;}

	@Override
    protected Void visitStrVal(AST node){return null;}

	@Override
    protected Void visitTimes(AST node){return null;}

	@Override
    protected Void visitVarDecl(AST node){return null;}

	@Override
    protected Void visitVarUse(AST node){return null;}

	@Override
    protected Void visitWrite(AST node){return null;}

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