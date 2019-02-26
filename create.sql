CREATE TABLE Komenda(
ID_Komendy NUMBER(5) CONSTRAINT ID_Komendy_PK PRIMARY KEY,
Miejscowosc VARCHAR(40),
Ulica VARCHAR(40),
Nr_Lokalu VARCHAR(10));

CREATE TABLE Osoba(
ID_Osoby NUMBER(5) CONSTRAINT ID_Osoby_PK PRIMARY KEY,
PESEL NUMBER(11) UNIQUE CONSTRAINT warunek CHECK (LENGTH(PESEL)=11),
Imie VARCHAR(40),
Nazwisko VARCHAR(40),
Data_narodzin DATE);

CREATE TABLE Radiowozy(
ID_Radiowozu NUMBER(5) CONSTRAINT ID_Radiowozu_PK PRIMARY KEY,
Tablica_Rej VARCHAR(12) UNIQUE,
Marka VARCHAR(20),
Model VARCHAR(20),
Przebieg NUMBER(7));

CREATE TABLE Policjanci(
ID_Policjanta NUMBER(5) CONSTRAINT ID_Policjanta_PK PRIMARY KEY,
ID_Osoby NUMBER(5) NOT NULL CONSTRAINT Policjanci_ID_OsobY_FK REFERENCES Osoba(ID_Osoby),
ID_Komendy NUMBER(6) NOT NULL CONSTRAINT Policjanci_ID_Komendy_FK REFERENCES Komenda(ID_Komendy),
ID_Radiowozu NUMBER(5) NOT NULL CONSTRAINT Policjanci_Radiowoz_FK REFERENCES Radiowozy(ID_Radiowozu),
Wynagrodzenie NUMBER(6));

CREATE TABLE Pracownicy_Adm(
ID_Pracownika NUMBER(5) CONSTRAINT ID_Pracownika_PK PRIMARY KEY,
ID_Osoby NUMBER(5) NOT NULL CONSTRAINT Pracownicy_Adm_Osoba_FK REFERENCES Osoba(ID_Osoby),
ID_Komendy NUMBER(6) NOT NULL CONSTRAINT Pracownicy_Adm_ID_Komendy_FK REFERENCES Komenda(ID_Komendy),
Wynagrodzenie NUMBER(6));

CREATE TABLE Zglaszajacy(
ID_Zglaszajacego NUMBER(5) CONSTRAINT ID_Zglaszajacego_PK PRIMARY KEY,
ID_Osoby NUMBER(5) NOT NULL CONSTRAINT Zglaszajacy_Osoba_FK REFERENCES Osoba(ID_Osoby));

CREATE TABLE Poszukiwani(
ID_Poszukiwanego NUMBER(5) CONSTRAINT ID_Poszukiwanego_PK PRIMARY KEY,
ID_Osoby NUMBER(5) NOT NULL CONSTRAINT Poszukiwani_PESEL_FK REFERENCES Osoba(ID_Osoby));

CREATE TABLE Zgloszenia(
ID_Zgloszenia NUMBER(5) CONSTRAINT ID_Zgloszenia_PK PRIMARY KEY,
Data_Zgloszenia Date,
ID_Zglaszajacego NUMBER(5) NOT NULL CONSTRAINT Zgloszenia_ID_Zglaszajacego_FK REFERENCES Zglaszajacy(ID_Zglaszajacego),
ID_Komendy NUMBER(5) NOT NULL CONSTRAINT Zgloszenia_ID_Komendy_FK REFERENCES Komenda(ID_Komendy));

CREATE TABLE ZgloszeniaPoszukiwani(
ID NUMBER(5) CONSTRAINT ID_PK PRIMARY KEY,
ID_Zgloszenia NUMBER(5) NOT NULL CONSTRAINT ZP_ID_Zgloszenia_FK REFERENCES Zgloszenia(ID_Zgloszenia),
ID_Poszukiwanego NUMBER(5) NOT NULL CONSTRAINT ZP_ID_Poszukiwanego_FK REFERENCES Poszukiwani(ID_Poszukiwanego));

CREATE TABLE Zatrzymani(
ID_Zatrzymanego NUMBER(5) CONSTRAINT ID_Zatrzymanego_PK PRIMARY KEY,
ID_Osoby NUMBER(5) NOT NULL CONSTRAINT Zatrzymani_PESEL_FK REFERENCES Osoba(ID_Osoby),
Data_Zatrzymania Date);

CREATE TABLE Wykroczenia(
ID_Wykroczenia NUMBER(5) CONSTRAINT ID_Wykroczenia_PK PRIMARY KEY,
Nazwa VARCHAR(40) NOT NULL UNIQUE);

CREATE TABLE WykroczeniaZatrzymani(
ID_WZ NUMBER(5) CONSTRAINT ID_WZ_PK PRIMARY KEY,
ID_Wykroczenia NUMBER(5) NOT NULL CONSTRAINT ID_Wykroczenia_FK REFERENCES Wykroczenia(ID_Wykroczenia),
ID_Zatrzymanego NUMBER(5) NOT NULL CONSTRAINT ID_Zatrzymanego_FK REFERENCES Zatrzymani(ID_Zatrzymanego));



