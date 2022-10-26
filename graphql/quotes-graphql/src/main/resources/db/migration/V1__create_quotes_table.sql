CREATE TABLE fields(
   id INTEGER PRIMARY KEY,
   field VARCHAR(1024)
);

INSERT INTO fields (id,field) VALUES (0,'Unknown');
INSERT INTO fields (id,field) VALUES (1,'politics');
INSERT INTO fields (id,field) VALUES (2,'philosophy');
INSERT INTO fields (id,field) VALUES (3,'sports');
INSERT INTO fields (id,field) VALUES (4,'science');
INSERT INTO fields (id,field) VALUES (5,'acting');

CREATE TABLE authors(
    id INTEGER PRIMARY KEY,
    name VARCHAR(1024) NOT NULL,
    wikipedia_url VARCHAR(1024),
    field INTEGER REFERENCES fields (id)
);


INSERT INTO authors (id, name, field) VALUES
  (0,'Anonymous', 0);

INSERT INTO authors (id, name, field, wikipedia_url) VALUES
  (1,'Winston Churchill', 1, 'https://en.wikipedia.org/wiki/Winston_Churchill');

INSERT INTO authors (id, name, field, wikipedia_url) VALUES
  (2,'Marcus Tullius Cicero', 3, 'https://en.wikipedia.org/wiki/Cicero');

INSERT INTO authors (id, name, field, wikipedia_url) VALUES
  (3,'Vincent Lombardi', 2, 'https://en.wikipedia.org/wiki/Vince_Lombardi');

INSERT INTO authors (id, name, field, wikipedia_url) VALUES
  (4,'Lord Herbert', 1, 'https://en.wikipedia.org/wiki/Edward_Herbert,_1st_Baron_Herbert_of_Cherbury');

CREATE TABLE quotes(
   id INTEGER PRIMARY KEY,
   quote VARCHAR(1024),
   author INTEGER REFERENCES authors (id)
);

INSERT INTO quotes (id,quote,author) VALUES (1,'Never, never, never give up',1);
INSERT INTO quotes (id,quote,author) VALUES (2,'While there''s life, there''s hope',2);
INSERT INTO quotes (id,quote,author) VALUES (3,'Failure is success in progress',0);
INSERT INTO quotes (id,quote,author) VALUES (4,'Success demands singleness of purpose',3);
INSERT INTO quotes (id,quote,author) VALUES (5,'The shortest answer is doing',4);
