package code;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Scanner;

import ast.AST;
import ast.ASTBaseVisitor;
import tables.StrTable;
import tables.VarTable;
import tables.FunctionTable;

import typing.Type;
import static typing.Type.INT_TYPE;
import static typing.Type.REAL_TYPE;
import static typing.Type.BOOL_TYPE;
import static typing.Type.STR_TYPE;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

public final class CodeGen extends ASTBaseVisitor<String> {

	private final StrTable st;
	private final VarTable vt;
	private final FunctionTable ft;

	private int functionIDX = -1;

	private static ArrayList<String> declares;
	private static HashMap<Print, Print> printStrs;

	private static Formatter strs;

	private static int globalRegsCount;
	private static int localRegsCount;
	private static int jumpLabel;

	private static String compPrototype = "declare i32 @strcmp(i8*, i8*)";
	private static String scanPrototype = "declare i32 @__isoc99_scanf(i8*, ...)";
	private static String printPrototype = "declare i32 @printf(i8*, ...)";

	public CodeGen(StrTable st, VarTable vt, FunctionTable ft) {
		this.st = st;
        this.vt = vt;
        this.ft = ft;

		declares = new ArrayList<>();
		printStrs = new HashMap<>();
		strs = new Formatter();
	}

	public void execute(AST root) {
		globalRegsCount = 0;
		localRegsCount = 1;
		visit(root);
	}

	// ----------------------------------------------------------------------------
	// ------------------------------- Prints -------------------------------------

	private void getStringTable() {
		for (int i = 0; i < st.size(); i++) {
			String s = st.getString(i);
			int x = newGlobalReg();
			strs.format("@%d = private constant [%d x i8] c\"%s\\00\"\n", x,
					s.length() + 1, s);
		}
	}

	private void getPrintStrings() {
		ArrayList<Print> a = new ArrayList<Print>(printStrs.values());
		PrintComparator pc = new PrintComparator();
		a.sort(pc);
		for (Print ele : a) {
			strs.format("%s", ele);
		}
	}

	private void dumpStrings() {
		System.out.println(strs.toString());
		strs.close();
	}

	private void dumpFuncDeclare() {
		for (String declare : declares) {
			System.out.println(declare);
		}
	}

	// ----------------------------------------------------------------------------
	// ---------------------------- Registradores ---------------------------------

	private int newGlobalReg() {
		return globalRegsCount++;
	}

	private void resetLocalScope() {
		localRegsCount = 1;
		jumpLabel = 0;
	}

	private int newLocalReg() {
		return localRegsCount++;
	}

	// This would be changed to handle multiple function scopes
	private int newJumpLabel() {
		return jumpLabel++;
	}

	// ----------------------------------------------------------------------------
	// -------------------------------- Árvore ------------------------------------
	
	@Override
    protected String visitProgram(AST node){

		getStringTable();

		System.out.println("\ndefine void @main() {");

		visit(node.getChild(0)); // run program heading
		visit(node.getChild(1)); // run block
	
		System.out.println("  ret void \n}\n");

		getPrintStrings();
		dumpStrings();

		dumpFuncDeclare();

		return null;
	}

	@Override
	protected String visitProgramHeading(AST node){
		// Nothing to do.
		return "";
	}

	@Override
	protected String visitIdentifier(AST node){
		// Nothing to do.
		return "";
	}

	@Override
    protected String visitBlock(AST node){

		// Visita todos os filhos do bloco na ordem.
		for (int i = 0; i < node.getChildCount(); i++) {
			visit(node.getChild(i));
		}

		return "";
	}

	@Override
	protected String visitVarDeclPart(AST node){

		// Visita todos os filhos do bloco na ordem.
		for (int i = 0; i < node.getChildCount(); i++) {
			visit(node.getChild(i));
		}

		return "";
	}

	@Override
	protected String visitIdentifierList(AST node){
		// Visita todos os filhos do bloco na ordem.
		for (int i = 0; i < node.getChildCount(); i++) {
			visit(node.getChild(i));
		}
		return "";		
	}

