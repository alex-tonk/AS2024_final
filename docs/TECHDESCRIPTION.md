# Техническое описание системы

Система **ProLegacy** разработана в соответствии с трёхзвенной архитектурой:

- Слой клиента представляет собой веб-приложение, разработанное при помощи фреймворка **Angular 17**
- Слой логики представляет собой сервис, написанный на языке **Java 17** с использованием фреймворка **Spring Boot 3**
- Слой данных представляет собой СУБД PostgreSQL

Для каждого слоя были выбраны решения с открытым исходным кодом и свободной лицензией. Выбор также обусловлен
стремлением обеспечить максимальную кроссплатформенность системы. Так, для доступа к веб-приложению клиенту необходим
только веб-браузер, что позволяет использовать его практически на любом устройстве, включая смартфоны, планшеты и т.д.
Использование Java в качестве языка программирования позволяет развернуть сервис на любой платформе, поддерживающей
виртуальную машину Java.
Схема взаимодействия между слоями представлена ниже:

```mermaid
   flowchart TD
    db[("Слой данных (PostgreSQL)")]
    web["Слой клиента (Веб-приложение)"]
    back["Слой логики (Java-сервис)"]
    web <-- HTTP --> back
    back <-- SQL --> db
```

Веб-приложение взаимодействует с Java-сервисом посредством HTTP-протокола. Для доступа Java-сервиса к базе данных
PostreSQL используется SQL.

---

## Описание Java-сервиса

Слой логики представляет собой сервис, написанный на языке **Java 17** с использованием фреймворка **Spring Boot 3**.
Выбор Java обусловлен её кроссплатформенностью, а также тем, что вокруг Java сформировалось обширное сообщество
разработчиков и богатая экосистема инструментов и библиотек, что облегчает разработку и поддержку приложений. Spring
Boot является одним из наиболее популярных фреймворков для разработки приложений на Java. Он предоставляет множество
готовых компонентов для быстрой разработки приложений, включая поддержку микросервисной архитектуры, безопасности,
работу с базами данных и многое другое. Кроме того, Java и Spring Boot известны своей долгосрочной поддержкой, что важно
для корпоративных приложений, поскольку они обычно имеют длительный жизненный цикл и требуют регулярного обновления и
поддержки.

Вся бизнес-логика системы сосредоточена на уровне Java-сервиса. Взаимодействие с Java-сервисом осуществляется с помощью
REST API посредством протокола HTTP. Для взаимодействия с базой данных используется библиотека объектно-реляционного
преобразования Hibernate, которая позволяет работать с базой данных в терминах Java-классов, а не таблиц данных.
Hibernate представляет свой объектно-ориентированный язык запросов - HQL, на основе которого генерируется SQL запрос,
передаваемый непосредственно в базу данных. Для написания запросов к базе данных также используется библиотека QueryDSL,
упрощающая создание HQL-запросов и обработку их результатов.

Общая логика работы Java-сервиса представлена на следующей схеме.

```mermaid
  flowchart TD
    subgraph " "
        db[(Database)]
        Controller --> Service
        Service --> Repository
        Service --> Reader
        Repository --> Hibernate
        Reader --> QueryDSL
        QueryDSL --> Hibernate
        Hibernate --> db
    end
    HTTP --> Controller
```

Внешний запрос поступает по HTTP и обрабатывается классом типа "Controller". Для обработки запроса в соответствии с
бизнес-логикой Controller вызывает класс типа "Service". Service использует класс типа "Repository" для чтения и
изменения сущностей приложения, который в свою очередь генерирует HQL-запросы для их обработки Hibernate. Класс типа "
Reader" используется Service для чтения объектов возврата клиенту. Reader использует QueryDSL для генерации HQL-запросов
для их обработки Hibernate. Hibernate обеспечивает преобразование HQL-запросов в SQL-запросы и непосредственное
взаимодействие с базой данных.

---

## Описание веб-приложения

Клиентское веб-приложение представляется собой Single Page Application (SPA), которое разработано с помощью фреймворка *
*Angular 17**. Angular - это популярный фреймворк для разработки веб-приложений, разработанный и поддерживаемый командой
Google. Angular использует язык TypeScript и принцип компонентной архитектуры, что делает код более читаемым,
масштабируемым, а также обеспечивает модульность. Angular является проектом с открытым исходным кодом и имеет свободную
лицензию.

