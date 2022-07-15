package tables;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import typing.Type;

public final class ArrayTable {

	// No mundo real isto certamente deveria ser um hash...
	// Implementação da classe não é exatamente Javanesca porque
	// tentei deixar o mais parecido possível com a original em C.
	private List<Entry> table = new ArrayList<Entry>(); 

    public int addArray(String s, int line, Type type,Range range) {
		Entry entry = new Entry(s, line, type,range);
		int idxAdded = table.size();
		table.add(entry);
		return idxAdded;
	}
	
    public Entry getArray(int i){
        return table.get(i);
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
    
    public String getRangeString(int i) {
        String text = "(";

        for (int j = 0; j < table.get(i).ranges.size(); j++) {
            text += table.get(i).ranges.get(j).toString();
            if (j != table.get(i).ranges.size() - 1) text += ", ";
        }

        return text + ")";
    }

    public int getDimensionSize(int i) {
        return table.get(i).ranges.size();
    }
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb);
		f.format("Array table:\n");
		for (int i = 0; i < table.size(); i++) {
			f.format("Entry %d -- name: %s, line: %d, type: %s, range: [%s]\n", i,
	                 getName(i), getLine(i), getType(i).toString(),getRangeString(i));
		}
		f.close();
		return sb.toString();
	}


    private static final class Entry {
		private final String name;
		private final int line;
		private final Type type;
        private ArrayList<Range> ranges = new ArrayList<Range>();
		
		Entry(String name, int line, Type type,Range range) {
			this.name = name;
			this.line = line;
			this.type = type;
            this.ranges.add(range);
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