	@Override
	protected String visitVarDecl(AST node){
		int x = newLocalReg();

		switch(node.type){
			case INT_TYPE:
				System.out.printf("  %%%d = alloca i32\n", x);
				break;
			case REAL_TYPE:
				System.out.printf("  %%%d = alloca double\n", x);
				break;
			case BOOL_TYPE:
				System.out.printf("  %%%d = alloca i1\n", x);
				break;
			case STR_TYPE:
				System.out.printf("  %%%d = alloca i8*\n", x);
				break;
			case NO_TYPE:
			default:
				System.err.println("Missing VarDecl!");
		}

		return "";
	}

	@Override
	protected String visitStatementList(AST node){
		// Visita todos os filhos do bloco na ordem.
		for (int i = 0; i < node.getChildCount(); i++) {
			visit(node.getChild(i));
		}
		return "";
	}

// Daqui pra cima tudo mais ou menos ok

	@Override
	protected String visitProcedureDesignator(AST node){

		// Identifica o procedimento.
		visit(node.getChild(0));

		// Salva os parametros e executa o procedimento.
		visit(node.getChild(1));

		return "";
	}

	@Override
	protected String visitFuncIdentifier(AST node){

		functionIDX = node.intData;

		return "";
	}

	@Override
	protected String visitParameterList(AST node){

		if (!declares.contains(printPrototype))
			declares.add(printPrototype);

		int size = node.getChildCount();
		
		if (functionIDX == 0){
			int varIdx = node.getChild(0).intData;
			Type varType = vt.getType(varIdx);

			switch(varType) {
				case INT_TYPE:  readInt(varIdx);    break;
				case REAL_TYPE: readReal(varIdx);   break;
				// case BOOL_TYPE: readBool(varIdx);   break;
				// case STR_TYPE:  readStr(varIdx);    break;
				case NO_TYPE:
				default:
					System.err.printf("Invalid type: %s!\n", varType.toString());
					System.exit(1);
			}
		}
		else if (functionIDX == 1){
			AST expr = node.getChild(0);
			String x = visit(expr);
			switch(expr.type) {
				case INT_TYPE:  writeInt(x);    break;
				case REAL_TYPE: writeReal(x);   break;
				case BOOL_TYPE: writeBool(x);   break;
				case STR_TYPE:  writeStr(x);    break;
				case NO_TYPE:
				default:
					System.err.printf("Invalid type: %s!\n", expr.type.toString());
					System.exit(1);
			}
		}
		else{
			// Função não implementada

			// for (int i = 0; i < node.getChildCount(); i++) {
			// 	visit(node.getChild(i));
			// }
		}
		functionIDX = -1;

		return "";
	}

	private String readInt(int varIdx) {
		int x = 0;
		if (!printStrs.containsKey(Print.INT)) {
			int i = newGlobalReg();
			Print p = Print.INT.setIndex(i);
			printStrs.put(Print.INT, p);
		}
		int a = printStrs.get(Print.INT).index;
		int pointer = newLocalReg();
		x = newLocalReg();

		System.out.printf("  %%%d = getelementptr inbounds [3 x i8], [3 x i8]* @%d, i64 0, i64 0\n", pointer, a);
		System.out.printf("  %%%d = call i32 (i8*, ...) @__isoc99_scanf(i8* %%%d, i32* %%%d)\n", x, pointer, varIdx + 1);
		return "";
	}

	private String readReal(int varIdx) {
		int x = 0;
		if (!printStrs.containsKey(Print.REAL)) {
			int i = newGlobalReg();
			Print p = Print.REAL.setIndex(i);
			printStrs.put(Print.REAL, p);
		}
		int a = printStrs.get(Print.REAL).index;
		int pointer = newLocalReg();
		x = newLocalReg();

		System.out.printf("  %%%d = getelementptr inbounds [4 x i8], [4 x i8]* @%d, i64 0, i64 0\n", pointer, a);
		System.out.printf("  %%%d = call i32 (i8*, ...) @__isoc99_scanf(i8* %%%d, double* %%%d)\n", x, pointer, varIdx + 1);
		return "";
	}

	private String writeInt(String x) {
		int pointer = newLocalReg();
		int result = newLocalReg();

		if (!printStrs.containsKey(Print.INT)) {
			int i = newGlobalReg();
			Print p = Print.INT.setIndex(i);
			printStrs.put(Print.INT, p);
		}
		int a = printStrs.get(Print.INT).index;

		System.out.printf("  %%%d = getelementptr inbounds [3 x i8], [3 x i8]* @%d, i64 0, i64 0\n", pointer, a);
		System.out.printf("  %%%d = call i32 (i8*, ...) @printf(i8* %%%d, i32 %s)\n", result, pointer, x);
		return "";
	}