В качестве библиотеки компонентов пользовательского интерфейса используется библиотека PrimeNG, разработанная компанией
PrimeTek Informatics. Она предоставляет широкий спектр готовых компонентов, таких как таблицы данных, формы, кнопки,
меню, диалоговые окна и многое другое. В проекте используется версия PrimeNG Community Edition - проект с открытым
исходным кодом, распространяемый под свободной лицензией.

---

## Описание СУБД

Данная система работает с помощью СУБД PostgreSQL.

Структура данных разработана на основе ER-диаграммы представленной ниже.

```plantuml
@startuml

!theme plain
top to bottom direction
skinparam linetype ortho

class attachment {
   file_id: bigint
   message_id: bigint
   id: bigint
}
class attempt {
   auto_check_failed: boolean
   is_last_attempt: boolean
   is_new_try_allowed: boolean
   end_date: timestamp(6) with time zone
   lesson_id: bigint
   start_date: timestamp(6) with time zone
   task_id: bigint
   topic_id: bigint
   user_id: bigint
   auto_mark: varchar(255)
   status: varchar(255)
   tutor_comment: text
   tutor_mark: varchar(255)
   id: bigint
}
class attempt_auto_check_result {
   attempt_check_result_id: bigint
   attempt_id: bigint
}
class attempt_check_result {
   is_automatic: boolean
   x1: numeric(19,6)
   x2: numeric(19,6)
   y1: numeric(19,6)
   y2: numeric(19,6)
   file_id: bigint
   comment: text
   id: bigint
}
class attempt_check_results_features {
   attempt_check_result_id: bigint
   feature_id: bigint
}
class attempt_file {
   attempt_id: bigint
   file_id: bigint
   comment: text
   id: bigint
}
class attempt_tutor_check_result {
   attempt_check_result_id: bigint
   attempt_id: bigint
}
class chat {
   name: varchar(255)
   type: varchar(255)
   id: bigint
}
class chat_members {
   chat_id: bigint
   members_id: bigint
}
class feature {
   code: text
   name: text
   id: bigint
}
class file {
   uuid: uuid
   file_name: text
   id: bigint
}
class lesson {
   author: text
   code: text
   content: text
   title: text
   id: bigint
}
class lesson_supplement {
   lesson_id: bigint
   supplement_id: bigint
}
class lesson_task {
   lesson_id: bigint
   task_id: bigint
}
class lesson_trait {
   lesson_id: bigint
   trait_id: bigint
}
class message {
   author_id: bigint
   chat_id: bigint
   created_date: timestamp(6) with time zone
   content: varchar(4000)
   id: bigint
}
class message_read_by {
   message_id: bigint
   read_by_id: bigint
}
class notification {
   attempt_id: bigint
   type: varchar(255)
   id: bigint
}
class role {
   locale_name: varchar(255)
   name: varchar(255)
   id: bigint
}
class supplement {
   file_id: bigint
   title: text
   id: bigint
}
class task {
   difficulty: numeric(19,6)
   time: integer
   code: text
   content: text
   title: text
   id: bigint
}
class task_supplement {
   supplement_id: bigint
   task_id: bigint
}
class topic {
   code: text
   description: text
   title: text
   id: bigint
}
class topic_lesson {
   lesson_id: bigint
   topic_id: bigint
}
class topic_trait {
   topic_id: bigint
   trait_id: bigint
}
class trait {
   code: text
   description: text
   name: text
   id: bigint
}
class user {
   archived: boolean
   registration_date: timestamp(6) with time zone
   email: varchar(255)
   firstname: varchar(255)
   lastname: varchar(255)
   password: varchar(255)
   phone_number: varchar(255)
   surname: varchar(255)
   id: bigint
}
class user_role {
   role_id: bigint
   user_id: bigint
}

attachment                      -[#595959,plain]-^  file                           : "file_id:id"
attachment                      -[#595959,plain]-^  message                        : "message_id:id"
attempt                         -[#595959,plain]-^  lesson                         : "lesson_id:id"
attempt                         -[#595959,plain]-^  task                           : "task_id:id"
attempt                         -[#595959,plain]-^  topic                          : "topic_id:id"
attempt                         -[#595959,plain]-^  user                           : "user_id:id"
attempt_auto_check_result       -[#595959,plain]-^  attempt                        : "attempt_id:id"
attempt_auto_check_result       -[#595959,plain]-^  attempt_check_result           : "attempt_check_result_id:id"
attempt_check_result            -[#595959,plain]-^  file                           : "file_id:id"
attempt_check_results_features  -[#595959,plain]-^  attempt_check_result           : "attempt_check_result_id:id"
attempt_check_results_features  -[#595959,plain]-^  feature                        : "feature_id:id"
attempt_file                    -[#595959,plain]-^  attempt                        : "attempt_id:id"
attempt_file                    -[#595959,plain]-^  file                           : "file_id:id"
attempt_tutor_check_result      -[#595959,plain]-^  attempt                        : "attempt_id:id"
attempt_tutor_check_result      -[#595959,plain]-^  attempt_check_result           : "attempt_check_result_id:id"
chat_members                    -[#595959,plain]-^  chat                           : "chat_id:id"
chat_members                    -[#595959,plain]-^  user                           : "members_id:id"
lesson_supplement               -[#595959,plain]-^  lesson                         : "lesson_id:id"
lesson_supplement               -[#595959,plain]-^  supplement                     : "supplement_id:id"
lesson_task                     -[#595959,plain]-^  lesson                         : "lesson_id:id"
lesson_task                     -[#595959,plain]-^  task                           : "task_id:id"
lesson_trait                    -[#595959,plain]-^  lesson                         : "lesson_id:id"
lesson_trait                    -[#595959,plain]-^  trait                          : "trait_id:id"
message                         -[#595959,plain]-^  chat                           : "chat_id:id"
message                         -[#595959,plain]-^  user                           : "author_id:id"
message_read_by                 -[#595959,plain]-^  message                        : "message_id:id"
message_read_by                 -[#595959,plain]-^  user                           : "read_by_id:id"
notification                    -[#595959,plain]-^  attempt                        : "attempt_id:id"
supplement                      -[#595959,plain]-^  file                           : "file_id:id"
task_supplement                 -[#595959,plain]-^  supplement                     : "supplement_id:id"
task_supplement                 -[#595959,plain]-^  task                           : "task_id:id"
topic_lesson                    -[#595959,plain]-^  lesson                         : "lesson_id:id"
topic_lesson                    -[#595959,plain]-^  topic                          : "topic_id:id"
topic_trait                     -[#595959,plain]-^  topic                          : "topic_id:id"
topic_trait                     -[#595959,plain]-^  trait                          : "trait_id:id"
user_role                       -[#595959,plain]-^  role                           : "role_id:id"
user_role                       -[#595959,plain]-^  user                           : "user_id:id"
@enduml
```

