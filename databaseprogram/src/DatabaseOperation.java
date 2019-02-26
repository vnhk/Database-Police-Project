import java.sql.*;
import java.util.Scanner;

class DatabaseOperation {
    private static void drop(Statement stmt, String name, String type) {
        try {
            String query = "DROP " + type + " " + name;
            stmt.executeUpdate(query);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    private static void insertInto(Statement stmt,String query){
        try{
            stmt.executeUpdate(query);
        }catch (SQLException se){
            se.printStackTrace();
        }
    }
    static void ownQuery(Statement stmt,String instruction,int type){
        try{
            if(type == 0){
                ResultSet rs = stmt.executeQuery(instruction);
                ResultSetMetaData rsmd = rs.getMetaData();

                int columnsNumber = rsmd.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columnsNumber; i++) {
                        String name = rsmd.getColumnName(i);
                        System.out.println(name + ": " + rs.getString(i) + " ");
                        System.out.println("---------");
                    }
                }
            }
            else{
                stmt.executeUpdate(instruction);
            }
        }catch (SQLException se){
            se.printStackTrace();
        }
    }

    static void select(Statement stmt, String name) {
        String sql;
        sql = "select * from " + name;
        try {
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();

            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    name = rsmd.getColumnName(i);
                    System.out.println(name + ": " + rs.getString(i) + " ");
                    System.out.println("---------");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static void createAll(Statement stmt) {
        try {
            stmt.addBatch("CREATE TABLE Komenda(\n" +
                    "ID_Komendy NUMBER(5) CONSTRAINT ID_Komendy_PK PRIMARY KEY,\n" +
                    "Miejscowosc VARCHAR(40),\n" +
                    "Ulica VARCHAR(40),\n" +
                    "Nr_Lokalu VARCHAR(10))");
            stmt.addBatch("CREATE TABLE Osoba(\n" +
                    "ID_Osoby NUMBER(5) CONSTRAINT ID_Osoby_PK PRIMARY KEY,\n" +
                    "PESEL NUMBER(11) UNIQUE CONSTRAINT warunek CHECK (LENGTH(PESEL)=11),\n" +
                    "Imie VARCHAR(40),\n" +
                    "Nazwisko VARCHAR(40),\n" +
                    "Data_narodzin DATE)");
            stmt.addBatch("CREATE TABLE Radiowozy(\n" +
                    "ID_Radiowozu NUMBER(5) CONSTRAINT ID_Radiowozu_PK PRIMARY KEY,\n" +
                    "Tablica_Rej VARCHAR(12) UNIQUE,\n" +
                    "Marka VARCHAR(20),\n" +
                    "Model VARCHAR(20),\n" +
                    "Przebieg NUMBER(7))");
            stmt.addBatch("CREATE TABLE Policjanci(\n" +
                    "ID_Policjanta NUMBER(5) CONSTRAINT ID_Policjanta_PK PRIMARY KEY,\n" +
                    "ID_Osoby NUMBER(5) NOT NULL CONSTRAINT Policjanci_ID_OsobY_FK REFERENCES Osoba(ID_Osoby),\n" +
                    "ID_Komendy NUMBER(6) NOT NULL CONSTRAINT Policjanci_ID_Komendy_FK REFERENCES Komenda(ID_Komendy),\n" +
                    "ID_Radiowozu NUMBER(5) NOT NULL CONSTRAINT Policjanci_Radiowoz_FK REFERENCES Radiowozy(ID_Radiowozu),\n" +
                    "Wynagrodzenie NUMBER(6))");
            stmt.addBatch("CREATE TABLE Pracownicy_Adm(\n" +
                    "ID_Pracownika NUMBER(5) CONSTRAINT ID_Pracownika_PK PRIMARY KEY,\n" +
                    "ID_Osoby NUMBER(5) NOT NULL CONSTRAINT Pracownicy_Adm_Osoba_FK REFERENCES Osoba(ID_Osoby),\n" +
                    "ID_Komendy NUMBER(6) NOT NULL CONSTRAINT Pracownicy_Adm_ID_Komendy_FK REFERENCES Komenda(ID_Komendy),\n" +
                    "Wynagrodzenie NUMBER(6))");
            stmt.addBatch("CREATE TABLE Zglaszajacy(\n" +
                    "ID_Zglaszajacego NUMBER(5) CONSTRAINT ID_Zglaszajacego_PK PRIMARY KEY,\n" +
                    "ID_Osoby NUMBER(5) NOT NULL CONSTRAINT Zglaszajacy_Osoba_FK REFERENCES Osoba(ID_Osoby))");
            stmt.addBatch("CREATE TABLE Poszukiwani(\n" +
                    "ID_Poszukiwanego NUMBER(5) CONSTRAINT ID_Poszukiwanego_PK PRIMARY KEY,\n" +
                    "ID_Osoby NUMBER(5) NOT NULL CONSTRAINT Poszukiwani_PESEL_FK REFERENCES Osoba(ID_Osoby))");
            stmt.addBatch("CREATE TABLE Zgloszenia(\n" +
                    "ID_Zgloszenia NUMBER(5) CONSTRAINT ID_Zgloszenia_PK PRIMARY KEY,\n" +
                    "Data_Zgloszenia Date,\n" +
                    "ID_Zglaszajacego NUMBER(5) NOT NULL CONSTRAINT Zgloszenia_ID_Zglaszajacego_FK REFERENCES Zglaszajacy(ID_Zglaszajacego),\n" +
                    "ID_Komendy NUMBER(5) NOT NULL CONSTRAINT Zgloszenia_ID_Komendy_FK REFERENCES Komenda(ID_Komendy))");
            stmt.addBatch("CREATE TABLE ZgloszeniaPoszukiwani(\n" +
                    "ID NUMBER(5) CONSTRAINT ID_PK PRIMARY KEY,\n" +
                    "ID_Zgloszenia NUMBER(5) NOT NULL CONSTRAINT ZP_ID_Zgloszenia_FK REFERENCES Zgloszenia(ID_Zgloszenia),\n" +
                    "ID_Poszukiwanego NUMBER(5) NOT NULL CONSTRAINT ZP_ID_Poszukiwanego_FK REFERENCES Poszukiwani(ID_Poszukiwanego))");
            stmt.addBatch("CREATE TABLE Zatrzymani(\n" +
                    "ID_Zatrzymanego NUMBER(5) CONSTRAINT ID_Zatrzymanego_PK PRIMARY KEY,\n" +
                    "ID_Osoby NUMBER(5) NOT NULL CONSTRAINT Zatrzymani_PESEL_FK REFERENCES Osoba(ID_Osoby),\n" +
                    "Data_Zatrzymania Date)");
            stmt.addBatch("CREATE TABLE Wykroczenia(\n" +
                    "ID_Wykroczenia NUMBER(5) CONSTRAINT ID_Wykroczenia_PK PRIMARY KEY,\n" +
                    "Nazwa VARCHAR(40) NOT NULL UNIQUE)");
            stmt.addBatch("CREATE TABLE WykroczeniaZatrzymani(\n" +
                    "ID_WZ NUMBER(5) CONSTRAINT ID_WZ_PK PRIMARY KEY,\n" +
                    "ID_Wykroczenia NUMBER(5) NOT NULL CONSTRAINT ID_Wykroczenia_FK REFERENCES Wykroczenia(ID_Wykroczenia),\n" +
                    "ID_Zatrzymanego NUMBER(5) NOT NULL CONSTRAINT ID_Zatrzymanego_FK REFERENCES Zatrzymani(ID_Zatrzymanego))");
            stmt.addBatch("CREATE SEQUENCE komendaseq");
            stmt.addBatch("CREATE SEQUENCE osobaseq");
            stmt.addBatch("CREATE SEQUENCE radiowozyseq");
            stmt.addBatch("CREATE SEQUENCE policjanciseq");
            stmt.addBatch("CREATE SEQUENCE pracownicy_admseq");
            stmt.addBatch("CREATE SEQUENCE zglaszajacyseq");
            stmt.addBatch("CREATE SEQUENCE poszukiwaniseq");
            stmt.addBatch("CREATE SEQUENCE zgloszeniaseq");
            stmt.addBatch("CREATE SEQUENCE zgloszeniaposzukiwaniseq");
            stmt.addBatch("CREATE SEQUENCE zatrzymaniseq");
            stmt.addBatch("CREATE SEQUENCE wykroczeniaseq");
            stmt.addBatch("CREATE SEQUENCE wykroczeniazatrzymaniseq");

            stmt.executeBatch();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    static void drop(Statement stmt) {
        drop(stmt,"male_zarobki_trigger","TRIGGER");
        drop(stmt, "WykroczeniaZatrzymani", "TABLE");
        drop(stmt, "Zatrzymani", "TABLE");
        drop(stmt, "Wykroczenia", "TABLE");
        drop(stmt, "ZgloszeniaPoszukiwani", "TABLE");
        drop(stmt, "Zgloszenia", "TABLE");
        drop(stmt, "Poszukiwani", "TABLE");
        drop(stmt, "Zglaszajacy", "TABLE");
        drop(stmt, "Pracownicy_Adm", "TABLE");
        drop(stmt, "Policjanci", "TABLE");
        drop(stmt, "Radiowozy", "TABLE");
        drop(stmt, "Osoba", "TABLE");
        drop(stmt, "Komenda", "TABLE");
        drop(stmt, "komendaseq", "SEQUENCE");
        drop(stmt, "osobaseq", "SEQUENCE");
        drop(stmt, "radiowozyseq", "SEQUENCE");
        drop(stmt, "policjanciseq", "SEQUENCE");
        drop(stmt, "pracownicy_admseq", "SEQUENCE");
        drop(stmt, "zglaszajacyseq", "SEQUENCE");
        drop(stmt, "poszukiwaniseq", "SEQUENCE");
        drop(stmt, "zgloszeniaseq", "SEQUENCE");
        drop(stmt, "zgloszeniaposzukiwaniseq", "SEQUENCE");
        drop(stmt, "zatrzymaniseq", "SEQUENCE");
        drop(stmt, "wykroczeniaseq", "SEQUENCE");
        drop(stmt, "wykroczeniazatrzymaniseq", "SEQUENCE");
        drop(stmt,"dodaj_policjanta","PROCEDURE");
        drop(stmt,"wypisz_policjantow","PROCEDURE");
        drop(stmt,"zarobki","PROCEDURE");
        drop(stmt,"zamien_radiowoz_policjanta","PROCEDURE");
        drop(stmt,"podwyzka_dla_policjantek","PROCEDURE");
        drop(stmt,"wystaw_mandat","PROCEDURE");
        drop(stmt,"wypisz_mandaty","PROCEDURE");
    }

   static void insert(Statement stmt, String name) {
        try {
            ResultSet rs = stmt.executeQuery("Select * from " + name);
            ResultSetMetaData rsmd = rs.getMetaData();
            int size = rsmd.getColumnCount();
            System.out.println("Set values:");
            System.out.println("String values place in quotes ('').");
            System.out.println("Date values: TO_DATE('11-22-3333','DD-MM-YYYY') ");
            Scanner scan = new Scanner(System.in);
            StringBuilder sb = new StringBuilder(100);
            String query = "INSERT INTO "+name+" VALUES(";
            for(int i = 1; i <= size; i++) {
                System.out.println(rsmd.getColumnName(i)+" "+rsmd.getColumnTypeName(i)+":");
                if(i<size)
                    sb.append(scan.nextLine()).append(",");
                else
                    sb.append(scan.nextLine()).append(")");
            }
            query += sb.toString();
            System.out.println(query);
            insertInto(stmt,query);

        } catch (SQLException se) {
            se.printStackTrace();

        }
    }
    static void delete(Statement stmt, String name){
        try{
            stmt.executeUpdate("DELETE FROM "+name);
        }catch (SQLException se){
            se.printStackTrace();
        }
    }
    static void delete(Statement stmt){

            delete(stmt,"WykroczeniaZatrzymani");
            delete(stmt,"Wykroczenia");
            delete(stmt,"Zatrzymani");
            delete(stmt,"ZgloszeniaPoszukiwani");
            delete(stmt,"Zgloszenia");
            delete(stmt,"Poszukiwani");
            delete(stmt,"Zglaszajacy");
            delete(stmt,"Pracownicy_Adm");
            delete(stmt,"Policjanci");
            delete(stmt,"Radiowozy");
            delete(stmt,"Osoba");
            delete(stmt,"Komenda");
    }
    static void createProc(Statement stmt){
        String storage = "CREATE OR REPLACE PROCEDURE zarobki\n" +
                "AS\n" +
                "BEGIN\n" +
                "UPDATE POLICJANCI SET wynagrodzenie = wynagrodzenie*1.1\n" +
                "WHERE wynagrodzenie BETWEEN 3000 AND 4999;\n" +
                "UPDATE POLICJANCI SET wynagrodzenie = wynagrodzenie*1.2\n" +
                "WHERE wynagrodzenie BETWEEN 2100 AND 2999;\n" +
                "UPDATE POLICJANCI SET wynagrodzenie = wynagrodzenie*1.3\n" +
                "WHERE wynagrodzenie < 2100;\n" +
                "END;";
        String storage1 = "CREATE OR REPLACE PROCEDURE podwyzka_dla_policjantek\n" +
                "IS\n" +
                "podwyzka NUMBER(7);\n" +
                "CURSOR policjantki_cursor IS\n" +
                "SELECT OSOBA.IMIE,OSOBA.NAZWISKO, OSOBA.PESEL, POLICJANCI.WYNAGRODZENIE, POLICJANCI.ID_POLICJANTA\n" +
                "FROM OSOBA,POLICJANCI \n" +
                "WHERE OSOBA.IMIE \n" +
                "LIKE '%a'\n" +
                "AND OSOBA.ID_OSOBY = POLICJANCI.ID_OSOBY;\n" +
                "BEGIN\n" +
                "FOR it IN policjantki_cursor LOOP\n" +
                "podwyzka:=2500;\n" +
                "podwyzka:= it.wynagrodzenie +podwyzka;\n" +
                "UPDATE POLICJANCI SET wynagrodzenie = podwyzka\n" +
                "WHERE it.id_Policjanta = id_policjanta;\n" +
                "END LOOP;\n" +
                "END;";
        String storage2 = "CREATE OR REPLACE PROCEDURE wypisz_policjantow\n" +
                "IS CURSOR policjanci_cursor(id_k NUMBER) IS \n" +
                "SELECT o.imie,o.nazwisko,k.ulica FROM POLICJANCI p, KOMENDA k, OSOBA o\n" +
                "WHERE p.id_komendy = id_k AND id_k = k.id_komendy AND\n" +
                "p.id_osoby = o.id_osoby;\n" +
                "CURSOR komendy_cursor IS\n" +
                "SELECT id_komendy,miejscowosc\n" +
                "FROM KOMENDA;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.ENABLE;\n" +
                "DBMS_OUTPUT.PUT_LINE('Policjanci:');\n" +
                "FOR it IN komendy_cursor LOOP\n" +
                "DBMS_OUTPUT.PUT_LINE(' ');\n" +
                "FOR it2 IN policjanci_cursor(it.id_komendy) LOOP\n" +
                "DBMS_OUTPUT.PUT_LINE(it2.imie||' '||it2.nazwisko|| ' w '||it.miejscowosc||' - '||it2.ulica);\n" +
                "END LOOP;\n" +
                "END LOOp;\n" +
                "END;";
        String storage3 = "CREATE OR REPLACE PROCEDURE zamien_radiowoz_policjanta(p_ID_Policjanta NUMBER,p_ID_Radiowozu NUMBER)\n" +
                "AS\n" +
                "BEGIN\n" +
                "IF (p_ID_Policjanta is not NULL) \n" +
                "AND (p_ID_Radiowozu is not NULL)\n" +
                "THEN\n" +
                "UPDATE Policjanci SET ID_Radiowozu=p_ID_Radiowozu WHERE ID_Policjanta=p_ID_Policjanta;\n" +
                "END IF;\n"+
                "END;";
        String storage4 = "CREATE OR REPLACE PROCEDURE dodaj_policjanta(pesel NUMBER,imie VARCHAR2,nazwisko VARCHAR2,\n" +
                "data_narodzin DATE,komenda NUMBER,radiowoz NUMBER,wynagrodzenie NUMBER)\n" +
                "AS\n" +
                "BEGIN\n" +
                "INSERT INTO OSOBA VALUES(osobaseq.nextval,pesel,imie,nazwisko,data_narodzin);\n" +
                "INSERT INTO Policjanci VALUES(policjanciseq.nextval,osobaseq.currval,komenda,radiowoz,wynagrodzenie);\n" +
                "END;";
        String storage5 = "CREATE OR REPLACE PROCEDURE wystaw_mandat(p_ID NUMBER, dataz DATE,pnazwa VARCHAR2)\n" +
                "IS\n" +
                "amount NUMBER(1);\n" +
                "idnum NUMBER(5);\n" +
                "BEGIN\n" +
                "INSERT INTO ZATRZYMANI VALUES(zatrzymaniseq.nextval,p_ID,dataz);\n" +
                "SELECT COUNT(*) INTO amount FROM Wykroczenia WHERE nazwa = pnazwa;\n" +
                "if amount < 1 THEN\n" +
                "INSERT INTO WYKROCZENIA VALUES(wykroczeniaseq.nextval,pnazwa);\n" +
                "END IF;\n" +
                "SELECT id_wykroczenia INTO idnum FROM WYKROCZENIA WHERE nazwa = pnazwa;\n" +
                "INSERT INTO WYKROCZENIAZATRZYMANI VALUES(wykroczeniazatrzymaniseq.nextval,idnum,zatrzymaniseq.currval);\n" +
                "END;";
        String storage6 = "CREATE OR REPLACE PROCEDURE wypisz_mandaty(DATAP DATE, DATAK DATE)\n" +
                "IS CURSOR przestepstwa IS\n" +
                "SELECT o.imie,o.nazwisko,z.data_zatrzymania, w.nazwa \n" +
                "FROM Osoba o, Zatrzymani z, Wykroczenia w,WykroczeniaZatrzymani wz\n" +
                "WHERE z.id_osoby = o.id_osoby AND z.id_zatrzymanego = wz.id_zatrzymanego\n" +
                "AND wz.id_wykroczenia = w.id_wykroczenia AND z.data_zatrzymania >= DATAP AND z.data_zatrzymania <=DATAK;\n" +
                "BEGIN\n" +
                "DBMS_OUTPUT.ENABLE;\n" +
                "DBMS_OUTPUT.PUT_LINE('Osoby i wykroczenia:');\n" +
                "FOR tmp in przestepstwa LOOP\n" +
                "DBMS_OUTPUT.PUT_LINE(tmp.imie||' '||tmp.nazwisko||' zatrzymany/a '||tmp.data_zatrzymania||'('||tmp.nazwa||')');\n" +
                "END LOOP;\n" +
                "END;";
        String storage7 = "CREATE OR REPLACE TRIGGER MALE_ZAROBKI_TRIGGER\n" +
                "BEFORE INSERT ON POLICJANCI\n" +
                "FOR EACH ROW\n" +
                "BEGIN \n" +
                "IF(:NEW.WYNAGRODZENIE < 1500)\n" +
                "THEN\n" +
                "RAISE_APPLICATION_ERROR(-20001,'Wynagrodzenie mniejsze od placy minimalnej');\n" +
                "END IF;\n" +
                "END;";
        try{
            stmt.executeUpdate(storage);
            stmt.executeUpdate(storage1);
            stmt.executeUpdate(storage2);
            stmt.executeUpdate(storage3);
            stmt.executeUpdate(storage4);
            stmt.executeUpdate(storage5);
            stmt.executeUpdate(storage6);
            stmt.executeUpdate(storage7);
        }catch (SQLException se){
            se.printStackTrace();
        }

    }
}
