public class Conexion {
Connection cn;

public Connection conectar(){
try {
Class.forName("com.mysql.jdbc.Driver");
cn=(Connection) DriverManager.getConnection("jdbc:mysql://localhost/esrexx_wings","root",""); //login_java_mysql es el nombre de la base datos, el alumno debe modificar esta opción acorde a su base de datos en phpmyadmin.
System.out.println("CONECTADO");
} catch (Exception e) {
System.out.println("ERROR DE CONEXION BD"+e);
}
return cn;
}

}