SET SERVEROUTPUT ON;

/
CREATE OR REPLACE PROCEDURE dodaj_policjanta(pesel NUMBER,imie VARCHAR2,nazwisko VARCHAR2,
data_narodzin DATE,komenda NUMBER,radiowoz NUMBER,wynagrodzenie NUMBER)
AS
BEGIN
INSERT INTO OSOBA VALUES(osobaseq.nextval,pesel,imie,nazwisko,data_narodzin);
INSERT INTO Policjanci VALUES(policjanciseq.nextval,osobaseq.currval,komenda,radiowoz,wynagrodzenie);
END;
/
BEGIN
dodaj_policjanta(12345678011,'Marek','Grechuta',TO_DATE('23-05-1977','DD-MM-YYYY'),4,4,123);
END;
/
SELECT *FROM OSOBA;
SELECT *FROM Policjanci;

/
--2 modyfikacja komendy po podanym id
CREATE OR REPLACE PROCEDURE edytuj_komenda(p_id NUMBER,p_miejscowosc VARCHAR2,p_ulica VARCHAR2, p_nr_lokalu VARCHAR2)
AS
idEx EXCEPTION;
BEGIN
IF (p_id is NULL) THEN
RAISE idEx;
END IF;
IF p_miejscowosc IS NOT NULL
THEN
UPDATE KOMENDA SET miejscowosc = p_miejscowosc WHERE id_komendy = p_id;
END IF;
IF p_ulica IS NOT NULL
THEN
UPDATE KOMENDA SET ulica = p_ulica WHERE id_komendy = p_id;
END IF;
IF p_nr_lokalu IS NOT NULL
THEN
UPDATE KOMENDA SET nr_lokalu = p_nr_lokalu WHERE id_komendy = p_id;
END IF;
EXCEPTION
WHEN idEx THEN
DBMS_OUTPUT.ENABLE;
DBMS_OUTPUT.PUT_LINE('Nieprawidlowa wartosc podanego argumentu p_id');
END;
/
SELECT * FROM KOMENDA;
/
BEGIN
edytuj_komenda(null,'Gdansk',null,'C221');
END;
/


--3 procedura zwiekszajaca zarobki policjantow o 10%  jesli zarabiaja mniej niz 5000 o 20% jesli zarabiaja mniej niz 3000 i o 30%
--jesli zarabiaja ponizej 2100
CREATE OR REPLACE PROCEDURE zarobki
AS
BEGIN
UPDATE POLICJANCI SET wynagrodzenie = wynagrodzenie*1.1
WHERE wynagrodzenie BETWEEN 3000 AND 4999;
UPDATE POLICJANCI SET wynagrodzenie = wynagrodzenie*1.2
WHERE wynagrodzenie BETWEEN 2100 AND 2999;
UPDATE POLICJANCI SET wynagrodzenie = wynagrodzenie*1.3
WHERE wynagrodzenie < 2100;
END;
/
SELECT * FROM POLICJANci;
/
BEGIN
zarobki;
END;
/
--4 tworzenie kopi podanej tablicy
CREATE OR REPLACE PROCEDURE tworz_kopie_log(nazwa IN VARCHAR2, opcja char)
AS
BEGIN
IF(opcja  = 'K') THEN
EXECUTE IMMEDIATE 'CREATE TABLE '||nazwa||'_kopia AS SELECT * FROM '||nazwa;
END IF;
IF(opcja = 'L') THEN
EXECUTE IMMEDIATE 'CREATE TABLE '||nazwa||'_log(id_logu NUMBER(7) PRIMARY KEY,data_logu DATE,operacja VARCHAR(40))';
END IF;
END;
/
BEGIN
TWORZ_KOPIE_log('Osoba','K');
END;
/
DESC Policjanci_kopia;
DESC POLICJANCI_LOG;
DROP TABLE policjanci_kopia;
DROP TABLE policjanci_LOG;
--
/

--5 kobiety w policji pozwyzki
CREATE OR REPLACE PROCEDURE podwyzka_dla_policjantek
IS
podwyzka NUMBER(7);
CURSOR policjantki_cursor IS
SELECT OSOBA.IMIE,OSOBA.NAZWISKO, OSOBA.PESEL, POLICJANCI.WYNAGRODZENIE, POLICJANCI.ID_POLICJANTA
FROM OSOBA,POLICJANCI 
WHERE OSOBA.IMIE 
LIKE '%a'
AND OSOBA.ID_OSOBY = POLICJANCI.ID_OSOBY;
BEGIN
DBMS_OUTPUT.ENABLE;
DBMS_OUTPUT.PUT_LINE('Podwyzki otrzymaly:');
FOR it IN policjantki_cursor LOOP
podwyzka:= ROUND(DBMS_RANDOM.VALUE(1, 2000));
DBMS_OUTPUT.PUT_LINE(it.imie||' '||it.nazwisko||' - ('||it.pesel||'): '||podwyzka||' zl.');
podwyzka:= it.wynagrodzenie +podwyzka;
UPDATE POLICJANCI SET wynagrodzenie = podwyzka
WHERE it.id_Policjanta = id_policjanta;
DBMS_OUTPUT.PUT_LINE('Aktualne zarobki: '||podwyzka||' zl.');
END LOOP;
END;
/

EXECUTE podwyzka_dla_policjantek;
/

--6 wypisuje policjantow z komend
CREATE OR REPLACE PROCEDURE wypisz_policjantow
IS CURSOR policjanci_cursor(id_k NUMBER) IS 
SELECT o.imie,o.nazwisko,k.ulica FROM POLICJANCI p, KOMENDA k, OSOBA o
WHERE p.id_komendy = id_k AND id_k = k.id_komendy AND
p.id_osoby = o.id_osoby;
CURSOR komendy_cursor IS
SELECT id_komendy,miejscowosc
FROM KOMENDA;
BEGIN
DBMS_OUTPUT.ENABLE;
DBMS_OUTPUT.PUT_LINE('Policjanci:');
FOR it IN komendy_cursor LOOP
DBMS_OUTPUT.PUT_LINE(' ');
FOR it2 IN policjanci_cursor(it.id_komendy) LOOP
DBMS_OUTPUT.PUT_LINE(it2.imie||' '||it2.nazwisko|| ' w '||it.miejscowosc||' - '||it2.ulica);
END LOOP;
END LOOp;
END;
/
BEGIN
wypisz_policjantow;
END;
/

--7

CREATE OR REPLACE PROCEDURE zamien_radiowoz_policjanta(p_ID_Policjanta NUMBER,p_ID_Radiowozu NUMBER)
AS
idEx EXCEPTION;
BEGIN
IF (p_ID_Policjanta is NULL) 
OR (p_ID_Radiowozu is NULL)
THEN
RAISE idEx;
END IF;
UPDATE Policjanci SET ID_Radiowozu=p_ID_Radiowozu WHERE ID_Policjanta=p_ID_Policjanta;
EXCEPTION
WHEN idEx THEN
DBMS_OUTPUT.ENABLE;
DBMS_OUTPUT.PUT_LINE('Nieprawidlowa wartosc podanego argumentu okreœlaj¹cego obiekt do zamiany');
END;
/
BEGIN
zamien_radiowoz_policjanta(2,8);
END;
/

SELECT * FROM POLICJANCI;