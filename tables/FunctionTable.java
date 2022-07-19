package tables;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import typing.Type;
//import tables.VarTable;

public final class FunctionTable {

	// No mundo real isto certamente deveria ser um hash...
	// Implementação da classe não é exatamente Javanesca porque
	// tentei deixar o mais parecido possível com a original em C.
	private List<Entry> table = new ArrayList<Entry>(); 
	
	public FunctionTable(){
		// Cria a função built-in de leitura que só lê string.
		this.addFunction("Readln", 0, Type.NO_TYPE);
		ArrayList<typing.Type> params = new ArrayList<typing.Type>();
		params.add(typing.Type.STR_TYPE);
		this.SetParameterList(params);

		// Cria a função built-in de escrita que só escreve string.
		this.addFunction("Writeln", 0, Type.NO_TYPE);
		this.SetParameterList(params);
	}

	public int func_lookupVarTable(String s) {
		return table.get(table.size()-1).vt.lookupVar(s);
	}

	public int getSize() {
		return this.table.size();
	}

	public int lookupFunc(String s) {
		for (int i = 0; i < table.size(); i++) {
			if (table.get(i).name.equals(s)) {
				return i;
			}
		}
		return -1;
	}

	public int addFunction(String s, int line, Type type) {
		Entry entry = new Entry(s, line, type);
		int idxAdded = table.size();
		table.add(entry);
		return idxAdded;
	}

	public void SetParameterList(ArrayList<typing.Type> parameters) {
		table.get(table.size()-1).parameters = parameters;
	}
	
	public int setVariable(String s, int line, Type type) {
		return table.get(table.size()-1).vt.addVar(s, line, type);
	}

	public ArrayList<typing.Type> GetParameterList(int i) {
		return table.get(i).parameters;
	}
	public String getName(int i) {
		return table.get(i).name;
	}
	
	public int getLine(int i) {
		return table.get(i).line;
	}
	
	public Type getType(int i) {
		return table.get(i).type;
	}
	
	public ArrayList<Type> getParameters(int i) {
		return table.get(i).parameters;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb);
		f.format("Functions table:\n");
		for (int i = 0; i < table.size(); i++) {
			f.format("Entry %d -- name: %s, line: %d, type: %s\n", i,
	                 getName(i), getLine(i), getType(i).toString());
			if (table.get(i).parameters == null)
				continue;
			for (int j = 0; j < table.get(i).parameters.size(); j++) {
				f.format("\t type: %s\n", table.get(i).parameters.get(j).toString());
					}
			f.format("\t Function Variable Table:\n");
			for (int j = 0; j < table.get(i).vt.getSize(); j++) {
				f.format("\t\t name: %s, line: %d, type: %s\n", table.get(i).vt.getName(j),
					table.get(i).vt.getLine(j), table.get(i).vt.getType(j).toString());
			}
		}
		f.close();
		return sb.toString();
	}
	
	private static final class Entry {
		private final String name;
		private final int line;
		private final Type type;
        private ArrayList<typing.Type> parameters = new ArrayList<typing.Type>();
		private final VarTable vt = new VarTable();
		
		Entry(String name, int line, Type type) {
			this.name = name;
			this.line = line;
			this.type = type;
			this.parameters = null;
		}
	}

    public int getIndex(String name) {
        for(int i = 0; i < this.table.size(); i++){
			if(this.table.get(i).name.equals(name)){
				return i;
			}
		}
		return -1;
    }
}