	private String writeReal(String x) {
		int pointer = newLocalReg();
		int result = newLocalReg();

		if (!printStrs.containsKey(Print.REAL)) {
			int i = newGlobalReg();
			Print p = Print.REAL.setIndex(i);
			printStrs.put(Print.REAL, p);
		}
		int a = printStrs.get(Print.REAL).index;

		System.out.printf("  %%%d = getelementptr inbounds [4 x i8], [4 x i8]* @%d, i64 0, i64 0\n", pointer, a);
		System.out.printf("  %%%d = call i32 (i8*, ...) @printf(i8* %%%d, double %s)\n", result, pointer, x);
		return "";
	}

	private String writeBool(String x) {
		int pointer = newLocalReg();
		int result = newLocalReg();

		// Printing as INT, as printf doesn't have bool
		if (!printStrs.containsKey(Print.INT)) {
			int i = newGlobalReg();
			Print p = Print.INT.setIndex(i);
			printStrs.put(Print.INT, p);
		}
		int a = printStrs.get(Print.INT).index;

		System.out.printf("  %%%d = getelementptr inbounds [3 x i8], [3 x i8]* @%d, i64 0, i64 0\n", pointer,
				a);
		System.out.printf("  %%%d = call i32 (i8*, ...) @printf(i8* %%%d, i1 %s)\n", result, pointer, x);
		return "";
	}

	private String writeStr(String x) {
		int pointer = newLocalReg();
		int result = newLocalReg();

		if (x.startsWith("@")) {
			String s = st.getString(Integer.parseInt(x.substring(1)));
			int len = s.length() + 1;

			System.out.printf("  %%%d = getelementptr inbounds [%d x i8], [%d x i8]* %s, i64 0, i64 0\n",
					pointer,
					len, len, x);
			System.out.printf("  %%%d = call i32 (i8*, ...) @printf(i8* %%%d)\n", result,
					pointer);
		} else {
			// If there isn't the printf string to print STR type
			// ("%s\00"), adds it.
			if (!printStrs.containsKey(Print.STR)) {
				int i = newGlobalReg();
				Print p = Print.STR.setIndex(i);
				printStrs.put(Print.STR, p);
			}
			int a = printStrs.get(Print.STR).index;

			System.out.printf("  %%%d = getelementptr inbounds [3 x i8], [3 x i8]* @%d, i64 0, i64 0\n",
					pointer,
					a);
			System.out.printf("  %%%d = call i32 (i8*, ...) @printf(i8* %%%d, i8* %s)\n", result, pointer, x);
		}
		return "";
	}

	@Override
    protected String visitAssign(AST node){
		AST r = node.getChild(1);
	    String x = visit(r);
	    int varIdx = node.getChild(0).intData;
	    Type varType = vt.getType(varIdx);

		switch(varType) {
			case INT_TYPE:
				System.out.printf("  store i32 %s, i32* %%%d\n", x, varIdx + 1);
				break;
			case REAL_TYPE:
				System.out.printf("  store double %s, double* %%%d\n", x, varIdx + 1);
				break;
			case BOOL_TYPE:
				System.out.printf("  store i1 %s, i1* %%%d\n", x, varIdx + 1);
				break;
			case STR_TYPE:
				// String can be pure (@str), which the pointer is needed,
				// or form register (%num)
				if (x.startsWith("@")) {
					int pointer = newLocalReg();
					String s = st.getString(Integer.parseInt(x.substring(1)));
					int len = s.length() + 1;

					System.out.printf("  %%%d = getelementptr inbounds [%d x i8], [%d x i8]* %s, i64 0, i64 0\n", pointer, len, len, x);

					System.out.printf("  store i8* %%%d, i8** %%%d\n", pointer, varIdx + 1);
				} else {
					System.out.printf("  store i8* %s, i8** %%%d\n", x, varIdx + 1);

				}
				break;
			case NO_TYPE:
			default:
				System.err.println("Assign type not known!");
		}
		return "";
	}

	@Override
    protected String visitIf(AST node){
	// 	// Visita a expressão de teste.
	// 	visit(node.getChild(0));
	// 	int test = stack.popi();
	// 	if (test == 1) { // THEN
	// 		visit(node.getChild(1));
	// 	} else if (test == 0 && node.getChildCount() == 3) { // ELSE, se houver
	// 		visit(node.getChild(2));
	// 	}
		return null;
	}

