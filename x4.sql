--kobiety pracujace w policji

SELECT O.IMIE,O.NAZWISKO,K.* 
FROM OSOBA O, KOMENDA K, POLICJANCI P
WHERE O.IMIE LIKE  '%a'
AND O.ID_OSOBY = P.ID_OSOBY
AND P.ID_KOMENDY = K.ID_KOMENDY;

--najmlodsza osoba w bazie
SELECT IMIE,NAZWISKO,DATA_NARODZIN
FROM OSOBA
WHERE DATA_NARODZIN = (SELECT MAX(DATA_NARODZIN) 
FROM OSOBA);

--radiowozy ktore maja wiekszy przebieg niz srednia i znajduja sie w kielcach lub krakowie
SELECT R.*,K.* 
FROM RADIOWOZY R,KOMENDA K,POLICJANCI P
WHERE PRZEBIEG > (SELECT AVG(PRZEBIEG) FROM RADIOWOZY)
AND R.ID_RADIOWOZU = P.ID_RADIOWOZU
AND K.ID_KOMENDY = P.ID_KOMENDY
AND K.MIEJSCOWOSC IN ('Kielce','Krakow');

--policjanci razem z radiowozami posortowane po marce 
SELECT O.IMIE,O.NAZWISKO,R.TABLICA_REJ,R.MARKA,R.MODEL, R.PRZEBIEG 
FROM OSOBA O NATURAL JOIN RADIOWOZY R NATURAL JOIN POLICJANCI P ORDER BY R.MARKA ASC;

--ilosc policjantow ktorzy jezdza danym radiowozem
SELECT COUNT(P.ID_POLICJANTA) AS ILOSC_KIEROWCOW,R.TABLICA_REJ,R.MARKA,R.MODEL
FROM RADIOWOZY R, POLICJANCI P
WHERE P.ID_RADIOWOZU = R.ID_RADIOWOZU
GROUP BY R.TABLICA_REJ,R.MARKA,R.MODEL ORDER BY ILOSC_KIEROWCOW DESC;

--widok zawierajacy dane o poszukiwanych posortowane po przestepstwie
CREATE OR REPLACE VIEW INFO_POSZUKIWANI AS
SELECT O.IMIE, O.NAZWISKO, TO_CHAR(O.DATA_NARODZIN,'DD-MM-YYYY') as Data_Narodzin,O.PESEL, WYKR.NAZWA AS WYKROCZENIE,TO_CHAR(ZATRZ.DATA_ZATRZYMANIA,'DD-MM-YYYY') AS Data_Zatrzymania 
FROM OSOBA O, WYKROCZENIA WYKR, WYKROCZENIAZATRZYMANI WK, ZATRZYMANI ZATRZ
WHERE WK.ID_WYKROCZENIA = WYKR.ID_WYKROCZENIA 
AND WK.ID_ZATRZYMANEGO = ZATRZ.ID_ZATRZYMANEGO
AND ZATRZ.ID_OSOBY = O.ID_OSOBY ORDER BY WYKR.NAZWA;

SELECT * FROM INFO_POSZUKIWANI;
--DROP VIEW info_poszukiwani;

--sumaryczne wydatki kazdej komendy na miesieczne wynagrodzenia policjantow 

SELECT K.ID_KOMENDY,K.MIEJSCOWOSC,K.ULICA,K.NR_LOKALU,SUM(POL.WYNAGRODZENIE) AS WYNAGRODZENIA_POLICJANTOW
FROM KOMENDA K, POLICJANCI POL
WHERE K.ID_KOMENDY = POL.ID_KOMENDY 
GROUP BY K.ID_KOMENDY,K.MIEJSCOWOSC, K.ULICA, K.NR_LOKALU
ORDER BY K.ID_KOMENDY ASC;

--imie i nazwisko policjantow ktorych zarobki sa wieksze od wszystkich zarobkow pracownikow cywilnych

SELECT O.IMIE|| ' ' ||O.NAZWISKO as Policjant,POL.WYNAGRODZENIE FROM OSOBA O, POLICJANCI POL
WHERE POL.ID_OSOBY = O.ID_OSOBY
AND POL.WYNAGRODZENIE > ALL(SELECT WYNAGRODZENIE FROM PRACOWNICY_ADM);

--Przestepcy zatrzymani w tym roku

SELECT O.IMIE,O.NAZWISKO,O.PESEL,Z.DATA_ZATRZYMANIA,W.NAZWA as Zatrzymany_Za FROM OSOBA O, ZATRZYMANI Z, WYKROCZENIA W, WYKROCZENIAZATRZYMANI WZ
WHERE EXTRACT(YEAR FROM Z.DATA_ZATRZYMANIA) = (SELECT EXTRACT (YEAR FROM SYSDATE) FROM DUAL)
AND O.ID_OSOBY = Z.ID_OSOBY
AND W.ID_WYKROCZENIA = WZ.ID_WYKROCZENIA
AND WZ.ID_ZATRZYMANEGO = Z.ID_ZATRZYMANEGO;

--najczesciej popelniane przestepstwa

SELECT W.NAZWA as Przestepstwo,COUNT(W.NAZWA) AS ILOSC 
FROM WYKROCZENIA W,WYKROCZENIAZATRZYMANI WK
WHERE W.ID_WYKROCZENIA = WK.ID_WYKROCZENIA
AND WK.ID_ZATRZYMANEGO IS NOT NULL 
HAVING (COUNT(W.NAZWA) > 
(SELECT AVG(COUNT(W.NAZWA)) 
FROM WYKROCZENIA W,WYKROCZENIAZATRZYMANI WK
WHERE W.ID_WYKROCZENIA = WK.ID_WYKROCZENIA
AND WK.ID_ZATRZYMANEGO 
IS NOT NULL 
GROUP BY W.NAZWA))
GROUP BY W.NAZWA;

--nigdy nie popelnione przestepstwa
SELECT W.NAZWA as Przestepstwo FROM WYKROCZENIA W, WYKROCZENIAZATRZYMANI WK
WHERE W.ID_WYKROCZENIA = WK.ID_WYKROCZENIA(+)
AND WK.ID_ZATRZYMANEGO IS NULL;
 
--przestepcy zatrzymani w latach 2000-2010

SELECT o.imie||' '|| o.nazwisko as Zatrzymani, TO_CHAR(zat.Data_Zatrzymania,'DD-MM-YYYY') as data_zatrzymania,wykr.Nazwa as Przestepstwo
FROM Osoba o, Zatrzymani zat, Wykroczenia wykr,WykroczeniaZatrzymani wz
WHERE o.id_osoby = zat.id_osoby
AND wz.id_zatrzymanego = zat.id_zatrzymanego
AND wz.id_wykroczenia = wykr.id_wykroczenia
AND zat.Data_Zatrzymania between TO_DATE('01-01-2000','DD-MM-YYYY')
AND TO_DATE('31-12-2010','DD-MM-YYYY');
 



