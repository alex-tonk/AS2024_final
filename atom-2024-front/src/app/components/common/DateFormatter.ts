import {DatePipe} from "@angular/common";

export class DateFormatter {
  static format(date: Date | null) {
    if (!date) {
      return null;
    }
    const pipe = new DatePipe('en-US')
    return pipe.transform(date, 'dd.MM.yyyy HH:mm', 'UTC');
  }

  static getDateWithZone(date: Date): Date {
    return new Date(date.getTime() - date.getTimezoneOffset() * 60 * 1000);
  }
}