	@Override
	protected String visitElse(AST node){
	// 	visit(node.getChild(0));
		return "";
	}

	@Override
    protected String visitEq(AST node){
	// 	// Executa as subexpressões cujos valores vão ficar na pilha.
	// 	AST lexpr = node.getChild(0);
	// 	AST rexpr = node.getChild(1);
	// 	visit(lexpr);
	// 	visit(rexpr);
	// 	// Poderia usar 'lexpr' também no teste abaixo, já que
	// 	// ambos os lados são garantidamente do mesmo tipo,
	// 	// por conta dos eventuais nós de conversão.
	// 	if (rexpr.type == INT_TYPE) {
	// 		int r = stack.popi();
	//         int l = stack.popi();
	//         stack.pushi(l == r ? 1 : 0); // 1 = true; 0 = false
	// 	} else if (rexpr.type == REAL_TYPE) {
	// 		float r = stack.popf();
	//         float l = stack.popf();
	//         stack.pushi(l == r ? 1 : 0);
	// 	} else { // Must be STR_TYPE
	// 		int r = stack.popi();
	//         int l = stack.popi();
	//         String ls = st.get(l);
	//         String rs = st.get(r);
	//         stack.pushi(ls.compareTo(rs) == 0 ? 1 : 0);
	// 	}
		return null;
	}

	@Override
	protected String visitGt(AST node) {
	// 	AST lexpr = node.getChild(0);
	// 	AST rexpr = node.getChild(1);
	// 	visit(lexpr);
	// 	visit(rexpr);
	// 	// Poderia usar 'lexpr' também no teste abaixo, já que
	// 	// ambos os lados são garantidamente do mesmo tipo,
	// 	// por conta dos eventuais nós de conversão.
	// 	if (rexpr.type == INT_TYPE) {
	// 		int r = stack.popi();
	//         int l = stack.popi();
	//         stack.pushi(l > r ? 1 : 0);
	// 	} else if (rexpr.type == REAL_TYPE) {
	// 		float r = stack.popf();
	//         float l = stack.popf();
	//         stack.pushi(l > r ? 1 : 0);
	// 	} else { // Must be STR_TYPE
	// 		int r = stack.popi();
	//         int l = stack.popi();
	//         String ls = st.get(l);
	//         String rs = st.get(r);
	//         stack.pushi(ls.compareTo(rs) > 0 ? 1 : 0);
	// 	}
		return null;
	}

	@Override
    protected String visitLt(AST node){
	// 	AST lexpr = node.getChild(0);
	// 	AST rexpr = node.getChild(1);
	// 	visit(lexpr);
	// 	visit(rexpr);
	// 	// Poderia usar 'lexpr' também no teste abaixo, já que
	// 	// ambos os lados são garantidamente do mesmo tipo,
	// 	// por conta dos eventuais nós de conversão.
	// 	if (rexpr.type == INT_TYPE) {
	// 		int r = stack.popi();
	//         int l = stack.popi();
	//         stack.pushi(l < r ? 1 : 0);
	// 	} else if (rexpr.type == REAL_TYPE) {
	// 		float r = stack.popf();
	//         float l = stack.popf();
	//         stack.pushi(l < r ? 1 : 0);
	// 	} else { // Must be STR_TYPE
	// 		int r = stack.popi();
	//         int l = stack.popi();
	//         String ls = st.get(l);
	//         String rs = st.get(r);
	//         stack.pushi(ls.compareTo(rs) < 0 ? 1 : 0);
	// 	}
		return null;
	}

	@Override
    protected String visitRepeat(AST node){
	// 	// Usando um loop para implementar um loop, que coisa "meta" isso... :P
	// 	int again = 1;
	//     while (again == 1) {
	//     	visit(node.getChild(1)); // run body
	// 		visit(node.getChild(0)); // run test
	//         again = (stack.popi() == 1? 1 : 0); // again = !popi();
	//     }
	    return null;
	}

