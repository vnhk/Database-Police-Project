import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) {
        String login;
        String password;

        if(args.length>1){
            login = args[0];
            password = args[1];
        }
        else {
            login = "system";
            password = "qwerty";
        }
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
         Connection con;


        try{
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }catch (Exception e){
        System.out.println(e.getMessage());
    }

        try {
            con = DriverManager.getConnection(url, login, password);

        }catch (SQLException e){
            e.printStackTrace();
            return;
        }
        int option = 1;
        Scanner scan = new Scanner(System.in);
        while(option>0&&option<9) {
            System.out.println("1.Create tables, triggers, procedures and sequences");
            System.out.println("2.Drop tables, triggers, procedures and sequences");
            System.out.println("3.Insert into...");
            System.out.println("4.Enter own instruction");
            System.out.println("5.Delete...");
            System.out.println("6.Select...");
            System.out.println("7.Functions and Procedures");
            System.out.println("ESLE EXIT:");
            option = scan.nextInt();
            scan.nextLine();

            switch (option) {
                case 1:
                    try {
                        System.out.println("Drop is required.");
                        Statement stmt = con.createStatement();
                        DatabaseOperation.createAll(stmt);
                        DatabaseOperation.createProc(stmt);
                        stmt.close();
                        System.out.println("Tables, procedures, triggers and sequences have been created.");
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 2:
                    try{
                        Statement stmt = con.createStatement();
                        DatabaseOperation.drop(stmt);
                        stmt.close();
                        System.out.println("Tables, procedures and sequences have been droped.");
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                    break;
                case 3:
                    try {
                        Statement stmt = con.createStatement();
                        System.out.println("Enter the name of the table:");
                        String name = scan.nextLine();
                        DatabaseOperation.insert(stmt,name);
                        stmt.close();
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 4:
                    try{
                        Statement stmt = con.createStatement();
                        System.out.println("1. Select");
                        System.out.println("2. Other");
                        if(scan.nextInt()==1) {
                            System.out.println("Enter a query:");
                            scan.nextLine();
                            DatabaseOperation.ownQuery(stmt,scan.nextLine(),0);
                        }
                        else{
                            System.out.println("Enter a query:");
                            scan.nextLine();
                            DatabaseOperation.ownQuery(stmt,scan.nextLine(),1);

                        }
                        stmt.close();
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                    break;

                case 5:
                    try{
                        Statement stmt = con.createStatement();
                        System.out.println("1. Delete All");
                        System.out.println("2. Delete from one table");
                        if(scan.nextInt()==1) {
                            DatabaseOperation.delete(stmt);
                        }
                        else{
                            System.out.println("Enter a name of a table:");
                            scan.nextLine();
                            DatabaseOperation.delete(stmt, scan.nextLine());

                        }
                        stmt.close();
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                    break;

                case 6:
                    try {
                        System.out.println("Enter a name of a table: ");
                        String name = scan.nextLine();
                        Statement stmt = con.createStatement();
                        DatabaseOperation.select(stmt,name);
                        scan.nextLine();

                        stmt.close();
                    } catch (Exception e) {
                        System.out.println(login);
                    }
                    break;
                case 7:
                    try {
                        System.out.println("1. Procedure zarobki");
                        System.out.println("2. Procedure podwyzka_dla_policjantek");
                        System.out.println("3. Procedure wypisz_policjantow");
                        System.out.println("4. Procedure zamien_radiowoz_policjanta");
                        System.out.println("5. Procedure dodaj_policjanta");
                        System.out.println("6. Procedure wystaw_mandat");
                        System.out.println("7. Procedure wypisz_mandat");
                        int x = scan.nextInt();
                        String proc;
                        scan.nextLine();
                        switch (x) {
                            case 1: {
                                proc = "{call zarobki()}";
                                CallableStatement cs = con.prepareCall(proc);
                                cs.execute();
                                break;
                            }
                            case 2: {
                                proc = "{call podwyzka_dla_policjantek()}";
                                CallableStatement cs = con.prepareCall(proc);
                                cs.execute();
                                break;
                            }
                            case 3: {
                                Statement stat = con.createStatement();
                                stat.executeUpdate("begin dbms_output.enable(); end;");
                                stat.executeUpdate("begin wypisz_policjantow(); end;");
                                try (CallableStatement call = con.prepareCall("declare "
                                        + "  num integer := 1000;"
                                        + "begin "
                                        + "  dbms_output.get_lines(?, num);"
                                        + "end;"
                                )) {
                                    call.registerOutParameter(1, Types.ARRAY,
                                            "DBMSOUTPUT_LINESARRAY");
                                    call.execute();

                                    Array array = null;
                                    try {
                                        array = call.getArray(1);
                                        Stream.of((Object[]) array.getArray())
                                                .forEach(System.out::println);
                                    } finally {
                                        if (array != null)
                                            array.free();
                                    }
                                }
                                break;
                            }
                            case 4: {
                                System.out.println("ID_Policjanta:");
                                int a = scan.nextInt();
                                System.out.println("ID_Radiowozu:");
                                int b = scan.nextInt();
                                scan.nextLine();
                                proc = "{call zamien_radiowoz_policjanta(?,?)}";
                                CallableStatement cs = con.prepareCall(proc);
                                cs.setInt(1,a);
                                cs.setInt(2,b);
                                cs.execute();
                                break;
                            }
                            case 5: {
                                try {
                                    System.out.println("Pesel:");
                                    BigDecimal a = scan.nextBigDecimal();
                                    scan.nextLine();
                                    System.out.println("Imie:");
                                    String b = scan.nextLine();
                                    System.out.println("Nazwisko:");
                                    String c = scan.nextLine();
                                    System.out.println("Komenda:");
                                    int d = scan.nextInt();
                                    System.out.println("Radiowoz:");
                                    int e = scan.nextInt();
                                    System.out.println("Wynagrodzenie:");
                                    long f = scan.nextLong();
                                    scan.nextLine();
                                    System.out.println("Data urodzin in YYYY-MM-DD format:");
                                    String g = scan.nextLine();
                                    proc = "{call dodaj_policjanta(?,?,?,?,?,?,?)}";
                                    CallableStatement cs = con.prepareCall(proc);
                                    cs.setBigDecimal(1, a);
                                    cs.setString(2, b);
                                    cs.setString(3, c);
                                    cs.setDate(4,java.sql.Date.valueOf(g));
                                    cs.setInt(5, d);
                                    cs.setInt(6, e);
                                    cs.setLong(7, f);
                                    cs.execute();
                                }catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                    break;

                            }
                            case 6:{
                                try{
                                    System.out.println("Podaj ID_OSOBY");
                                    int y = scan.nextInt();
                                    scan.nextLine();
                                    System.out.println("Podaj nazwe wykroczenia");
                                    String str = scan.nextLine();
                                    System.out.println("Podaj Date w YYYY-MM-DD");
                                    String data = scan.nextLine();
                                    proc = "{call wystaw_mandat(?,?,?)}";
                                    CallableStatement cs = con.prepareCall(proc);
                                    cs.setInt(1,y);
                                    cs.setDate(2,java.sql.Date.valueOf(data));
                                    cs.setString(3,str);
                                    cs.execute();

                                }catch (SQLException se){
                                    se.printStackTrace();
                                }
                                break;

                            }
                            case 7:{
                                try{
                                    Statement stat = con.createStatement();
                                    stat.executeUpdate("begin dbms_output.enable(); end;");
                                    System.out.println("Podaj date od jakiej rozpoczac szukanie");
                                    System.out.println("Podaj Date w YYYY-MM-DD");
                                    String datap = scan.nextLine();
                                    System.out.println("Podaj date do ktorej szukac");
                                    System.out.println("Podaj Date w YYYY-MM-DD");
                                    String dataz = scan.nextLine();
                                    proc = "{call wypisz_mandaty(?,?)}";
                                    CallableStatement cs = con.prepareCall(proc);
                                    cs.setDate(1,java.sql.Date.valueOf(datap));
                                    cs.setDate(2,java.sql.Date.valueOf(dataz));
                                    cs.execute();
                                }catch (SQLException se){
                                    se.printStackTrace();
                                }
                                try (CallableStatement call = con.prepareCall("declare "
                                        + "  num integer := 1000;"
                                        + "begin "
                                        + "  dbms_output.get_lines(?, num);"
                                        + "end;"
                                )) {
                                    call.registerOutParameter(1, Types.ARRAY,
                                            "DBMSOUTPUT_LINESARRAY");
                                    call.execute();

                                    Array array = null;
                                    try {
                                        array = call.getArray(1);
                                        Stream.of((Object[]) array.getArray())
                                                .forEach(System.out::println);
                                    } finally {
                                        if (array != null)
                                            array.free();
                                    }
                                }
                            }

                        }
                    }catch (SQLException se){
                        se.printStackTrace();
                    }
            }
        }
        try {
            con.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }

}

