import {MetadataNodeType} from "../../../gen/parsing-enums";

export class Column {
 header: string;
 field: string;
 fieldGetter?: string;
 type?: string; // 'text' | 'date' | 'numeric' | 'boolean'
 width?: number;

  static getColumnType(value: MetadataNodeType): string {
    switch (value){
      case MetadataNodeType.NULL:
      case MetadataNodeType.STRING: return 'text';
      case MetadataNodeType.BOOLEAN: return 'boolean';
      case MetadataNodeType.NUMBER: return 'numeric';
      case MetadataNodeType.DATE:
      case MetadataNodeType.INSTANT: return 'date';
      default: return 'text';
    }
  }
}
