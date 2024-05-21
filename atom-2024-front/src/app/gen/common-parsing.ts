import {MetadataNodeType} from './parsing-enums';

export interface Metadata {
  fields: {[key: string] :MetadataField};
}

export interface MetadataField {
  label: string;
  nodeType: MetadataNodeType;
}