	@Override
    protected String visitMinus(AST node){
	// 	visit(node.getChild(0));
	// 	visit(node.getChild(1));
	// 	if (node.type == INT_TYPE) {
	//         int r = stack.popi();
	//         int l = stack.popi();
	//         stack.pushi(l - r);
	//     } else { // Result must be REAL_TYPE.
	//         float r = stack.popf();
	//         float l = stack.popf();
	//         stack.pushf(l - r);
	//     }
		return null;
	}

	@Override
    protected String visitOver(AST node){
	// 	visit(node.getChild(0));
	// 	visit(node.getChild(1));
	// 	if (node.type == INT_TYPE) {
	//         int r = stack.popi();
	//         int l = stack.popi();
	//         stack.pushi(l / r);
	//     } else { // Result must be REAL_TYPE.
	//         float r = stack.popf();
	//         float l = stack.popf();
	//         stack.pushf(l / r);
	//     }
		return null;
	}

	@Override
    protected String visitPlus(AST node){

		String y = visit(node.getChild(0));
		String z = visit(node.getChild(1));
		int x = 0;

		switch(node.type){
			case INT_TYPE:
				x = newLocalReg();
				System.out.printf("  %%%d = add i32 %s, %s\n", x, y, z);
				break;
			case REAL_TYPE:
				x = newLocalReg();
				System.out.printf("  %%%d = fadd double %s, %s\n", x, y, z);
				break;
			case STR_TYPE:
				// Requires LLVM memory handling to avoid degmentation faults
				// could be handled with @strcat
				break;
			case NO_TYPE:
			default:
				System.err.println("This type is impossible to add");
		}

		if (node.type == INT_TYPE) {
			x = newLocalReg();
			System.out.printf("  %%%d = add i32 %s, %s\n", x, y, z);
		} else if (node.type == REAL_TYPE) {
			x = newLocalReg();
			System.out.printf("  %%%d = fadd double %s, %s\n", x, y, z);
		} else if (node.type == STR_TYPE) {
			// Requires LLVM memory handling to avoid degmentation faults
			// could be handled with @strcat
		} else {
			System.err.println("This type is impossible to add");
		}

		return String.format("%%%d", x);
	}

	@Override
    protected String visitTimes(AST node){
	// 	visit(node.getChild(0));
	// 	visit(node.getChild(1));
	// 	if (node.type == INT_TYPE) {
	//         int r = stack.popi();
	//         int l = stack.popi();
	//         stack.pushi(l * r);
	//     } else { // Result must be REAL_TYPE.
	//         float r = stack.popf();
	//         float l = stack.popf();
	//         stack.pushf(l * r);
	//     }
		return "";
	}

	@Override
    protected String visitVarUse(AST node){
		int x = newLocalReg();
		int varIdx = node.intData;

		switch (node.type){
			case INT_TYPE:
				System.out.printf("  %%%d = load i32, i32* %%%d\n", x, varIdx + 1);
				break;
			case REAL_TYPE:
				System.out.printf("  %%%d = load double, double* %%%d\n", x, varIdx + 1);
				break;
			case BOOL_TYPE:
				System.out.printf("  %%%d = load i1, i1* %%%d\n", x, varIdx + 1);
				break;
			case STR_TYPE:
				System.out.printf("  %%%d = load i8*, i8** %%%d\n", x, varIdx + 1);
				break;
			case NO_TYPE:
			default:
				System.err.println("Missing VarUse!");
		}

		return String.format("%%%d", x);
	}

	@Override
    protected String visitIntVal(AST node){
		return Integer.toString(node.intData);
	}

	@Override
    protected String visitStrVal(AST node){
		return String.format("@%d", node.intData);
	}

	@Override
    protected String visitBoolVal(AST node){
		boolean x;
		if (node.intData == 0) {
			x = false;
		} else {
			x = true;
		}
		String val = Boolean.toString(x);
		return val;
	}

	@Override
    protected String visitRealVal(AST node){
		return Float.toString(node.floatData);
	}

	@Override
    protected String visitB2I(AST node){return "";}

	@Override
    protected String visitB2R(AST node){return "";}

	@Override
    protected String visitB2S(AST node){return "";}

	@Override
    protected String visitI2R(AST node){
		String i = visit(node.getChild(0));
		int r = newLocalReg();
		System.out.printf("  %%%d = sitofp i32 %s to double\n", r, i);

		return String.format("%%%d", r);
	}

	@Override
    protected String visitI2S(AST node){return "";}

	@Override
    protected String visitR2S(AST node){return "";}

}
