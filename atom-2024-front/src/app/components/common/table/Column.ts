
export class Column {
 header: string;
 field: string;
 fieldGetter?: string;
 type?: string; // 'text' | 'date' | 'numeric' | 'boolean'
 width?: number;

  static getColumnType(value: string): string {
    switch (value){
      case 'NULL':
      case 'STRING': return 'text';
      case 'BOOLEAN': return 'boolean';
      case 'NUMBER': return 'numeric';
      case 'DATE':
      case 'INSTANT': return 'date';
      default: return 'text';
    }
  }
}
