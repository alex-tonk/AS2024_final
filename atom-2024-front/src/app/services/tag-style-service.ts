import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TagStyleService {
  defaultStyle = {padding: '0.6rem', fontSize: '15px', fontWeight: '600', color: 'black', minWidth: 'fit-content'}

  getStatusStyle(statusLocale: string) {
    switch (statusLocale) {
      case 'Не пройдено':
        return {...this.defaultStyle, backgroundColor: 'var(--border-color)'};
      case 'Не прочитано':
        return {...this.defaultStyle, backgroundColor: 'var(--border-color)'};
      case 'Взято в работу':
        return {...this.defaultStyle, backgroundColor: 'var(--border-color)'};
      case 'Отправлено на проверку':
        return {...this.defaultStyle, backgroundColor: 'rgba(0,159,227,0.75)'};
      case 'Проверено':
        return {...this.defaultStyle, backgroundColor: 'var(--green-color)'};
      case 'Прочитано':
        return {...this.defaultStyle, backgroundColor: 'var(--green-color)'};
      default:
        return this.defaultStyle;
    }
  }

  getAutoStatusStyle(autoStatus: string) {
    switch (autoStatus) {
      case 'Пропущено':
        return {...this.defaultStyle, backgroundColor: 'var(--border-color)'};
      case 'В обработке':
        return {...this.defaultStyle, backgroundColor: 'rgba(0,159,227,0.75)'};
      case 'Ошибка':
        return {...this.defaultStyle, backgroundColor: 'var(--red-color)'};
      case 'Проверено':
        return {...this.defaultStyle, backgroundColor: 'var(--green-color)'};
      default:
        return this.defaultStyle;
    }
  }

  getMarkStyle(markLocale: string) {
    switch (markLocale) {
      case 'Отлично':
        return {...this.defaultStyle, backgroundColor: 'var(--green-color)'};
      case 'Хорошо':
        return {...this.defaultStyle, backgroundColor: '#ede636'};
      case 'Удовлетворительно':
        return {...this.defaultStyle, backgroundColor: 'orange'};
      case 'Неудовлетворительно':
        return {...this.defaultStyle, backgroundColor: 'var(--red-color)'};
      default:
        return this.defaultStyle;
    }
  }

  getDifficultyStyle(difficultyLocale: string) {
    switch (difficultyLocale) {
      case 'Новичок':
        return {...this.defaultStyle, backgroundColor: '#fbf0b6'};
      case 'Ученик':
        return {...this.defaultStyle, backgroundColor: '#faea64'};
      case 'Профессионал':
        return {...this.defaultStyle, backgroundColor: '#f09f35'};
      case 'Эксперт':
        return {...this.defaultStyle, backgroundColor: '#f1aa2b'};
      case 'Мастер':
        return {...this.defaultStyle, backgroundColor: '#e8783c'};
      default:
        return this.defaultStyle;
    }
  }
}
