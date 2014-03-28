# --- !Ups

CREATE TABLE USER (
  id               BIGINT(20)     NOT NULL AUTO_INCREMENT,
  name             TEXT           NOT NULL,

  PRIMARY KEY (id)
);

CREATE TABLE QUESTION (
  id               BIGINT(20)     NOT NULL AUTO_INCREMENT,
  id_user          BIGINT(20)     NOT NULL,
  text             TEXT           NOT NULL,
  created          DATETIME       NOT NULL,
  delay            BIGINT(20)     ,
  closed           DATETIME       ,

  PRIMARY KEY (id)
);

CREATE TABLE SUBMISSION (
    id            BIGINT(20)      NOT NULL AUTO_INCREMENT,
    id_question   BIGINT(20)      NOT NULL,
    id_user       BIGINT(20)      NOT NULL,
    release       DATETIME        NOT NULL,
    file_name     VARCHAR(255)    NOT NULL,
    comment       text            ,

    PRIMARY KEY (id),
    FOREIGN KEY (id_question) REFERENCES QUESTION(id)
    FOREIGN KEY (id_user)     REFERENCES USER(id)
);

# --- !Downs
DROP TABLE SUBMISSION;
DROP TABLE QUESTION;
DROP TABLE USER;