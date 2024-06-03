import {Column} from '../components/common/table/Column';

export function getField(row: any, col: Column): any {
  const path = (col.fieldGetter ?? col.field).split('.');
  return path.reduce((p, c) => p[c], row);
}