---

## Масштабирование

Серверная часть приложения **ProLegacy** представляет собой Java-сервис, разработанный с помощью технологии Spring
Boot. Рекомендуемым способом развертывания Java-сервиса является развертывание в Docker-контейнере. Использование Docker
позволяет легко реализовать горизонтальное масштабирование путём развертывания дополнительных контейнеров. Таким
образом, в случае, если основной контейнер приложения испытывает значительную загрузку, можно просто развернуть
дополнительный контейнер, подключенный к той же базе данных, и перенаправить на него часть запросов. Для организации
доступа к нужному контейнеру предлагается использование nginx, который может выступать в качестве proxy.

```mermaid
  flowchart TD
    ng[[nginx proxy]]
    subgraph Docker
        id1[[Container 1]]
        id2[[Container 2]]
    end
    db[(Database)]
    ng --> id1
    ng --> id2
    id1 --> db
    id2 --> db
```

Такой подход, однако, имеет недостаток в виде необходимости полу-ручного управления процессами развертывания,
масштабирования и обновления. Более продвинутым подходом является использование технологий оркестрации Docker таких как
Kubernetes или Docker Swarm. При помощи этих технологий можно поднять несколько узлов c Docker и оркестрировать
контейнеры между узлами. Это позволит автоматизировать процессы управления контейнерами.

В случае, если значительную загрузку будет испытывать не Java-сервис, а база данных, то целесообразно использовать
технологии репликации. Можно распределить нагрузку, расположив инстансы базы данных на нескольких серверах. Один инстанс
будет использоваться для записи данных, а все прочие - для чтения. При изменении инстанса для записи, прочие инстансы (
реплики) будут своевременно синхронизированы. Если применения технологий репликации будет недостаточно, то имеет смысл
прибегнуть к технологиям шардирования.

По мере роста инфраструктуры и требований к производительности может потребоваться увеличение мощностей. Соответственно,
может потребоватьcя увеличение мощностей за счет увеличения ресурсов: процессоров, оперативной и постоянной памяти(SSD,
HDD).

---